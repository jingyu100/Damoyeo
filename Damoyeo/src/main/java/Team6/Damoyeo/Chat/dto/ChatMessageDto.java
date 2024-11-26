package Team6.Damoyeo.chat.dto;

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

