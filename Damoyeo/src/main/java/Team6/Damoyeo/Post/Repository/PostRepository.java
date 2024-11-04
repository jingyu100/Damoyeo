package Team6.Damoyeo.Post.Repository;

import Team6.Damoyeo.Post.Entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p ORDER BY p.id DESC LIMIT :limit OFFSET :offset")
    List<Post> findPostsByPage(@Param("offset") int offset, @Param("limit") int limit);

}
