package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.nails.services.*;

import java.time.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(
            AvailabilityService availabilityService
    ) {
        this.availabilityService = availabilityService;
    }

    // HORARIOS DISPONIBLES
    @GetMapping(path = "/slots")
    public ResponseEntity<Map<String, Object>> getAvailability(
            @RequestParam LocalDate date
    ) {

        Map<String, Object> response =
                availabilityService.getAvailabilityByDate(date);

        return ResponseEntity.ok(response);
    }
}
