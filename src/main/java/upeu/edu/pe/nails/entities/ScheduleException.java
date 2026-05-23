package upeu.edu.pe.nails.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule_exceptions")
public class ScheduleException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private ExceptionType type;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;
}