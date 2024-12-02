package Team6.Damoyeo.User.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import Team6.Damoyeo.calendar.repository.CalendarRepository;
import Team6.Damoyeo.chat.repository.ChatRoomRepository;
import java.util.List;
import java.util.Optional;

import Team6.Damoyeo.calendar.Entity.CalendarEvent;
import Team6.Damoyeo.chat.repository.ChatParticipantRepository;
import Team6.Damoyeo.chat.Entity.ChatParticipant;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final PostRequestRepository postRequestRepository;
    private final CalendarRepository calendarRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    // 회원가입 처리
    public User registerUser(User user) {

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus("1");
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

    // 탈퇴
    @Transactional
    public void deleteUser(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (!ou.isEmpty()) {
            User user = ou.get();


            List<Post> postList = postRepository.findByUserAndStatusNot(user, "4");
            //탈퇴 하면 유저가 적은글 상태 변화 시키게 만들기
            for (Post post : postList) {
                // 탈퇴한 유저 게시글 상태
                post.setStatus("0");
                postRepository.save(post);
                // 탈퇴하면 탈퇴한 유저의 게시글 모임신청한 유저들의 캘린더 등록된거 삭제
                List<CalendarEvent> calendarEventList = calendarRepository.findByPost(post);
                calendarRepository.deleteAll(calendarEventList);
                //탈퇴한 유저 게시글 찾고 해당 게시글의 참가 요청들의 상태를 4로 변환
                List<PostRequest> postRequests = postRequestRepository.findByPostAndStatusNot(post, "2");
                for (PostRequest postRequest : postRequests) {
                    postRequest.setStatus("4");
                    postRequestRepository.save(postRequest);
                }

            }

            // 이제는 탈퇴한 유저가 참가한 채팅방에서 나가기..
            // 모임 참가 요청이 1인거 찾기..
            List<PostRequest> byUserAndStatus = postRequestRepository.findByUserAndStatus(user, "1");
            for (PostRequest postRequest : byUserAndStatus) {
                Optional<ChatRoom> byPost = chatRoomRepository.findByPost(postRequest.getPost());
                if (byPost.isEmpty()){
                    continue;
                }
                ChatRoom chatRoom =  byPost.get();
                // 게시글 현재 인원 1 줄여주고 최대 인원보다 작아지고 게시글 상태가 2인건 게시글 상태를 1로 변경
                postRequest.getPost().setNowParticipants(postRequest.getPost().getNowParticipants() - 1);
                if (postRequest.getPost().getNowParticipants() < postRequest.getPost().getMaxParticipants() &&  postRequest.getPost().getStatus().equals("2")) {
                    postRequest.getPost().setStatus("1");
                }
                postRepository.save(postRequest.getPost());
                //채팅 룸 찾기
                Optional<ChatParticipant> byChatRoomAndUser = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user);
                if (byChatRoomAndUser.isEmpty()) {
                    continue;
                }
                ChatParticipant chatParticipant = byChatRoomAndUser.get();

                // 캘린더에서도 삭제
                Optional<CalendarEvent> byPostAndUser1 = calendarRepository.findByPostAndUser(postRequest.getPost(), user);
                if (byPostAndUser1.isEmpty()) {
                    continue;
                }
                CalendarEvent calendarEvent = byPostAndUser1.get();

                // 셋다 db에서 삭제시킨다
                calendarRepository.delete(calendarEvent);
                postRequestRepository.delete(postRequest);
                chatParticipantRepository.delete(chatParticipant);
            }

            user.setStatus("0");
            userRepository.save(user);
        }
    }

    // 비밀번호 확인 메서드
    public boolean checkPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }


    //사용자 아이디 찾기 메서드
    public Optional<User> findByUserId(String name, String phone) {
        return userRepository.findByNameAndPhone(name,phone);
    }

    //사용자 비밀번호 찾기 메서드
    public Optional<User> findByUserPassword(String email, String phone) {
        return userRepository.findByEmailAndPhone(email,phone);
    }

    public Optional<User> findByUserEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
