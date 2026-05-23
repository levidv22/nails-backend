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
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ReservationRepository reservationRepository,
            UserRepository userRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Review createReview(Review review) {

        if (review.getReservation() == null || review.getReservation().getId() == null) {
            throw new RuntimeException("La reserva es obligatoria para crear una reseña");
        }
        Reservation reservation = reservationRepository.findById(
                review.getReservation().getId()
        ).orElseThrow(() ->
                new RuntimeException("Reserva no encontrada")
        );
        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new RuntimeException(
                    "Solo puedes reseñar reservas completadas"
            );
        }
        if (reservation.getId() != null) {
            boolean exists = reviewRepository.findAll()
                    .stream()
                    .anyMatch(r ->
                            r.getReservation().getId().equals(reservation.getId())
                    );
            if (exists) {
                throw new RuntimeException("Esta reserva ya tiene una reseña");
            }
        }
        review.setId(null);
        review.setReservation(reservation);
        review.setClient(reservation.getClient());
        review.setCreatedAt(LocalDateTime.now());
        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            throw new RuntimeException("La calificación debe estar entre 1 y 5");
        }
        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getPublicReviews() {
        return reviewRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    public void deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Reseña no encontrada")
                );

        reviewRepository.delete(review);
    }
}