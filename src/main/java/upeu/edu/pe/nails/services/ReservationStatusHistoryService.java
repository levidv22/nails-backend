package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.*;

import java.util.List;

public interface ReservationStatusHistoryService {

    void saveHistory(Reservation reservation, ReservationStatus oldStatus, ReservationStatus newStatus, String reason, User changedBy);
    List<ReservationStatusHistory> getReservationHistory(Long reservationId);
}