package Team6.Damoyeo.calendar.repository;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<CalendarEvent, Integer> {
    List<CalendarEvent> findByUser(User user);
}