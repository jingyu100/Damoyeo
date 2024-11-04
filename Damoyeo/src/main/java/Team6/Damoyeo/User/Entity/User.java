package Team6.Damoyeo.User.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column
    private String userPassword;

    @Column
    private String userName;

    @Column
    private String userNickname;

    @Column
    private String userPhoneNumber;

    @Column
    private String userEmail;

    @Column
    private String userGender;

    @Column(name = "user_enroll_date")
    private LocalDateTime enrollDate;

    @Column(name = "user_withdrawal_date")
    private LocalDateTime withdrawalDate;

    @Column(name = "user_last_login_date")
    private LocalDateTime lastLoginDate;

    @Column
    private String userArea;

    @Column
    private String userComment;

    @OneToMany(mappedBy = "user")
    private List<UserProfile> profiles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<UserInterest> interests = new ArrayList<>();

}