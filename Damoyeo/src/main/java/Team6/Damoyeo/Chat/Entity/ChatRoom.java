    package Team6.Damoyeo.chat.Entity;

    import Team6.Damoyeo.Post.Entity.Post;
    import com.fasterxml.jackson.databind.PropertyNamingStrategies;
    import com.fasterxml.jackson.databind.annotation.JsonNaming;
    import jakarta.persistence.*;
    import lombok.AccessLevel;
    import lombok.Builder;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "chat_rooms")
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class ChatRoom extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String roomName;

        @OneToOne
        @JoinColumn(name = "post_id")
        private Post post;

        @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
        private List<ChatParticipant> participants = new ArrayList<>();

        @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
        private List<ChatMessage> messages = new ArrayList<>();

        @Builder
        public ChatRoom(String roomName, Post post) {
            this.roomName = roomName;
            this.post = post;
        }
    }