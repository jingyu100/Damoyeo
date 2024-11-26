package Team6.Damoyeo.chat.repository;

import Team6.Damoyeo.chat.Entity.ChatMessage;
import Team6.Damoyeo.chat.dto.ChatMessageDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

}
