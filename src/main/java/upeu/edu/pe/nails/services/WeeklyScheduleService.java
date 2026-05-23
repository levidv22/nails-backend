package upeu.edu.pe.nails.services;

import upeu.edu.pe.nails.entities.WeeklySchedule;

import java.time.DayOfWeek;
import java.util.List;

public interface WeeklyScheduleService {

    WeeklySchedule createWeeklySchedule(WeeklySchedule weeklySchedule);
    WeeklySchedule updateWeeklySchedule(Long scheduleId, WeeklySchedule weeklySchedule);
    void disableWeeklySchedule(Long scheduleId);
    List<WeeklySchedule> getSchedulesByDay(DayOfWeek dayOfWeek);
}