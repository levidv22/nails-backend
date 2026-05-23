package upeu.edu.pe.nails.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.*;

import java.time.*;
import java.util.List;

public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long> {

    List<ScheduleException> findByDate(LocalDate date);
    List<ScheduleException> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<ScheduleException> findByAdmin(User admin);
}
