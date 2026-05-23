package upeu.edu.pe.nails.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import upeu.edu.pe.nails.entities.*;

import java.time.DayOfWeek;
import java.util.List;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {

    List<WeeklySchedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<WeeklySchedule> findByAdmin(User admin);
    List<WeeklySchedule> findByActiveTrue();
    List<WeeklySchedule> findByDayOfWeekAndActiveTrue(DayOfWeek dayOfWeek);
}