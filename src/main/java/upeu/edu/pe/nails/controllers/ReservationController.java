package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.services.*;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping(path = "/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final AvailabilityService availabilityService;
    private final ReservationStatusHistoryService historyService;
    private final FileStorageService fileStorageService;

    public ReservationController(
            ReservationService reservationService,
            AvailabilityService availabilityService,
            LoyaltyService loyaltyService,
            NotificationService notificationService,
            ReservationStatusHistoryService historyService,
            FileStorageService fileStorageService
    ) {
        this.reservationService = reservationService;
        this.availabilityService = availabilityService;
        this.historyService = historyService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<Reservation> create(
            @RequestBody Reservation reservation
    ) {
        return ResponseEntity.ok(
                reservationService.createReservation(reservation)
        );
    }

    @PutMapping(path = "/approve/{id}")
    public ResponseEntity<Reservation> approve(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        return ResponseEntity.ok(
                reservationService.approveReservation(id, adminId)
        );
    }

    @PutMapping(path = "/reject/{id}")
    public ResponseEntity<Reservation> reject(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(
                reservationService.rejectReservation(id, reason, adminId)
        );
    }

    @PutMapping(path = "/cancel/{id}")
    public ResponseEntity<Reservation> cancel(
            @PathVariable Long id,
            @RequestParam Long adminId,
            @RequestParam String reason
    ) {
        return ResponseEntity.ok(
                reservationService.cancelReservation(id, reason, adminId)
        );
    }

    @PutMapping(path = "/complete/{id}")
    public ResponseEntity<Reservation> complete(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        return ResponseEntity.ok(
                reservationService.completeReservation(id, adminId)
        );
    }

    @GetMapping(path = "/pending")
    public ResponseEntity<List<Reservation>> getPending() {
        return ResponseEntity.ok(
                reservationService.getPendingReservations()
        );
    }

    @GetMapping(path = "/client/{id}")
    public ResponseEntity<List<Reservation>> getByClient(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                reservationService.getReservationsByClient(id)
        );
    }

    @GetMapping(path = "/history/{reservationId}")
    public ResponseEntity<List<ReservationStatusHistory>> getHistory(
            @PathVariable Long reservationId
    ) {
        return ResponseEntity.ok(
                historyService.getReservationHistory(reservationId)
        );
    }

    @GetMapping(path = "/slots")
    public ResponseEntity<List<LocalTime>> getSlots(
            @RequestParam LocalDate date,
            @RequestParam Long serviceId
    ) {
        return ResponseEntity.ok(
                availabilityService.generateAvailableSlots(date, serviceId)
        );
    }

    @PostMapping(path = "/upload-image")
    public ResponseEntity<String> uploadImage(
            @RequestParam MultipartFile file
    ) {
        String path = fileStorageService.uploadImage(file);
        return ResponseEntity.ok(path);
    }

    @DeleteMapping(path = "/delete-image")
    public ResponseEntity<Void> deleteImage(
            @RequestParam String path
    ) {
        fileStorageService.deleteImage(path);
        return ResponseEntity.ok().build();
    }
}