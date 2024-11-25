package Team6.Damoyeo.calendar.controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import Team6.Damoyeo.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final UserService userService;
    private final CalendarService calendarService;

    @GetMapping("main")
    public String main(@SessionAttribute(name = "userId", required = false) Integer userId,
                       Model model) {
        User user = null;
        if (userId != null) {
            user = userService.findByUser(userId);
        }
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "calendar/main";
    }

    @GetMapping("/events")
    public ResponseEntity<?> getEvents(
            @SessionAttribute(name = "userId", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }

        User user = userService.findByUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        return ResponseEntity.ok(calendarService.getEventsByUser(user));
    }

    @PostMapping("/events")
    @ResponseBody
    public ResponseEntity<?> createEvent(
            @RequestBody Team6.Damoyeo.calendar.dto.CalendarEventDTO eventDto,
            @SessionAttribute(name = "userId", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }

        User user = userService.findByUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        CalendarEvent event = calendarService.createEvent(eventDto, user);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseBody
    public ResponseEntity<?> deleteEvent(
            @PathVariable("eventId") Integer eventId,  // 이름 추가
            @SessionAttribute(name = "userId", required = false) Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User not logged in");
        }

        User user = userService.findByUser(userId);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        calendarService.deleteEvent(eventId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<CalendarEvent> updateEvent(@PathVariable Integer id, @RequestBody CalendarEvent event) {
        return ResponseEntity.ok(calendarService.updateEvent(id, event));
    }
}