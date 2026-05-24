package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.nails.entities.Reservation;
import upeu.edu.pe.nails.services.*;

import java.time.*;
import java.util.List;

@RestController
@RequestMapping(path = "/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;

    public AvailabilityController(AvailabilityService availabilityService,
                                  ReservationService reservationService) {
        this.availabilityService = availabilityService;
        this.reservationService = reservationService;
    }

    @GetMapping(path = "/slots")
    public ResponseEntity<List<String>> getAvailableSlots(
            @RequestParam LocalDate date,
            @RequestParam Long serviceId
    ) {

        List<LocalTime> slots =
                availabilityService.generateAvailableSlots(date, serviceId);

        List<String> response = slots.stream()
                .map(LocalTime::toString)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/validate")
    public ResponseEntity<Boolean> validateSlot(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime
    ) {

        boolean available =
                availabilityService.isSlotAvailable(date, startTime, endTime);

        return ResponseEntity.ok(available);
    }

//    @GetMapping(path = "/occupied")
//    public ResponseEntity<List<Reservation>> getOccupiedReservations(
//            @RequestParam LocalDate date
//    ) {
//
//        List<Reservation> reservations =
//                reservationService.getReservationsByClient(date != null ? 0L : null);
//        return ResponseEntity.ok(reservations);
//    }
}
