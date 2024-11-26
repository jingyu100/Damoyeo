package Team6.Damoyeo.chat.dto;

import Team6.Damoyeo.chat.Entity.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageHistoryDto {
    private String sender;
    private String content;
    private LocalDateTime sentAt;
}
