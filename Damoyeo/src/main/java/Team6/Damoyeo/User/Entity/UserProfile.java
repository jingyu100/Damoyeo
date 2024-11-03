package Team6.Damoyeo.User.Entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Getter
public class UserProfile {

    @Id
    @Column(name = "up_photo_url")
    private String photoUrl;

    @Column(name = "up_created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}