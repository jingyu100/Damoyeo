package Team6.Damoyeo.Post.Entity;

import Team6.Damoyeo.User.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String postTitle;
    private String postContent;
    private String postActivityArea;
    private String postField;

    @Column(name = "post_created_at")
    private LocalDateTime createdAt;

    @Column(name = "post_end_at")
    private LocalDateTime endAt;

    private String postStatus;
    private int postViewCount;
    private int postMaxParticipants;
    private int postCurParticipants;
    private BigDecimal postLatitude;
    private BigDecimal postLongitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostProfile> profiles = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostRequest> requests = new ArrayList<>();

}