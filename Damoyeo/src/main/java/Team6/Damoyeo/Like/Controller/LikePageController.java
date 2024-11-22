package Team6.Damoyeo.Like.Controller;

import Team6.Damoyeo.Like.Service.LikeService;
import Team6.Damoyeo.Post.Entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikePageController {
    // LikeController 는 RestController 라서 LikePageController 따로 만들었음
    
    
    private final LikeService likeService;

    @GetMapping("/{userId}")
    public String getLikedPosts(@PathVariable("userId") int userId, Model model) {
        List<Post> likesPosts = likeService.findByLikedUser(userId);
        model.addAttribute("likesPosts", likesPosts);


        return "user/mylikepage";

    }
}
