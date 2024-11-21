package Team6.Damoyeo.Like.Service;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Like.Repository.LikeRepository;
import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private final LikeRepository likeRepository;

    @Autowired
    private final PostRepository postRepository;

    @Autowired
    private final UserRepository userRepository;


    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public boolean toggleLike(int postId, int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user,post);

        if(existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false;
        }else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            return true;
        }
    }

    public int getLikeCount(int postId) {
        return likeRepository.countByPostId(postId);
    }
}
