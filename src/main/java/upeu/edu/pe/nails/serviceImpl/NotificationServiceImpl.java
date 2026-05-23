package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.NotificationRepository;
import upeu.edu.pe.nails.services.NotificationService;

import java.time.LocalDateTime;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendReservationApproved(Reservation reservation) {
        User client = reservation.getClient();
        String message = "Tu reserva ha sido CONFIRMADA ✅. " +
                "Fecha: " + reservation.getReservationDate() +
                " Hora: " + reservation.getStartTime() +
                ". ¡Te esperamos!";
        saveNotification(client,
                NotificationType.WHATSAPP,
                "Reserva Confirmada",
                message);
    }

    @Override
    public void sendReservationRejected(Reservation reservation) {
        User client = reservation.getClient();
        String message = "Tu reserva ha sido RECHAZADA ❌. " +
                "Motivo: " + reservation.getRejectionReason();
        saveNotification(client,
                NotificationType.WHATSAPP,
                "Reserva Rechazada",
                message);
    }

    @Override
    public void sendReservationCancelled(Reservation reservation) {
        User client = reservation.getClient();
        String message = "Tu reserva ha sido CANCELADA ⚠️. " +
                "Motivo: " + reservation.getCancellationReason();
        saveNotification(client,
                NotificationType.WHATSAPP,
                "Reserva Cancelada",
                message);
    }

    @Override
    public void sendReminderNotification(Reservation reservation) {
        User client = reservation.getClient();
        String message = "Recordatorio ⏰: Tienes una cita programada para el " +
                reservation.getReservationDate() +
                " a las " + reservation.getStartTime() +
                ".";
        saveNotification(client,
                NotificationType.WHATSAPP,
                "Recordatorio de Cita",
                message);
    }

    private void saveNotification(
            User user,
            NotificationType type,
            String title,
            String message
    ) {

        Notification notification = new Notification();

        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setSent(true);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }
}
