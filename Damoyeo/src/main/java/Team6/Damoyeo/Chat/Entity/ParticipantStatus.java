package Team6.Damoyeo.chat.Entity;

import lombok.Getter;

@Getter
public enum ParticipantStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    BANNED("차단됨");

    private final String description;

    ParticipantStatus(String description) {
        this.description = description;
    }
}