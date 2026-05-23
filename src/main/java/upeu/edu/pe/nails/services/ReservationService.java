package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.Reservation;

import java.util.List;

public interface ReservationService {

    Reservation createReservation(Reservation reservation);
    Reservation approveReservation(Long reservationId, Long adminId);
    Reservation rejectReservation(Long reservationId, String reason, Long adminId);
    Reservation cancelReservation(Long reservationId, String reason, Long adminId);
    Reservation completeReservation(Long reservationId, Long adminId);
    List<Reservation> getPendingReservations();
    List<Reservation> getReservationsByClient(Long clientId);
}