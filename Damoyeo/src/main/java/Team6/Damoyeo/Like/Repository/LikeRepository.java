package Team6.Damoyeo.Like.Repository;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like,Integer> {

    Optional<Like> findByUserAndPost(User user, Post post);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.postId = :postId")
    int countByPostId(@Param("postId") int postId);


    boolean existsByUserAndPost(User user, Post post);
}
