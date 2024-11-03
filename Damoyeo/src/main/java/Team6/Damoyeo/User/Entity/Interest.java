package Team6.Damoyeo.User.Entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "interests")
@Getter
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long id;

    @Column(name = "interest_name")
    private String name;

}