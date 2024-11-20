package Team6.Damoyeo.Post.Entity;

import Team6.Damoyeo.User.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column
    private String title;

    @Column
    private String content;

    //도로명 주소
    @Column
    private String roadAddress;

    @Column
    private String detailAddress;

    //포스트 분야태그
    @Column
    private String tag;

    @Column
    private int likeCount;

    @Column
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private String status;

    @Column
    private int viewCount;

    @Column
    private int maxParticipants;

    @Column
    private int nowParticipants;

    @Column
    private String photoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore // Post -> User의 직렬화를 막음
    private User user;

    @OneToMany(mappedBy = "post")
    private List<PostRequest> requests = new ArrayList<>();

}