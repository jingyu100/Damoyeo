package Team6.Damoyeo.User.Repository;

import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByNameAndPhone(String name, String phone);
}
