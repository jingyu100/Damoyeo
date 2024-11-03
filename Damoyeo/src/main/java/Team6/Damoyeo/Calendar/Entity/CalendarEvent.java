package Team6.Damoyeo.Calendar.Entity;

import Team6.Damoyeo.User.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "calendar_events")
@Getter
public class CalendarEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ce_event_id")
    private Long id;

    private String ceTitle;
    private String ceDescription;

    @Column(name = "ce_start_time")
    private LocalDateTime startTime;

    @Column(name = "ce_end_time")
    private LocalDateTime endTime;

    @Column(name = "ce_created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
