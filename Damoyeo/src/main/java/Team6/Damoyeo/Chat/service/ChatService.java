package Team6.Damoyeo.chat.service;

import Team6.Damoyeo.chat.dto.ChatRoomDto;
import Team6.Damoyeo.chat.repository.ChatParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatParticipantRepository chatParticipantRepository;

    public List<ChatRoomDto> getUserChatRooms(Integer userId) {
        return chatParticipantRepository.findAllByUserId(userId)
                .stream()
                .map(participant -> ChatRoomDto.from(participant.getChatRoom()))
                .collect(Collectors.toList());
    }
}
