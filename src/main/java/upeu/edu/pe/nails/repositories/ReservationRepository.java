package upeu.edu.pe.nails.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;
import upeu.edu.pe.nails.entities.*;

import java.time.*;
import java.util.*;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByClient(User client);
    List<Reservation> findByReservationDate(LocalDate reservationDate);
    List<Reservation> findByStatusOrderByCreatedAtDesc(
            ReservationStatus status
    );
    List<Reservation> findByReservationDateAndStatus(
            LocalDate date,
            ReservationStatus status
    );
    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.reservationDate = :date
        AND r.status IN (
            upeu.edu.pe.nails.entities.ReservationStatus.PENDING,
            upeu.edu.pe.nails.entities.ReservationStatus.CONFIRMED
        )
        AND (
            :startTime < r.endTime
            AND :endTime > r.startTime
        )
    """)
    List<Reservation> findConflictingReservations(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r
        FROM Reservation r
        WHERE r.reservationDate = :date
    """)
    List<Reservation> lockReservationsByDate(
            @Param("date") LocalDate date
    );
}