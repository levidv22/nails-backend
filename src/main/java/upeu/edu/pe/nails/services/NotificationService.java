package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.Reservation;

public interface NotificationService {

    void sendReservationApproved(Reservation reservation);
    void sendReservationRejected(Reservation reservation);
    void sendReservationCancelled(Reservation reservation);
    void sendReminderNotification(Reservation reservation);
}
