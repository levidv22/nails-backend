package upeu.edu.pe.nails.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer completedReservations;

    private Integer currentProgress;

    private Boolean discountAvailable;

    private Integer discountPercentage;

    @OneToOne
    @JoinColumn(name = "client_id")
    private User client;
}