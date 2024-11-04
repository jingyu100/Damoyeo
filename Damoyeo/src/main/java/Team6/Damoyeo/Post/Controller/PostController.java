package Team6.Damoyeo.Post.Controller;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 메인 화면
    @GetMapping("/main")
    public String showMainPage(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        int pageSize = 6;
        Page<Post> postPage = postService.findPostsByPage(page, pageSize);
        List<Post> posts = postPage.getContent();

        // 다음 페이지가 있는지 여부를 체크
        boolean hasNextPage = postPage.hasNext();

        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("hasNextPage", hasNextPage);

        return "main";
    }
    @GetMapping("/create")
    public String createPost(Model model) {
        model.addAttribute("post", new Post());
        return "post/create";
    }

    @GetMapping("/detail{id}")
    public String detailPost(Model model, @PathVariable int id) {
        return "post/detail";
    
    }
    // 아직 사용자의 id 값 가져오는건 구현 안했음 ㅈㅅ
    @PostMapping("/create")
    public String savePost(@ModelAttribute("post") Post post) {
        postService.savePost(post);
        return "redirect:/post/main";
    }
}
