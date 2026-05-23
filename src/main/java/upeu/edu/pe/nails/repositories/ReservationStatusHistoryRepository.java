package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.*;

import java.util.List;

public interface ReservationStatusHistoryRepository extends JpaRepository<ReservationStatusHistory, Long> {

    List<ReservationStatusHistory> findByReservationOrderByChangedAtDesc(Reservation reservation);
}