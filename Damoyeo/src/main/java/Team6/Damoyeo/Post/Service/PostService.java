package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRequestRepository postRequestRepository;


    // 특정 페이지에 해당하는 게시글을 가져오는 메서드
    public Page<Post> findPostsByPage(int page, int pageSize) {
        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate")));
    }

    // 게시글 저장 메서드 (작성자 정보 포함)
    public Post savePost(Post post, @SessionAttribute(name = "userId", required = false) Integer userId) {

        // 새로운 게시글 생성 시 참가자 수를 1로 설정
        post.setNowParticipants(1);

        // 사용자 ID로 작성자 정보 조회
        Optional<User> byId = userRepository.findById(userId);
        User user = byId.get();

        // 작성 시간과 작성자 정보 설정
        post.setCreatedDate(LocalDateTime.now());
        post.setUser(user);

        // 게시글 저장
        return postRepository.save(post);

    }

    //좋아요 메서드
    public void saveLike(Post post) {
        postRepository.save(post);
    }


    // ID로 게시글을 조회하는 메서드 (상세 페이지용)
    public Post findById(Integer id) throws Exception {

        Optional<Post> post = this.postRepository.findById(id);

        // 게시글 존재 여부 확인
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new Exception("post not found");
        }

    }

    // 조회수를 업데이트하는 메서드 (트랜잭션 적용)
    @Transactional
    public int updateView(Integer id) {

        return postRepository.updateViews(id);

    }

    // 이미지 URL이 널일 경우 기본 이미지를 사용하는 메서드
    public String postImgUrl(String imgUrl) {

        if (imgUrl.isEmpty()) {
            return "";  // 기본 이미지를 설정할 수 있음
        }

        return imgUrl;

    }

    // 주변 모임을 보여주는 메서드 (주소 기준)
    public List<Post> findByroadAddress(String keyword, int postId) {

        // 키워드에서 첫 두 단어 추출하여 주소 검색에 사용
        String[] locationParts = keyword.split(" ");
        String locationfix = locationParts[0] + " " + locationParts[1];

        // 주소를 기준으로 게시물 검색
        return postRepository.findByroadAddress(locationfix, postId);

    }

    // 제목을 기준으로 게시글을 검색하는 메서드
    public Page<Post> searchPostsByTitle(String title, Pageable pageable) {

        return postRepository.findByTitleContaining(title, pageable);

    }

    // 조회수가 높은 상위 게시글 목록을 가져오는 메서드
    public List<Post> findTopPostsByViews(int limit) {

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "viewCount"));

        return postRepository.findAll(pageable).getContent();

    }

    // 태그를 기준으로 게시글을 검색하는 메서드
    public Page<Post> searchPostByTag(String tag, Pageable pageable) {

        return postRepository.findByTagContaining(tag, pageable);

    }

    //포스트 삭제하는 메서드
    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    // 자기글 찾기 위한 메서드
    public List<Post> postSerch(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()){
            return null;
        }
        User user = ou.get();
        return postRepository.findByUser(user);
    }
}
