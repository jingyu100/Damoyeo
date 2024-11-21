package Team6.Damoyeo.chat.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private MessageType type;
    private String content;
    private String sender;
    private String roomId; // 추가된 필드

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}

