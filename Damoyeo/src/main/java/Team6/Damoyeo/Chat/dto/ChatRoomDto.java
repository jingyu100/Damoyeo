package Team6.Damoyeo.chat.dto;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomDto {

    private Long roomId;

    private String postTitle;

    private LocalDateTime createdAt;

    private String photoUrl;

//    private Integer postId;

    private Post post;

    public static ChatRoomDto from(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
                .roomId(chatRoom.getId())
                .postTitle(chatRoom.getPost().getTitle())
                .createdAt(chatRoom.getCreatedAt())
                .photoUrl(chatRoom.getPost().getPhotoUrl())
                .post(chatRoom.getPost())
                .build();
    }

}
