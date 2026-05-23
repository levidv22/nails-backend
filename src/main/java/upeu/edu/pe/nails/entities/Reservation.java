package upeu.edu.pe.nails.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.*;
import java.time.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reservationDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    private ServiceMode serviceMode;

    private BigDecimal basePrice;

    private BigDecimal discountAmount;

    private BigDecimal finalPrice;

    private String rejectionReason;

    private String cancellationReason;

    private Boolean discountApplied;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Servi servi;
}
