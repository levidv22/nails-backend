package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.repositories.*;
import upeu.edu.pe.nails.services.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationStatusHistoryServiceImpl implements ReservationStatusHistoryService {

    private final ReservationStatusHistoryRepository historyRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReservationStatusHistoryServiceImpl(
            ReservationStatusHistoryRepository historyRepository,
            ReservationRepository reservationRepository,
            UserRepository userRepository
    ) {
        this.historyRepository = historyRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveHistory(
            Reservation reservation,
            ReservationStatus oldStatus,
            ReservationStatus newStatus,
            String reason,
            User changedBy
    ) {
        if (reservation == null || reservation.getId() == null) {
            throw new RuntimeException("Reserva inválida");
        }
        if (changedBy == null || changedBy.getId() == null) {
            throw new RuntimeException("Usuario que realiza el cambio inválido");
        }
        ReservationStatusHistory history = new ReservationStatusHistory();
        history.setReservation(reservation);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setReason(reason);
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    @Override
    public List<ReservationStatusHistory> getReservationHistory(Long reservationId) {

        if (reservationId == null) {
            throw new RuntimeException("ID de reserva inválido");
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() ->
                        new RuntimeException("Reserva no encontrada")
                );

        return historyRepository.findByReservationOrderByChangedAtDesc(reservation);
    }
}
