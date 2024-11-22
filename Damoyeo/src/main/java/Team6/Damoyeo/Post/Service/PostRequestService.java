package Team6.Damoyeo.Post.Service;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Repository.PostRepository;
import Team6.Damoyeo.Post.Repository.PostRequestRepository;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostRequestService {

    private final PostRequestRepository postRequestRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    public void saveRequest(Integer userId, Integer postId, String text) {
        PostRequest postRequest = new PostRequest();
        Optional<User> ou = userRepository.findById(userId);
        Optional<Post> po = postRepository.findById(postId);
        User user = null;
        Post post = null;
        if (ou.isPresent()) {
            user = ou.get();
        }
        if (po.isPresent()) {
            post = po.get();
        }
        postRequest.setUser(user);
        postRequest.setPost(post);
        postRequest.setStatus("0");
        postRequest.setMessage(text);
        postRequest.setCreatedDate(LocalDateTime.now());
        postRequestRepository.save(postRequest);
    }

    public PostRequest findClick(Post post, User user)  {

        Optional<PostRequest> pru = postRequestRepository.findByPostAndUser(post, user);
        if (pru.isEmpty()) {
            return null;
        }
        return pru.get();
    }

    public void delete(PostRequest postRequest) {
        postRequestRepository.delete(postRequest);
    }

    //수락했을때
    public void accet(Integer prId){
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
            pr.setStatus("1");
            pr.getPost().setNowParticipants(pr.getPost().getNowParticipants() + 1);
            postRequestRepository.save(pr);
            postRepository.save(pr.getPost());
        }
    }
    public void refusal(Integer prId){
        Optional<PostRequest> opr = postRequestRepository.findById(prId);
        if (opr.isPresent()) {
            PostRequest pr = opr.get();
            pr.setStatus("2");
            postRequestRepository.save(pr);
        }
    }
}
