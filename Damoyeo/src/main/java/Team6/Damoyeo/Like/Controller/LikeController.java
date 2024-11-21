package Team6.Damoyeo.Like.Controller;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Like.Entity.LikeRequest;
import Team6.Damoyeo.Like.Service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {

    @Autowired
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable int postId, @RequestBody LikeRequest likeRequest) {
        int userId = likeRequest.getUserId();

        boolean liked = likeService.toggleLike(postId,userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success",true);
        response.put("liked",liked);
        response.put("likeCount", likeService.getLikeCount(postId)); // 좋아요 수 갱신

        return ResponseEntity.ok(response);
    }
}
