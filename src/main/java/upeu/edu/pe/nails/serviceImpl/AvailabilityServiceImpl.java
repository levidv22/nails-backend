package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.*;
import upeu.edu.pe.nails.services.AvailabilityService;

import java.time.*;
import java.util.*;

@Service
@Transactional
public class AvailabilityServiceImpl
        implements AvailabilityService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;
    private final ReservationRepository reservationRepository;
    private final ServiRepository serviRepository;

    public AvailabilityServiceImpl(
            WeeklyScheduleRepository weeklyScheduleRepository,
            ScheduleExceptionRepository scheduleExceptionRepository,
            ReservationRepository reservationRepository,
            ServiRepository serviRepository
    ) {

        this.weeklyScheduleRepository = weeklyScheduleRepository;
        this.scheduleExceptionRepository = scheduleExceptionRepository;
        this.reservationRepository = reservationRepository;
        this.serviRepository = serviRepository;
    }

    @Override
    public List<LocalTime> generateAvailableSlots(
            LocalDate date,
            Long serviId
    ) {
        if (date == null) {

            throw new RuntimeException(
                    "La fecha es obligatoria"
            );
        }
        Servi servi = serviRepository
                .findById(serviId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Servicio no encontrado"
                        )
                );
        int durationMinutes =
                servi.getDurationMinutes();
        List<LocalTime> availableSlots =
                new ArrayList<>();
        List<TimeRange> availableRanges =
                getAvailableRanges(date);
        for (TimeRange range : availableRanges) {

            LocalTime current =
                    range.startTime();

            while (
                    !current.plusMinutes(durationMinutes)
                            .isAfter(range.endTime())
            ) {

                LocalTime endTime =
                        current.plusMinutes(
                                durationMinutes
                        );
                boolean available =
                        isSlotAvailable(
                                date,
                                current,
                                endTime
                        );

                if (available) {

                    availableSlots.add(current);
                }
                current = current.plusMinutes(30);
            }
        }
        availableSlots.sort(
                Comparator.naturalOrder()
        );

        return availableSlots;
    }

    @Override
    public boolean isSlotAvailable(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        try {

            validateReservationTime(
                    date,
                    startTime,
                    endTime
            );

        } catch (Exception e) {

            return false;
        }
        List<Reservation> reservations =
                reservationRepository
                        .findByReservationDate(date);

        for (Reservation reservation
                : reservations) {
            boolean activeReservation =
                    reservation.getStatus()
                            == ReservationStatus.PENDING
                            ||
                            reservation.getStatus()
                                    == ReservationStatus.CONFIRMED;

            if (!activeReservation) {

                continue;
            }
            boolean overlaps =
                    startTime.isBefore(
                            reservation.getEndTime()
                    )
                            &&
                            endTime.isAfter(
                                    reservation.getStartTime()
                            );

            if (overlaps) {

                return false;
            }
        }

        return true;
    }

    @Override
    public void validateReservationTime(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {
        if (date == null) {

            throw new RuntimeException(
                    "La fecha es obligatoria"
            );
        }
        if (startTime == null
                || endTime == null) {

            throw new RuntimeException(
                    "Las horas son obligatorias"
            );
        }
        if (!startTime.isBefore(endTime)) {

            throw new RuntimeException(
                    "La hora de inicio debe ser menor a la hora final"
            );
        }
        if (date.isBefore(LocalDate.now())) {

            throw new RuntimeException(
                    "No se puede reservar fechas pasadas"
            );
        }
        List<TimeRange> availableRanges =
                getAvailableRanges(date);
        boolean valid = false;

        for (TimeRange range : availableRanges) {

            boolean insideRange =
                    (
                            startTime.equals(
                                    range.startTime()
                            )
                                    ||
                                    startTime.isAfter(
                                            range.startTime()
                                    )
                    )
                            &&
                            (
                                    endTime.equals(
                                            range.endTime()
                                    )
                                            ||
                                            endTime.isBefore(
                                                    range.endTime()
                                            )
                            );

            if (insideRange) {

                valid = true;
                break;
            }
        }

        if (!valid) {

            throw new RuntimeException(
                    "El horario está fuera del horario disponible"
            );
        }
    }

    private List<TimeRange> getAvailableRanges(
            LocalDate date
    ) {
        List<TimeRange> ranges =
                new ArrayList<>();
        List<ScheduleException> exceptions =
                scheduleExceptionRepository
                        .findByDate(date);
        boolean hasCustomAvailable =
                exceptions.stream()
                        .anyMatch(exception ->
                                exception.getType()
                                        == ExceptionType.CUSTOM_AVAILABLE
                        );
        if (hasCustomAvailable) {

            for (ScheduleException exception
                    : exceptions) {

                if (exception.getType()
                        == ExceptionType.CUSTOM_AVAILABLE) {

                    ranges.add(
                            new TimeRange(
                                    exception.getStartTime(),
                                    exception.getEndTime()
                            )
                    );
                }
            }

            return ranges;
        }
        DayOfWeek dayOfWeek =
                date.getDayOfWeek();

        List<WeeklySchedule> schedules =
                weeklyScheduleRepository
                        .findByDayOfWeekAndActiveTrue(
                                dayOfWeek
                        );

        for (WeeklySchedule schedule
                : schedules) {

            ranges.add(
                    new TimeRange(
                            schedule.getStartTime(),
                            schedule.getEndTime()
                    )
            );
        }
        List<TimeRange> blockedRanges =
                new ArrayList<>();

        for (ScheduleException exception
                : exceptions) {

            if (exception.getType()
                    == ExceptionType.BLOCK) {

                blockedRanges.add(
                        new TimeRange(
                                exception.getStartTime(),
                                exception.getEndTime()
                        )
                );
            }
        }
        return subtractBlockedRanges(
                ranges,
                blockedRanges
        );
    }

    private List<TimeRange> subtractBlockedRanges(
            List<TimeRange> availableRanges,
            List<TimeRange> blockedRanges
    ) {

        List<TimeRange> result =
                new ArrayList<>();

        for (TimeRange available
                : availableRanges) {

            List<TimeRange> currentRanges =
                    new ArrayList<>();

            currentRanges.add(available);

            for (TimeRange blocked
                    : blockedRanges) {

                List<TimeRange> updatedRanges =
                        new ArrayList<>();

                for (TimeRange current
                        : currentRanges) {

                    boolean overlaps =
                            blocked.startTime()
                                    .isBefore(current.endTime())
                                    &&
                                    blocked.endTime()
                                            .isAfter(current.startTime());
                    if (!overlaps) {

                        updatedRanges.add(current);
                        continue;
                    }
                    if (blocked.startTime()
                            .isAfter(current.startTime())) {

                        updatedRanges.add(
                                new TimeRange(
                                        current.startTime(),
                                        blocked.startTime()
                                )
                        );
                    }
                    if (blocked.endTime()
                            .isBefore(current.endTime())) {

                        updatedRanges.add(
                                new TimeRange(
                                        blocked.endTime(),
                                        current.endTime()
                                )
                        );
                    }
                }

                currentRanges = updatedRanges;
            }

            result.addAll(currentRanges);
        }

        return result;
    }

    private record TimeRange(
            LocalTime startTime,
            LocalTime endTime
    ) {
    }

}