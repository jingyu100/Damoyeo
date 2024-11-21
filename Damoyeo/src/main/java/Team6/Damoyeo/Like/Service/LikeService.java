package Team6.Damoyeo.Like.Service;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Like.Repository.LikeRepository;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public boolean addLike(int postId, int userId) {
        if(likeRepository.countLikeByPostIdAndUserId(postId, userId) > 0 ) {
            return false;
        }

        var post = postRepository.findById(postId).orElse(null);
        var user = userRepository.findById(userId).orElse(null);

        if (post == null || user == null) {
            return false;  // Post 또는 User가 존재하지 않으면 실패
        }

        Like like = new Like();
        like.setPost(post);
        like.setUser(user);
        like.setLikeid(1);

        likeRepository.save(like);
        return true;
    }

    public boolean removeLike(int postId, int userId) {
        if(likeRepository.countLikeByPostIdAndUserId(postId, userId) == 0) {
            return false;
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);
        return true;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public PostRepository getPostRepository() {
        return postRepository;
    }
}
