package Team6.Damoyeo.User.Service;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User loginUser(String email, String password) {
        // 로그 추가
        System.out.println("Login attempt - Email: " + email);
        Optional<User> ou = userRepository.findByEmail(email);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();

        // 사용자가 없는 경우
        if(user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return userRepository.save(user);
    }
    
    //이메일 중복 체크 메서드
    public boolean emailCheck(String email) {
        Optional<User> ou = userRepository.findByEmail(email);
        if (ou.isEmpty()) {
            return false;
        }
        User user = ou.get();
        return user != null;
    }

    public User findByUser(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        return ou.get();
    }

    public String findByUserId(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        String nickName = user.getNickname();
        return nickName;
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email); // 여기서 true/false 반환
    }

    public void updateUser(User user) {
        Optional<User> byId = userRepository.findById(user.getUserId());
        if (byId.isEmpty()){
            return;
        }
        User user1 = byId.get();
        userRepository.save(user1);
    }
}