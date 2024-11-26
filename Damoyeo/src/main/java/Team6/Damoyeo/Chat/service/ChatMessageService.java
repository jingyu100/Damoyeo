package Team6.Damoyeo.chat.service;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import Team6.Damoyeo.chat.Entity.ChatMessage;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import Team6.Damoyeo.chat.dto.ChatMessageDto;
import Team6.Damoyeo.chat.repository.ChatMessageRepository;
import Team6.Damoyeo.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    private final UserRepository userRepository;

    private final ChatRoomRepository chatRoomRepository;

    public void saveChatMessage(ChatMessageDto chatMessageDto) {

        // room sender content
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(Long.valueOf(chatMessageDto.getRoomId()));

        log.info(chatMessageDto.toString());

        Optional<User> ou = userRepository.findByNickname(chatMessageDto.getSender());
        ChatRoom chatRoom = null;
        User user = null;
        if (optionalChatRoom.isPresent()) {
            chatRoom = optionalChatRoom.get();
        }
        if (ou.isPresent()) {
            user = ou.get();
        }

        assert user != null;
        log.info(user.toString());

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(user)
                .content(chatMessageDto.getContent())
                .build();

        chatMessageRepository.save(chatMessage);
    }

}
