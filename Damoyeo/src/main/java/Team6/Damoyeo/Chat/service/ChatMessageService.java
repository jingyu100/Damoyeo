package Team6.Damoyeo.chat.service;

import Team6.Damoyeo.chat.Entity.ChatMessage;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import Team6.Damoyeo.chat.dto.ChatMessageDto;
import Team6.Damoyeo.chat.repository.ChatMessageRepository;
import Team6.Damoyeo.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방의 모든 메시지를 생성 시간 순으로 조회
    @Transactional(readOnly = true)
    public List<ChatMessage> findChatMessageByChatRoom_id(Long roomId) {

        // 채팅방 ID로 해당 방의 모든 메시지를 시간순으로 조회
        return chatMessageRepository.findByChatRoom_IdOrderByCreatedAtAsc(roomId);

    }


    // 새로운 채팅 메시지를 저장
    @Transactional
    public ChatMessage saveChatMessage(ChatMessageDto chatMessageDto) {

        // 메시지를 저장할 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(chatMessageDto.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + chatMessageDto.getRoomId()));

        // DTO를 엔티티로 변환
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(chatMessageDto.getContent());  // 메시지 내용 설정
        chatMessage.setSender(chatMessageDto.getSender());    // 발신자 설정
        chatMessage.setChatRoom(chatRoom);                    // 채팅방 연결
        chatMessage.setCreatedAt(LocalDateTime.now());        // 생성 시간 설정

        // DB에 메시지 저장 후 반환
        return chatMessageRepository.save(chatMessage);
    }
}

