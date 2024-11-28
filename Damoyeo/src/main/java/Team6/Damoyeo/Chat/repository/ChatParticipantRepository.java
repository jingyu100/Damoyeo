package Team6.Damoyeo.chat.repository;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.chat.Entity.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import Team6.Damoyeo.chat.Entity.ChatRoom;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    @Query("SELECT cp FROM ChatParticipant cp " +
            "JOIN FETCH cp.chatRoom cr " +
            "JOIN FETCH cr.post " +
            "WHERE cp.user.id = :userId AND cp.status = 'ACTIVE'")
    List<ChatParticipant> findAllByUserId(@Param("userId") Integer userId);

    Optional<ChatParticipant>findByChatRoomAndUser(ChatRoom chatRoom, User user);
}
