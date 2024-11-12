package Team6.Damoyeo;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PostService postService;

    // 조회수 기준으로 내림차순 정렬해서 6개 노출
    @GetMapping("/")
    public String goMain(@SessionAttribute(name = "userId", required = false) Integer userId, Model model) {
        int limit = 6;
        List<Post> topPosts = postService.findTopPostsByViews(limit);
        model.addAttribute("posts", topPosts);
        model.addAttribute("userId", userId);
        return "main";
    }


}
