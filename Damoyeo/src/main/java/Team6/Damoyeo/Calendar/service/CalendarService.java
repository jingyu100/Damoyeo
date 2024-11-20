package Team6.Damoyeo.calendar.service;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import Team6.Damoyeo.calendar.dto.CalendarEventDto;
import Team6.Damoyeo.calendar.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarService {

    private final CalendarRepository eventRepository;

    public List<CalendarEvent> getEventsByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        return eventRepository.findByUser(user);
    }

    public CalendarEvent createEvent(CalendarEventDto eventDto, User user) {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null");
            }

            log.debug("Creating event: {} for user: {}", eventDto, user.getNickname());

            CalendarEvent event = CalendarEvent.builder()
                    .title(eventDto.getTitle())
                    .description(eventDto.getDescription())
                    .startTime(eventDto.getStartTime())
                    .endTime(eventDto.getEndTime())
                    .createdDate(LocalDateTime.now())
                    .user(user)
                    .build();

            return eventRepository.save(event);
        } catch (Exception e) {
            log.error("Error creating event", e);
            throw new RuntimeException("Failed to create event: " + e.getMessage(), e);
        }
    }
}