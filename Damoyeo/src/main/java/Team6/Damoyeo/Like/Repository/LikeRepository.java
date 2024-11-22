package Team6.Damoyeo.Like.Repository;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Integer> {

    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("SELECT p FROM Post p JOIN Like l ON p.postId = l.post.postId WHERE l.user.userId = :userId")
    List<Post> findLikeedPostByUserId(@Param("userId") int userId);
}
