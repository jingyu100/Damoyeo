package Team6.Damoyeo.Like.Controller;


import Team6.Damoyeo.Like.Service.LikeService;
import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import Team6.Damoyeo.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final PostService postService;
    private final UserService userService;



@PostMapping("/{postId}")
public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("postId") int postId, @RequestBody Map<String, Integer> request) throws Exception {

    int userId = request.get("userId");  // 클라이언트에서 전달된 userId
    String liked = likeService.toggleLike(postId, userId);  // 좋아요 토글 서비스 호출

    // 좋아요 후 게시물 데이터 갱신
    Post post = postService.findById(postId);
    int likeCount = post.getLikeCount();

    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("liked", liked);
    response.put("likeCount", likeCount);

    return ResponseEntity.ok(response);
}
}
