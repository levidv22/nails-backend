package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.*;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.WeeklyScheduleRepository;
import upeu.edu.pe.nails.services.WeeklyScheduleService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class WeeklyScheduleServiceImpl implements WeeklyScheduleService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    public WeeklyScheduleServiceImpl(WeeklyScheduleRepository weeklyScheduleRepository
    ) {
        this.weeklyScheduleRepository = weeklyScheduleRepository;
    }

    @Override
    public WeeklySchedule createWeeklySchedule(
            WeeklySchedule weeklySchedule
    ) {
        if (weeklySchedule.getAdmin() == null) {

            throw new RuntimeException(
                    "El administrador es obligatorio"
            );
        }
        if (weeklySchedule.getDayOfWeek() == null) {

            throw new RuntimeException(
                    "El día es obligatorio"
            );
        }
        validateTimeRange(
                weeklySchedule.getStartTime(),
                weeklySchedule.getEndTime()
        );
        validateScheduleConflict(
                null,
                weeklySchedule.getAdmin(),
                weeklySchedule.getDayOfWeek(),
                weeklySchedule.getStartTime(),
                weeklySchedule.getEndTime()
        );
        weeklySchedule.setId(null);
        weeklySchedule.setActive(true);
        return weeklyScheduleRepository.save(
                weeklySchedule
        );
    }

    @Override
    public WeeklySchedule updateWeeklySchedule(
            Long scheduleId,
            WeeklySchedule updatedSchedule
    ) {

        WeeklySchedule existingSchedule =
                weeklyScheduleRepository.findById(scheduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Horario no encontrado"
                                )
                        );
        if (updatedSchedule.getDayOfWeek() == null) {

            throw new RuntimeException(
                    "El día es obligatorio"
            );
        }
        validateTimeRange(
                updatedSchedule.getStartTime(),
                updatedSchedule.getEndTime()
        );
        validateScheduleConflict(
                existingSchedule.getId(),
                existingSchedule.getAdmin(),
                updatedSchedule.getDayOfWeek(),
                updatedSchedule.getStartTime(),
                updatedSchedule.getEndTime()
        );
        existingSchedule.setDayOfWeek(
                updatedSchedule.getDayOfWeek()
        );
        existingSchedule.setStartTime(
                updatedSchedule.getStartTime()
        );
        existingSchedule.setEndTime(
                updatedSchedule.getEndTime()
        );
        return weeklyScheduleRepository.save(
                existingSchedule
        );
    }

    @Override
    public void disableWeeklySchedule(
            Long scheduleId
    ) {

        WeeklySchedule weeklySchedule =
                weeklyScheduleRepository.findById(scheduleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Horario no encontrado"
                                )
                        );
        weeklySchedule.setActive(false);
        weeklyScheduleRepository.save(
                weeklySchedule
        );
    }

    @Override
    public List<WeeklySchedule> getSchedulesByDay(
            DayOfWeek dayOfWeek
    ) {

        return weeklyScheduleRepository
                .findByDayOfWeekAndActiveTrue(
                        dayOfWeek
                );
    }

    private void validateTimeRange(
            LocalTime startTime,
            LocalTime endTime
    ) {
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
    }

    private void validateScheduleConflict(
            Long currentScheduleId,
            User admin,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime
    ) {
        List<WeeklySchedule> schedules =
                weeklyScheduleRepository
                        .findByDayOfWeekAndActiveTrue(
                                dayOfWeek
                        );

        for (WeeklySchedule schedule : schedules) {
            if (!schedule.getAdmin()
                    .getId()
                    .equals(admin.getId())) {

                continue;
            }
            if (currentScheduleId != null
                    && schedule.getId()
                    .equals(currentScheduleId)) {

                continue;
            }
            boolean overlaps =
                    startTime.isBefore(schedule.getEndTime())
                            &&
                            endTime.isAfter(schedule.getStartTime());
            if (overlaps) {

                throw new RuntimeException(
                        "Existe conflicto con otro horario del mismo día"
                );
            }
        }
    }

}