package Team6.Damoyeo.chat.Entity;

import lombok.Getter;

@Getter
public enum MessageStatus {
    SENT("전송됨"),
    DELIVERED("전달됨"),
    READ("읽음");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }
}
