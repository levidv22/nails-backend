package upeu.edu.pe.nails.services;

import java.time.*;
import java.util.*;

public interface AvailabilityService {

    List<LocalTime> generateAvailableSlots(LocalDate date, Long serviId);
    boolean isSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime);
    void validateReservationTime(LocalDate date, LocalTime startTime, LocalTime endTime);
    Map<String, Object> getAvailabilityByDate(LocalDate date);
}
