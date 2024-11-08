package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    // 특정 페이지에 해당하는 게시글 가져오기
    public Page<Post> findPostsByPage(int page, int pageSize) {
        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate")));
    }

    public void savePost(Post post) {
        //게시글 생성할때 현재 참가자 1
        post.setNowParticipants(1);
        post.setCreatedDate(LocalDateTime.now());
        postRepository.save(post);
    }
    
    // 상세 페이지
    public Post findById(Integer id) throws Exception {
        Optional<Post> post = this.postRepository.findById(id);
        if(post.isPresent()) {
            return post.get();
        }else {
            throw new Exception("post not found");
        }
    }
    
    // 조회 수
    @Transactional
    public int updateView(Integer id) {
        return postRepository.updateViews(id);
    }


    // 이미지 널 값일때 기본 이미지 사용
    public String postImgUrl(String imgUrl) {
        if (imgUrl.isEmpty())
        {
            return "";
        }
        return imgUrl;
    }

}
