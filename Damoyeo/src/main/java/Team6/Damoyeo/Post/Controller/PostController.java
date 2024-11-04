package Team6.Damoyeo.Post.Controller;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 메인 화면
    @GetMapping("/main")
    public String showMainPage(Model model) {
        // 모든 게시글을 가져와 모델에 추가
        List<Post> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);
        return "main";
    }
}
