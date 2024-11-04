package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    // 특정 페이지에 해당하는 게시글 가져오기
    public Page<Post> findPostsByPage(int page, int pageSize) {
        return postRepository.findAll(PageRequest.of(page, pageSize));
    }

    public void savePost(Post post) {
        //게시글 생성할때 현재 참가자 1
        post.setPostCurParticipants(1);
        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);
    }
}
