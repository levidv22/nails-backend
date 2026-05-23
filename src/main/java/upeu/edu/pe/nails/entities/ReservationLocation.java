package upeu.edu.pe.nails.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reservation_locations")
public class ReservationLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;

    private Double longitude;

    private String address;

    private String referenceText;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
}
