package Team6.Damoyeo.Post.Repository;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRequestRepository extends JpaRepository<PostRequest, Integer> {


     Optional<PostRequest> findByPostAndUser(Post post, User user);

}
