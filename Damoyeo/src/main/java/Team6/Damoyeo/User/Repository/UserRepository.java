package Team6.Damoyeo.User.Repository;

import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Integer id);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    // 이름이랑 전화번호로 아이디 찾는 메서드
    Optional<User> findByNameAndPhone(String name, String phone);

    // 아이디랑 전화번호로 비밀번호 찾는 메서드
    Optional<User> findByEmailAndPhone(String email, String phone);

    Optional<User> findByEmailAndStatus(String email, String status);
}
