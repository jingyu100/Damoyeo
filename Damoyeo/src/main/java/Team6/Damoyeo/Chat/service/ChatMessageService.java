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

    @Transactional(readOnly = true)
    public List<ChatMessage> findChatMessageByChatRoom_id(Long roomId) {

        return chatMessageRepository.findByChatRoom_IdOrderByCreatedAtAsc(roomId);

    }

    @Transactional
    public ChatMessage saveChatMessage(ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(Long.valueOf(chatMessageDto.getRoomId()))
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + chatMessageDto.getRoomId()));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(chatMessageDto.getContent());
        chatMessage.setSender(chatMessageDto.getSender());
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setCreatedAt(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }
}

