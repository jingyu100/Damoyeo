package Team6.Damoyeo.chat.repository;

import Team6.Damoyeo.chat.Entity.ChatMessage;
import Team6.Damoyeo.chat.dto.ChatMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoom_id(Long chatRoomId);

    List<ChatMessage> findByChatRoom_IdOrderByCreatedAtAsc(Long roomId);


}
