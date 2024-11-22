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


    public List<PostWithRequest> getPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if(ou.isEmpty()){
            return null;
        }
        User user = ou.get();
        List<Post> postList = postRepository.findByUser(user);
        List<PostWithRequest> postWithRequests = new ArrayList<>();

        for (Post post : postList) {
            List<PostRequest> posts = postRequestRepository.findByPostAndStatus(post, "0");
            if (!posts.isEmpty()) {
                postWithRequests.add(new PostWithRequest(post, posts));
            }
        }
        return postWithRequests;
    }

    public List<PostWithRequest> rejectedPostsWithRequests(Integer userId) {
        Optional<User> ou = userRepository.findById(userId);
        if(ou.isEmpty()){
            return null;
        }
        User user = ou.get();
        List<PostRequest> rejectedRequests = postRequestRepository.findByUserAndStatus(user, "2");

        List<PostWithRequest> rejectedPostWithRequests = new ArrayList<>();

        for (PostRequest request : rejectedRequests) {
            Post post = request.getPost();
            PostWithRequest pwRequest = new PostWithRequest(post, Arrays.asList(request));
            rejectedPostWithRequests.add(pwRequest);
        }

        return rejectedPostWithRequests;
    }

}
