package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.*;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.*;
import upeu.edu.pe.nails.services.*;

import java.time.*;
import java.util.List;

@Service
@Transactional
public class ScheduleExceptionServiceImpl implements ScheduleExceptionService {

    private final ScheduleExceptionRepository scheduleExceptionRepository;

    public ScheduleExceptionServiceImpl(ScheduleExceptionRepository scheduleExceptionRepository
    ) {

        this.scheduleExceptionRepository = scheduleExceptionRepository;
    }

    @Override
    public ScheduleException createBlockException(
            ScheduleException scheduleException
    ) {
        validateAdmin(scheduleException);
        validateDate(scheduleException.getDate());
        validateTimeRange(
                scheduleException.getStartTime(),
                scheduleException.getEndTime()
        );
        validateExceptionConflict(
                null,
                scheduleException.getDate(),
                scheduleException.getStartTime(),
                scheduleException.getEndTime(),
                scheduleException.getAdmin().getId()
        );
        scheduleException.setId(null);

        scheduleException.setType(
                ExceptionType.BLOCK
        );
        if (scheduleException.getReason() == null
                || scheduleException.getReason().isBlank()) {

            scheduleException.setReason(
                    "Bloqueo manual del administrador"
            );
        }
        return scheduleExceptionRepository.save(
                scheduleException
        );
    }

    @Override
    public ScheduleException
    createCustomAvailableException(
            ScheduleException scheduleException
    ) {
        validateAdmin(scheduleException);
        validateDate(scheduleException.getDate());
        validateTimeRange(
                scheduleException.getStartTime(),
                scheduleException.getEndTime()
        );
        validateExceptionConflict(
                null,
                scheduleException.getDate(),
                scheduleException.getStartTime(),
                scheduleException.getEndTime(),
                scheduleException.getAdmin().getId()
        );
        scheduleException.setId(null);
        scheduleException.setType(
                ExceptionType.CUSTOM_AVAILABLE
        );
        if (scheduleException.getReason() == null
                || scheduleException.getReason().isBlank()) {

            scheduleException.setReason(
                    "Horario personalizado"
            );
        }

        return scheduleExceptionRepository.save(
                scheduleException
        );
    }

    @Override
    public void removeException(Long exceptionId) {

        ScheduleException scheduleException =
                scheduleExceptionRepository.findById(exceptionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Excepción no encontrada"
                                )
                        );

        scheduleExceptionRepository.delete(
                scheduleException
        );
    }

    private void validateAdmin(
            ScheduleException scheduleException
    ) {

        if (scheduleException.getAdmin() == null) {

            throw new RuntimeException(
                    "El administrador es obligatorio"
            );
        }
    }

    private void validateDate(
            LocalDate date
    ) {

        if (date == null) {

            throw new RuntimeException(
                    "La fecha es obligatoria"
            );
        }
        if (date.isBefore(LocalDate.now())) {

            throw new RuntimeException(
                    "No se puede crear excepciones en fechas pasadas"
            );
        }
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
                    "La hora inicial debe ser menor a la final"
            );
        }
    }

    private void validateExceptionConflict(
            Long currentExceptionId,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            Long adminId
    ) {

        List<ScheduleException> exceptions =
                scheduleExceptionRepository
                        .findByDate(date);

        for (ScheduleException existing : exceptions) {
            if (!existing.getAdmin()
                    .getId()
                    .equals(adminId)) {

                continue;
            }
            if (currentExceptionId != null
                    && existing.getId()
                    .equals(currentExceptionId)) {

                continue;
            }
            boolean overlaps =
                    startTime.isBefore(
                            existing.getEndTime()
                    )
                            &&
                            endTime.isAfter(
                                    existing.getStartTime()
                            );

            if (overlaps) {

                throw new RuntimeException(
                        "Existe conflicto con otra excepción en la misma fecha"
                );
            }
        }
    }

}