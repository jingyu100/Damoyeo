package Team6.Damoyeo.chat.service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import Team6.Damoyeo.chat.Entity.ChatParticipant;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import Team6.Damoyeo.chat.dto.ChatRoomDto;
import Team6.Damoyeo.chat.dto.ParticipantDto;
import Team6.Damoyeo.chat.repository.ChatMessageRepository;
import Team6.Damoyeo.chat.repository.ChatParticipantRepository;
import Team6.Damoyeo.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import Team6.Damoyeo.calendar.repository.CalendarRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
@Slf4j
public class ChatService {

    // 채팅 참여자 정보를 관리하는 레포지토리
    private final ChatParticipantRepository chatParticipantRepository;

    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostRequestRepository postRequestRepository;

    private final CalendarRepository calendarRepository;
    public List<ChatRoomDto> getUserChatRooms(Integer userId) {

        // 사용자의 모든 채팅방 참여 기록을 조회한 후
        // 참여 기록에서 각 채팅방 정보를 ChatRoomDto로 변환
        return chatParticipantRepository.findAllByUserId(userId)
                .stream()
                .map(participant -> ChatRoomDto.from(participant.getChatRoom()))
                .collect(Collectors.toList());
    }

    public void createChatRoomForPost(Post post, Integer userId) {

        // 1. 채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName("Chat for Post " + post.getPostId())
                .post(post)
                .build();
        chatRoomRepository.save(chatRoom);

        // 2. 작성자를 채팅방 참여자로 추가
        ChatParticipant participant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .user(post.getUser()) // Post의 작성자
                .build();
        chatParticipantRepository.save(participant);
    }

    // 채팅방의 모든 참가자 목록 조회
    public List<ParticipantDto> getRoomParticipants(String roomId) {
        return chatParticipantRepository.findByChatRoom_Id(Long.valueOf(roomId))
                .stream()
                .map(participant -> {
                    User user = participant.getUser();
                    return ParticipantDto.builder()
                            .userId(user.getUserId())
                            .nickname(user.getNickname())
                            .photoUrl(user.getPhotoUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }


    // 채팅방 나갈때
    @Transactional
    public void getOutChatRoom(Long id, Integer userId) {



        //1. id로 채팅방 찾고
        Optional<ChatRoom> oc = chatRoomRepository.findById(id);
        if (oc.isEmpty()) {
            return;
        }
        ChatRoom chatRoom = oc.get();
        // 채팅방에서 게시글 가져와서
        Post post = chatRoom.getPost();
        // 게시글 현재 인원 1 줄여주고 최대 인원보다 작아지면 게시글 상태를 1로 변경
        post.setNowParticipants(post.getNowParticipants() - 1);
        if (post.getNowParticipants() < post.getMaxParticipants()) {
            post.setStatus("1");
        }
        postRepository.save(post);
        // 유저 아이디로 유저 찾고
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return;
        }
        User user = ou.get();
        // 찾은 채팅방 유저로 ChatParticipant 찾고
        Optional<ChatParticipant> byChatRoomAndUser = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user);
        if (byChatRoomAndUser.isEmpty()) {
            return;
        }
        ChatParticipant chatParticipant = byChatRoomAndUser.get();

        // 찾은 채팅방 유저로 해당 유저가 보낸 요청 찾아서
        Optional<PostRequest> byPostAndUser = postRequestRepository.findByPostAndUser(post, user);
        if (byPostAndUser.isEmpty()) {
            return;
        }
        PostRequest postRequest = byPostAndUser.get();

        // 캘린더에서도 삭제
        Optional<CalendarEvent> byPostAndUser1 = calendarRepository.findByPostAndUser(post, user);
        if (byPostAndUser1.isEmpty()) {
            return;
        }
        CalendarEvent calendarEvent = byPostAndUser1.get();

        // 셋다 db에서 삭제시킨다
        calendarRepository.delete(calendarEvent);
        postRequestRepository.delete(postRequest);
        chatParticipantRepository.delete(chatParticipant);

    }
    @Transactional
    public void kickOutChatRoom(Long id, Integer userId) {
        //1. id로 채팅방 찾고
        Optional<ChatRoom> oc = chatRoomRepository.findById(id);
        if (oc.isEmpty()) {
            return;
        }
        ChatRoom chatRoom = oc.get();
        // 채팅방에서 게시글 가져와서
        Post post = chatRoom.getPost();
        // 게시글 현재 인원 1 줄여주고 최대 인원보다 작아지면 게시글 상태를 1로 변경
        post.setNowParticipants(post.getNowParticipants() - 1);
        if (post.getNowParticipants() < post.getMaxParticipants()) {
            post.setStatus("1");
        }
        postRepository.save(post);
        // 유저 아이디로 유저 찾고
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return;
        }
        User user = ou.get();
        // 찾은 채팅방 유저로 ChatParticipant 찾고
        Optional<ChatParticipant> byChatRoomAndUser = chatParticipantRepository.findByChatRoomAndUser(chatRoom, user);
        if (byChatRoomAndUser.isEmpty()) {
            return;
        }
        ChatParticipant chatParticipant = byChatRoomAndUser.get();

        // 찾은 채팅방 유저로 해당 유저가 보낸 요청 찾아서
        Optional<PostRequest> byPostAndUser = postRequestRepository.findByPostAndUser(post, user);
        if (byPostAndUser.isEmpty()) {
            return;
        }
        PostRequest postRequest = byPostAndUser.get();
        postRequest.setStatus("5");

        // 캘린더에서도 삭제
        Optional<CalendarEvent> byPostAndUser1 = calendarRepository.findByPostAndUser(post, user);
        if (byPostAndUser1.isEmpty()) {
            return;
        }
        CalendarEvent calendarEvent = byPostAndUser1.get();

        // 셋다 db에서 삭제시킨다
        calendarRepository.delete(calendarEvent);
        postRequestRepository.save(postRequest);
        chatParticipantRepository.delete(chatParticipant);
    }

}

