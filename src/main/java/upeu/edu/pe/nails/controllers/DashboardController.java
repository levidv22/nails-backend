package upeu.edu.pe.nails.controllers;

import org.springframework.web.bind.annotation.*;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.services.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/dashboard")
public class DashboardController {

    private final ReservationService reservationService;
    private final ServiService serviService;

    public DashboardController(
            ReservationService reservationService,
            ServiService serviService
    ) {
        this.reservationService = reservationService;
        this.serviService = serviService;
    }

    @GetMapping(path = "/summary")
    public Map<String, Object> getSummary() {

        List<Servi> activeServices = serviService.getAllActiveServices();

        List<Reservation> pendingReservations =
                reservationService.getPendingReservations();

        List<Reservation> allReservations =
                getAllReservations();

        long totalReservations = allReservations.size();

        long completedReservations = allReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .count();

        long totalClients = getAllClients().size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("activeServices", activeServices.size());
        summary.put("pendingReservations", pendingReservations.size());
        summary.put("totalReservations", totalReservations);
        summary.put("completedReservations", completedReservations);
        summary.put("totalClients", totalClients);

        return summary;
    }

    @GetMapping(path = "/today-reservations")
    public List<Reservation> getTodayReservations() {

        LocalDate today = LocalDate.now();

        return getAllReservations().stream()
                .filter(r -> r.getReservationDate().equals(today))
                .toList();
    }

    @GetMapping(path = "/income")
    public Map<String, Object> getIncome() {

        List<Reservation> completedReservations =
                getAllReservations().stream()
                        .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                        .toList();

        BigDecimal totalIncome = completedReservations.stream()
                .map(Reservation::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> response = new HashMap<>();
        response.put("totalIncome", totalIncome);
        response.put("completedReservations", completedReservations.size());

        return response;
    }

    @GetMapping(path = "/top-services")
    public List<Map<String, Object>> getTopServices() {

        List<Servi> services = serviService.getAllActiveServices();

        List<Reservation> reservations = getAllReservations();

        return services.stream()
                .map(service -> {

                    long count = reservations.stream()
                            .filter(r ->
                                    r.getServi().getId().equals(service.getId())
                            )
                            .count();

                    Map<String, Object> data = new HashMap<>();
                    data.put("serviceId", service.getId());
                    data.put("name", service.getName());
                    data.put("totalReservations", count);

                    return data;
                })
                .sorted((a, b) -> Long.compare(
                        (Long) b.get("totalReservations"),
                        (Long) a.get("totalReservations")
                ))
                .toList();
    }

    /**
     * Simulación: debería existir en ReservationServiceImpl
     * Ej: reservationRepository.findAll()
     */
    private List<Reservation> getAllReservations() {
        throw new UnsupportedOperationException(
                "Implementar reservationService.getAllReservations()"
        );
    }

    /**
     * Simulación: debería existir en UserServiceImpl
     * Ej: userRepository.findByRole(CLIENT)
     */
    private List<User> getAllClients() {
        throw new UnsupportedOperationException(
                "Implementar userService.getAllClients()"
        );
    }
}
