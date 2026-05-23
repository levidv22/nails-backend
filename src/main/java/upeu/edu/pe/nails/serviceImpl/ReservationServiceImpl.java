package upeu.edu.pe.nails.serviceImpl;

import jakarta.transaction.*;
import org.springframework.stereotype.Service;
import upeu.edu.pe.nails.entities.*;
import upeu.edu.pe.nails.entities.User;
import upeu.edu.pe.nails.repositories.*;
import upeu.edu.pe.nails.services.*;

import java.math.*;
import java.time.*;
import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ServiRepository serviRepository;
    private final UserRepository userRepository;
    private final AvailabilityService availabilityService;
    private final LoyaltyService loyaltyService;
    private final NotificationService notificationService;
    private final ReservationStatusHistoryService historyService;
    private final ReservationLocationRepository reservationLocationRepository;
    private final ReservationReferenceImageRepository referenceImageRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            ServiRepository serviRepository,
            UserRepository userRepository,
            AvailabilityService availabilityService,
            LoyaltyService loyaltyService,
            NotificationService notificationService,
            ReservationStatusHistoryService historyService,
            ReservationLocationRepository reservationLocationRepository,
            ReservationReferenceImageRepository referenceImageRepository
    ) {

        this.reservationRepository = reservationRepository;
        this.serviRepository = serviRepository;
        this.userRepository = userRepository;
        this.availabilityService = availabilityService;
        this.loyaltyService = loyaltyService;
        this.notificationService = notificationService;
        this.historyService = historyService;
        this.reservationLocationRepository = reservationLocationRepository;
        this.referenceImageRepository = referenceImageRepository;
    }

    @Override
    public Reservation createReservation(
            Reservation reservation
    ) {
        if (reservation.getClient() == null
                || reservation.getClient().getId() == null) {

            throw new RuntimeException(
                    "El cliente es obligatorio"
            );
        }
        if (reservation.getServi() == null
                || reservation.getServi().getId() == null) {

            throw new RuntimeException(
                    "El servicio es obligatorio"
            );
        }
        User client = userRepository.findById(
                        reservation.getClient().getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cliente no encontrado"
                        )
                );
        Servi servi = serviRepository.findById(
                        reservation.getServi().getId()
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Servicio no encontrado"
                        )
                );
        LocalTime endTime =
                reservation.getStartTime()
                        .plusMinutes(
                                servi.getDurationMinutes()
                        );
        availabilityService.validateReservationTime(
                reservation.getReservationDate(),
                reservation.getStartTime(),
                endTime
        );
        reservationRepository.lockReservationsByDate(
                reservation.getReservationDate()
        );
        List<Reservation> conflicts =
                reservationRepository
                        .findConflictingReservations(
                                reservation.getReservationDate(),
                                reservation.getStartTime(),
                                endTime
                        );
        if (!conflicts.isEmpty()) {

            throw new RuntimeException(
                    "El horario ya está ocupado"
            );
        }
        BigDecimal basePrice =
                servi.getBasePrice();

        BigDecimal discountAmount = loyaltyService.calculateDiscount(client, basePrice);
        BigDecimal finalPrice = basePrice.subtract(discountAmount);
        reservation.setId(null);
        reservation.setClient(client);
        reservation.setServi(servi);
        reservation.setEndTime(endTime);
        reservation.setStatus(
                ReservationStatus.PENDING
        );

        reservation.setBasePrice(basePrice);
        reservation.setDiscountAmount(
                discountAmount
        );

        reservation.setFinalPrice(
                finalPrice
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
        );

        reservation.setDiscountApplied(
                discountAmount.compareTo(
                        BigDecimal.ZERO
                ) > 0
        );

        reservation.setCreatedAt(
                LocalDateTime.now()
        );
        Reservation savedReservation =
                reservationRepository.save(
                        reservation
                );
        historyService.saveHistory(
                savedReservation,
                null,
                ReservationStatus.PENDING,
                "Reserva creada",
                client
        );

        return savedReservation;
    }

    @Override
    public Reservation approveReservation(
            Long reservationId,
            Long adminId
    ) {

        Reservation reservation =
                getReservationOrThrow(
                        reservationId
                );
        User admin =
                getAdminOrThrow(adminId);
        if (reservation.getStatus()
                != ReservationStatus.PENDING) {

            throw new RuntimeException(
                    "Solo reservas pendientes pueden aprobarse"
            );
        }
        reservationRepository.lockReservationsByDate(
                reservation.getReservationDate()
        );
        List<Reservation> conflicts =
                reservationRepository
                        .findConflictingReservations(
                                reservation.getReservationDate(),
                                reservation.getStartTime(),
                                reservation.getEndTime()
                        );
        conflicts = conflicts.stream()
                .filter(conflict ->
                        !conflict.getId().equals(
                                reservation.getId()
                        )
                )
                .toList();

        if (!conflicts.isEmpty()) {

            throw new RuntimeException(
                    "Existe conflicto con otra reserva"
            );
        }
        ReservationStatus oldStatus =
                reservation.getStatus();

        reservation.setStatus(
                ReservationStatus.CONFIRMED
        );
        Reservation updatedReservation =
                reservationRepository.save(
                        reservation
                );
        historyService.saveHistory(
                updatedReservation,
                oldStatus,
                ReservationStatus.CONFIRMED,
                "Reserva aprobada",
                admin
        );
        notificationService
                .sendReservationApproved(
                        updatedReservation
                );

        return updatedReservation;
    }

    @Override
    public Reservation rejectReservation(
            Long reservationId,
            String reason,
            Long adminId
    ) {
        if (reason == null
                || reason.isBlank()) {

            throw new RuntimeException(
                    "El motivo es obligatorio"
            );
        }
        Reservation reservation =
                getReservationOrThrow(
                        reservationId
                );
        User admin =
                getAdminOrThrow(adminId);
        if (reservation.getStatus()
                != ReservationStatus.PENDING) {
            throw new RuntimeException(
                    "Solo reservas pendientes pueden rechazarse"
            );
        }
        ReservationStatus oldStatus =
                reservation.getStatus();
        reservation.setStatus(
                ReservationStatus.REJECTED
        );
        reservation.setRejectionReason(
                reason
        );
        Reservation updatedReservation =
                reservationRepository.save(
                        reservation
                );

        historyService.saveHistory(
                updatedReservation,
                oldStatus,
                ReservationStatus.REJECTED,
                reason,
                admin
        );

        notificationService
                .sendReservationRejected(
                        updatedReservation
                );

        return updatedReservation;
    }

    @Override
    public Reservation cancelReservation(
            Long reservationId,
            String reason,
            Long adminId
    ) {
        if (reason == null
                || reason.isBlank()) {

            throw new RuntimeException(
                    "El motivo es obligatorio"
            );
        }
        Reservation reservation =
                getReservationOrThrow(
                        reservationId
                );
        User admin =
                getAdminOrThrow(adminId);
        if (reservation.getStatus()
                != ReservationStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Solo reservas confirmadas pueden cancelarse"
            );
        }
        ReservationStatus oldStatus =
                reservation.getStatus();
        reservation.setStatus(
                ReservationStatus.CANCELLED
        );
        reservation.setCancellationReason(
                reason
        );
        Reservation updatedReservation =
                reservationRepository.save(
                        reservation
                );
        historyService.saveHistory(
                updatedReservation,
                oldStatus,
                ReservationStatus.CANCELLED,
                reason,
                admin
        );
        notificationService
                .sendReservationCancelled(
                        updatedReservation
                );
        return updatedReservation;
    }

    @Override
    public Reservation completeReservation(
            Long reservationId,
            Long adminId
    ) {
        Reservation reservation =
                getReservationOrThrow(
                        reservationId
                );
        User admin =
                getAdminOrThrow(adminId);
        if (reservation.getStatus()
                != ReservationStatus.CONFIRMED) {
            throw new RuntimeException(
                    "Solo reservas confirmadas pueden completarse"
            );
        }
        ReservationStatus oldStatus =
                reservation.getStatus();
        reservation.setStatus(
                ReservationStatus.COMPLETED
        );
        Reservation updatedReservation =
                reservationRepository.save(
                        reservation
                );
        loyaltyService.updateProgress(
                reservation.getClient()
        );
        if (Boolean.TRUE.equals(
                reservation.getDiscountApplied()
        )) {

            loyaltyService.consumeDiscount(
                    reservation.getClient()
            );
        }
        historyService.saveHistory(
                updatedReservation,
                oldStatus,
                ReservationStatus.COMPLETED,
                "Reserva completada",
                admin
        );

        return updatedReservation;
    }

    @Override
    public List<Reservation>
    getPendingReservations() {

        return reservationRepository
                .findByStatusOrderByCreatedAtDesc(
                        ReservationStatus.PENDING
                );
    }

    @Override
    public List<Reservation>
    getReservationsByClient(
            Long clientId
    ) {

        User client = userRepository.findById(
                        clientId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Cliente no encontrado"
                        )
                );

        return reservationRepository
                .findByClient(client);
    }

    private Reservation getReservationOrThrow(
            Long reservationId
    ) {

        return reservationRepository.findById(
                        reservationId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reserva no encontrada"
                        )
                );
    }

    private User getAdminOrThrow(
            Long adminId
    ) {
        return userRepository.findById(
                        adminId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Administrador no encontrado"
                        )
                );
    }

}
