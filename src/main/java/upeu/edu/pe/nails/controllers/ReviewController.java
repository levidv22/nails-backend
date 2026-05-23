package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.services.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ReservationService reservationService;
    private final FileStorageService fileStorageService;

    public ReviewController(
            ReviewService reviewService,
            ReservationService reservationService,
            FileStorageService fileStorageService
    ) {
        this.reviewService = reviewService;
        this.reservationService = reservationService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<Review> createReview(
            @RequestBody Review review
    ) {
        return ResponseEntity.ok(
                reviewService.createReview(review)
        );
    }

    @GetMapping(path = "/public")
    public ResponseEntity<List<Review>> getPublicReviews() {
        return ResponseEntity.ok(
                reviewService.getPublicReviews()
        );
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/upload-image")
    public ResponseEntity<String> uploadImage(
            @RequestParam MultipartFile file
    ) {
        String path = fileStorageService.uploadImage(file);
        return ResponseEntity.ok(path);
    }

    @GetMapping(path = "/can-review/{reservationId}")
    public ResponseEntity<Boolean> canReview(
            @PathVariable Long reservationId
    ) {
        Reservation reservation = reservationService
                .getReservationsByClient(reservationId)
                .stream()
                .filter(r -> r.getId().equals(reservationId))
                .findFirst()
                .orElse(null);

        boolean canReview = reservation != null &&
                reservation.getStatus() == ReservationStatus.COMPLETED;

        return ResponseEntity.ok(canReview);
    }
}
