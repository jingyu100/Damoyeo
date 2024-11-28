package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.Post.dto.PostWithRequest;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostRequestRepository postRequestRepository;

    // 자기 게시글에 모임 신청 한사람 알림 찾는 메서드
    public List<PostWithRequest> getPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        List<Post> postList = postRepository.findByUser(user);
        List<PostWithRequest> postWithRequests = new ArrayList<>();

        for (Post post : postList) {
            // 포스트에 연결된 요청의 상태가 0인거 조회(수락도 거절도 안한 상태가 0)
            List<PostRequest> posts = postRequestRepository.findByPostAndStatus(post, "0");
            if (!posts.isEmpty()) {
                // 존재한다면 dto 리스트에 새로운 객체로 넣음
                postWithRequests.add(new PostWithRequest(post, posts));
            }
        }
        return postWithRequests;
    }

    // 모임 신청 거절한거 알림 찾는 메서드
    public List<PostWithRequest> rejectedPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        // 요청 상태가 2인 거를 찾음 ( 요청 상태 2가 거절상태)
        List<PostRequest> rejectedRequests = postRequestRepository.findByUserAndStatus(user, "2");
        return getPostWithRequests(rejectedRequests);
    }

    // 모임 삭제했을때
    public List<PostWithRequest> deltedPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        // 요청 상태가 3인 거를 찾음 ( 요청 상태 3이 게시글 삭제한 상태)
        List<PostRequest> rejectedRequests = postRequestRepository.findByUserAndStatus(user, "3");
        return getPostWithRequests(rejectedRequests);
    }

    // 유저가 모임 탈퇴한 상황
    public List<PostWithRequest> userOutPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        // 요청 상태가 4인 거를 찾음 ( 유저가 탈퇴한 상태)
        List<PostRequest> rejectedRequests = postRequestRepository.findByUserAndStatus(user, "4");
        return getPostWithRequests(rejectedRequests);
    }

    // 유저가 모임 강퇴당한 상황
    public List<PostWithRequest> kickOutPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if (ou.isEmpty()) {
            return null;
        }
        User user = ou.get();
        // 요청 상태가 5인 거를 찾음 ( 유저가 탈퇴한 상태)
        List<PostRequest> rejectedRequests = postRequestRepository.findByUserAndStatus(user, "5");
        return getPostWithRequests(rejectedRequests);
    }
    
    // 중복이라 메서드 추출했음
    private List<PostWithRequest> getPostWithRequests(List<PostRequest> rejectedRequests) {
        List<PostWithRequest> rejectedPostWithRequests = new ArrayList<>();

        for (PostRequest request : rejectedRequests) {
            Post post = request.getPost();
            //request를 리스트로 변환해서 객체 생성
            PostWithRequest pwRequest = new PostWithRequest(post, Arrays.asList(request));
            rejectedPostWithRequests.add(pwRequest);
        }

        return rejectedPostWithRequests;
    }

}
