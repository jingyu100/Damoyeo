package Team6.Damoyeo.chat.repository;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.chat.Entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPost(Post post);

}
