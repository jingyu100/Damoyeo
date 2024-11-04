package Team6.Damoyeo.User.Service;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        // 로그 추가
        System.out.println("Login attempt - Email: " + email);

        User user = null;

        user = userRepository.findByUserEmail(email);

        // 사용자가 없는 경우
        if(user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 확인
        if(!user.getUserPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        user.setLastLoginDate(LocalDateTime.now());
        return userRepository.save(user);
    }
}