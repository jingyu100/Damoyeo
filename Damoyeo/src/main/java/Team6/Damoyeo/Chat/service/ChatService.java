package Team6.Damoyeo.chat.service;

import Team6.Damoyeo.chat.dto.ChatRoomDto;
import Team6.Damoyeo.chat.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동 생성
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class ChatService {

    // 채팅 참여자 정보를 관리하는 레포지토리
    private final ChatParticipantRepository chatParticipantRepository;

    public List<ChatRoomDto> getUserChatRooms(Integer userId) {
        // 사용자의 모든 채팅방 참여 기록을 조회한 후
        // 참여 기록에서 각 채팅방 정보를 ChatRoomDto로 변환
        return chatParticipantRepository.findAllByUserId(userId)
                .stream()
                .map(participant -> ChatRoomDto.from(participant.getChatRoom()))
                .collect(Collectors.toList());
    }
}

