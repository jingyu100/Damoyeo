package Team6.Damoyeo.User.Entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String userPassword;
    private String userName;
    private String userNickname;
    private String userPhoneNumber;
    private String userEmail;
    private String userGender;

    @Column(name = "user_enroll_date")
    private LocalDateTime enrollDate;

    @Column(name = "user_withdrawal_date")
    private LocalDateTime withdrawalDate;

    @Column(name = "user_last_login_date")
    private LocalDateTime lastLoginDate;

    private String userArea;
    private String userComment;

    @OneToMany(mappedBy = "user")
    private List<UserProfile> profiles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserInterest> interests = new ArrayList<>();

}