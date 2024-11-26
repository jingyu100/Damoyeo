package Team6.Damoyeo.chat.dto;

import Team6.Damoyeo.chat.Entity.ChatMessage;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private MessageType type;
    private String content;
    private String sender;
    private String roomId;
    private List<ChatMessageHistoryDto> chatHistory;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}

