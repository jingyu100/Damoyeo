package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import Team6.Damoyeo.chat.Entity.ChatParticipant;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import Team6.Damoyeo.chat.repository.ChatParticipantRepository;
import Team6.Damoyeo.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostRequestService {

    private final PostRequestRepository postRequestRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final ChatParticipantRepository chatParticipantRepository;

    public void saveRequest(Integer userId, Integer postId, String text) {
        PostRequest postRequest = new PostRequest();
        Optional<User> ou = userRepository.findById(userId);
        Optional<Post> po = postRepository.findById(postId);
        User user = null;
        Post post = null;
        if (ou.isPresent()) {
            user = ou.get();
        }
        if (po.isPresent()) {
            post = po.get();
        }
        postRequest.setUser(user);
        postRequest.setPost(post);
        postRequest.setStatus("0");
        postRequest.setMessage(text);
        postRequest.setCreatedDate(LocalDateTime.now());
        postRequestRepository.save(postRequest);
    }

    public PostRequest findClick(Post post, User user) {

        Optional<PostRequest> pru = postRequestRepository.findByPostAndUser(post, user);
        if (pru.isEmpty()) {
            return null;
        }
        return pru.get();
    }

    public void delete(PostRequest postRequest) {
        postRequestRepository.delete(postRequest);
    }

    //수락했을때
//    public void accet(Integer prId) {
//        Optional<PostRequest> opr = postRequestRepository.findById(prId);
//        if (opr.isPresent()) {
//            PostRequest pr = opr.get();
//            pr.setStatus("1");
//            pr.getPost().setNowParticipants(pr.getPost().getNowParticipants() + 1);
//            postRequestRepository.save(pr);
//            postRepository.save(pr.getPost());
//        }
//    }

    public void accet(Integer prId) {
        // 1. PostRequest 조회
        PostRequest postRequest = postRequestRepository.findById(prId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Post Request ID"));

        // 2. 상태를 '수락'으로 변경
        postRequest.setStatus("ACCEPTED");

        // 3. 해당 게시물의 채팅방 조회 또는 생성
        ChatRoom chatRoom = chatRoomRepository.findByPost(postRequest.getPost())
                .orElseGet(() -> {
                    ChatRoom newChatRoom = ChatRoom.builder()
                            .roomName(postRequest.getPost().getTitle() + "의 채팅방")
                            .post(postRequest.getPost())
                            .build();
                    return chatRoomRepository.save(newChatRoom);
                });

        // 4. 채팅방 참여자로 바로 추가
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(postRequest.getUser())
                .build();
        chatParticipantRepository.save(participant);
    }


    public void refusal(Integer prId) {
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
            pr.setStatus("2");
            postRequestRepository.save(pr);
        }
    }

    //거절 메서드인데 일단은 삭제로 함
    public void rejected(Integer prId) {
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
//            pr.setStatus("3");
//            postRequestRepository.save(pr);
            postRequestRepository.delete(pr);
        }
    }
}
