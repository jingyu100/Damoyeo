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

    @Column
    private String postTitle;

    @Column
    private String postContent;

    @Column
    private String postActivityArea;

    @Column
    private String postField;

    @Column(name = "post_created_at")
    private LocalDateTime createdAt;

    @Column(name = "post_end_at")
    private LocalDateTime endAt;

    @Column
    private String postStatus;

    @Column
    private int postViewCount;

    @Column
    private int postMaxParticipants;

    @Column
    private int postCurParticipants;

    @Column
    private BigDecimal postLatitude;

    @Column
    private BigDecimal postLongitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostProfile> profiles = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostRequest> requests = new ArrayList<>();

}