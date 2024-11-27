package Team6.Damoyeo.calendar.service;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import Team6.Damoyeo.calendar.repository.CalendarRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public CalendarEvent createEvent(Team6.Damoyeo.calendar.dto.CalendarEventDTO eventDto, User user) {
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

    public void deleteEvent(Integer eventId, User user) {
        CalendarEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));

        if (!event.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("Unauthorized to delete this event");
        }

        eventRepository.delete(event);
    }

    public CalendarEvent updateEvent(Integer id, CalendarEvent updateEvent) {
        CalendarEvent existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));

        // 기존 이벤트의 불변 데이터 유지
        CalendarEvent updatedEvent = CalendarEvent.builder()
                .ceId(existingEvent.getCeId())
                .title(updateEvent.getTitle())
                .description(updateEvent.getDescription())
                .startTime(updateEvent.getStartTime())
                .endTime(updateEvent.getEndTime())
                .createdDate(existingEvent.getCreatedDate())
                .user(existingEvent.getUser())
                .build();

        return eventRepository.save(updatedEvent);
    }
}