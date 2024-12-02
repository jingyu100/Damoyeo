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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.SessionAttribute;
import Team6.Damoyeo.calendar.repository.CalendarRepository;
import Team6.Damoyeo.calendar.Entity.CalendarEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRequestRepository postRequestRepository;
    private final CalendarRepository calendarRepository;

    // 특정 페이지에 해당하는 게시글을 가져오는 메서드
    public Page<Post> findPostsByPage(int page, int pageSize) {
//        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate")));
        return postRepository.findByStatus(PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "createdDate")), "1");
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
        postRepository.save(post);

        CalendarEvent calendarEvent = CalendarEvent.builder()
                .title(post.getTitle()) // 게시물 제목
                .description(post.getTitle()) // 게시물 설명
                .startTime(post.getEndDate())
                .endTime(post.getEndDate()) // 게시물의 종료 날짜 사용
                .createdDate(LocalDateTime.now()) // 이벤트 생성 날짜
                .user(post.getUser()) // 요청한 사용자
                .post(post)
                .build();
        calendarRepository.save(calendarEvent);

        return post;

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
        return postRepository.findTop4ByRoadAddressContainingAndPostIdNotAndStatusOrderByLikeCountDesc(locationfix, postId, "1");

    }

    // 제목을 기준으로 게시글을 검색하는 메서드
    public Page<Post> searchPostsByTitle(String title, Pageable pageable) {

//        return postRepository.findByTitleContaining(title, pageable);
        return postRepository.findByTitleContainingAndStatus(title, "1", pageable);
    }

    // 조회수가 높은 상위 게시글 목록을 가져오는 메서드
    public List<Post> findTopPostsByViews(int limit) {

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "viewCount"));

//        return postRepository.findAll(pageable).getContent();
        return postRepository.findByStatus(pageable, "1").getContent();
    }

    // 태그를 기준으로 게시글을 검색하는 메서드
    public Page<Post> searchPostByTag(String tag, Pageable pageable) {

//        return postRepository.findByTagContaining(tag, pageable);
        return postRepository.findByTagContainingAndStatus(tag, "1", pageable);
    }

    //포스트 삭제하는 메서드
    public void deletePost(Post post) {
        //삭제는 4 상태로
        post.setStatus("4");
        List<PostRequest> postRequests = postRequestRepository.findByPostAndStatusNot(post,"2");
        for (PostRequest postRequest : postRequests) {
            //게시글 삭제되면 요청은 상태 3로변환
            postRequest.setStatus("3");
            postRequestRepository.save(postRequest);
        }
        postRepository.save(post);
    }

    // 자기글 찾기 위한 메서드
    public List<Post> postSerch(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        //4를 제외한 게시글
        return postRepository.findByUserAndStatusNot(user, "4");
    }

    public void updatePost(Post post) {
        postRepository.save(post);
    }

    //1분 지날때마다 포스트 갱신
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void timePost() {
        LocalDateTime now = LocalDateTime.now();
        List<Post> endDateBeforeAndStatus = postRepository.findByEndDateBeforeAndStatus(now, "1");
        if (!endDateBeforeAndStatus.isEmpty()) {
            for (Post post : endDateBeforeAndStatus) {
                //스테이터스 3으로 한이유는 게시글 들어갔을떄 완료문을 다르게 하기 위해서
                post.setStatus("3");
                postRepository.save(post);

                // 해당 포스트의 상태가 "0"인 모든 요청 삭제
                List<PostRequest> postStatusZero = postRequestRepository.findByPostAndStatus(post, "0");
                postRequestRepository.deleteAll(postStatusZero);
            }
        }
    }

    public Page<Post> findFilteredPosts(String location, String tag, String sort, Pageable pageable) {
        Sort sorting;
        // 인자값 받은게 인기순이라면 안가슌 어나러묜 최신순
        if ("인기순".equals(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "likeCount", "postId");
        } else {
            // 우리는 최신으로 만든게 숫자가 더 크기 때문에 이게 가능
            sorting = Sort.by(Sort.Direction.DESC, "postId"); // 최신순
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorting);

        // 필터링 조건에 따른 쿼리 실행
        // 지역, 태그 가 널 아니면
        if (location != null && !location.isEmpty() && tag != null && !tag.isEmpty()) {
            return postRepository.findByStatusAndRoadAddressContainingAndTag("1", location, tag, pageable);
        }
        //지역만 널아니면
        else if (location != null && !location.isEmpty()) {
            return postRepository.findByStatusAndRoadAddressContaining("1", location, pageable);
        }
        //태그가 널아니면
        else if (tag != null && !tag.isEmpty()) {
            return postRepository.findByStatusAndTag("1", tag, pageable);
        }
        //다 널이면
        else {
            return postRepository.findByStatus(pageable, "1");
        }
    }
}
