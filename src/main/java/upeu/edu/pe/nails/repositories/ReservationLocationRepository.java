package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.*;

import java.util.Optional;

public interface ReservationLocationRepository extends JpaRepository<ReservationLocation, Long> {

    Optional<ReservationLocation> findByReservation(Reservation reservation);
}
