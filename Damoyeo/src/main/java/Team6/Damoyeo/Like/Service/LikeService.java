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

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

    public final LikeRepository likeRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;


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

    public int getLikeCount(int postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean checkIfUserLiked(int postId, int userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Post post = postRepository.findById(postId).orElseThrow();

        Optional<Like> like = likeRepository.findByUserAndPost(user,post);
        log.info(String.valueOf(like.isPresent()));
        return like.isPresent();
    }
}
