package Team6.Damoyeo.Like.Service;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Like.Repository.LikeRepository;
import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    public final LikeRepository likeRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    
    // 좋아요 토글 기능 메서드
    public String toggleLike(int postId, int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<Like> existingLike = likeRepository.findByUserAndPost(user,post);
        String result;

        log.info(existingLike.toString());

        if(existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() -1);
            result = "0";
        }else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            like.setIsLiked("1");
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            result = "1";
        }

        postRepository.save(post);
        return result;
    }

    // 체크를 했는지 안했는지 체크하는 메서드
    public boolean checkIfUserLiked(int postId, int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<Like> like = likeRepository.findByUserAndPost(user,post);
        log.info(String.valueOf(like.isPresent()));
        return like.isPresent();
    }
    
    // 유저가 좋아요 눌렀던 게시글 보여주는 메서드
    public List<Post> findByLikedUser(int userId) {
        return likeRepository.findLikeedPostByUserId(userId);
    }
}
