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

    // 회원가입 처리
    public User registerUser(User user) {

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // User 객체 저장 후 반환
        return userRepository.save(user);

    }

    // 로그인 처리
    public User loginUser(String email, String password) {

        // 로그 출력
        System.out.println("Login attempt - Email: " + email);

        // 이메일로 사용자 조회
        Optional<User> ou = userRepository.findByEmail(email);

        // 사용자가 없을 경우 null 반환
        if (ou.isEmpty()) {
            return null;
        }

        User user = ou.get();

        // 사용자 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return userRepository.save(user);

    }

    // 이메일 중복 체크 메서드
    public boolean emailCheck(String email) {

        // 이메일로 사용자 조회
        Optional<User> ou = userRepository.findByEmail(email);

        // 중복 여부 확인 후 반환
        return ou.isPresent();

    }

    // 사용자 ID로 사용자 조회
    public User findByUser(Integer userId) {

        // ID로 사용자 조회
        Optional<User> ou = userRepository.findById(userId);

        // 존재 여부 확인 후 사용자 반환
        return ou.orElse(null);

    }

    // 사용자 ID로 닉네임 조회
    public String findByUserId(Integer userId) {

        // ID로 사용자 조회
        Optional<User> ou = userRepository.findById(userId);

        // 존재 여부 확인 후 닉네임 반환
        return ou.map(User::getNickname).orElse(null);

    }

    // 이메일 존재 여부 확인
    public boolean isEmailExists(String email) {

        // 이메일로 존재 여부 확인 후 반환
        return userRepository.existsByEmail(email);

    }

    // 이메일 존재 여부 확인
    public boolean isNicknameExists(String nickname) {

        // 이메일로 존재 여부 확인 후 반환
        return userRepository.existsByNickname(nickname);

    }

    // 사용자 정보 업데이트
    public void updateUser(User user) {

        // 사용자 ID로 기존 사용자 조회
        Optional<User> byId = userRepository.findById(user.getUserId());

        // 사용자 존재할 경우 업데이트
        byId.ifPresent(existingUser -> userRepository.save(user));

    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    // 비밀번호 확인 메서드
    public boolean checkPassword(User user,String password) {
        return passwordEncoder.matches(password,user.getPassword());
    }
}
