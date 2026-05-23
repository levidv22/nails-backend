package upeu.edu.pe.nails.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.services.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping(path = "/api/schedules")
public class ScheduleController {

    private final WeeklyScheduleService weeklyScheduleService;
    private final ScheduleExceptionService scheduleExceptionService;

    public ScheduleController(WeeklyScheduleService weeklyScheduleService,
                              ScheduleExceptionService scheduleExceptionService) {
        this.weeklyScheduleService = weeklyScheduleService;
        this.scheduleExceptionService = scheduleExceptionService;
    }

    @PostMapping(path = "/weekly")
    public ResponseEntity<WeeklySchedule> createWeeklySchedule(
            @RequestBody WeeklySchedule weeklySchedule
    ) {
        return ResponseEntity.ok(
                weeklyScheduleService.createWeeklySchedule(weeklySchedule)
        );
    }

    @PutMapping(path = "/weekly/{id}")
    public ResponseEntity<WeeklySchedule> updateWeeklySchedule(
            @PathVariable Long id,
            @RequestBody WeeklySchedule weeklySchedule
    ) {
        return ResponseEntity.ok(
                weeklyScheduleService.updateWeeklySchedule(id, weeklySchedule)
        );
    }

    @DeleteMapping(path = "/weekly/{id}")
    public ResponseEntity<Void> disableWeeklySchedule(
            @PathVariable Long id
    ) {
        weeklyScheduleService.disableWeeklySchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/weekly")
    public ResponseEntity<List<WeeklySchedule>> getByDay(
            @RequestParam DayOfWeek dayOfWeek
    ) {
        return ResponseEntity.ok(
                weeklyScheduleService.getSchedulesByDay(dayOfWeek)
        );
    }

    @PostMapping(path = "/exceptions/block")
    public ResponseEntity<ScheduleException> createBlockException(
            @RequestBody ScheduleException scheduleException
    ) {
        return ResponseEntity.ok(
                scheduleExceptionService.createBlockException(scheduleException)
        );
    }

    @PostMapping(path = "/exceptions/available")
    public ResponseEntity<ScheduleException> createCustomAvailableException(
            @RequestBody ScheduleException scheduleException
    ) {
        return ResponseEntity.ok(
                scheduleExceptionService.createCustomAvailableException(scheduleException)
        );
    }

    @DeleteMapping(path = "/exceptions/{id}")
    public ResponseEntity<Void> deleteException(
            @PathVariable Long id
    ) {
        scheduleExceptionService.removeException(id);
        return ResponseEntity.noContent().build();
    }
}
