package Team6.Damoyeo.Post.Entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_profiles")
@Getter
public class PostProfile {

    @Id
    @Column(name = "pp_photo_url")
    private String photoUrl;

    @Column(name = "pp_created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

}

