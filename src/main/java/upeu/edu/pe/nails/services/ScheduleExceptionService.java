package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.ScheduleException;

public interface ScheduleExceptionService {

    ScheduleException createBlockException(ScheduleException scheduleException);
    ScheduleException createCustomAvailableException(ScheduleException scheduleException);
    void removeException(Long exceptionId);
}