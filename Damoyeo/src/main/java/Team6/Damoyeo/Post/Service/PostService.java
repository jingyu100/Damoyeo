package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 특정 페이지에 해당하는 게시글 가져오기
    public Page<Post> findPostsByPage(int page, int pageSize) {
        return postRepository.findAll(PageRequest.of(page, pageSize));
    }

}
