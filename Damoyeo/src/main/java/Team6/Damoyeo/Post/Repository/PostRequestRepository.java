package Team6.Damoyeo.Post.Repository;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRequestRepository extends JpaRepository<PostRequest, Integer> {


     Optional<PostRequest> findByPostAndUser(Post post, User user);

     List<PostRequest> findByPost(Post post);
     // 스테이터스가 0일때 찾기 위해 만듬
     List<PostRequest> findByPostAndStatus(Post post, String status);

     List<PostRequest> findByUserAndStatus(User user, String status);
}
