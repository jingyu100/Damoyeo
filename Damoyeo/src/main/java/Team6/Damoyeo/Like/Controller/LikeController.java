package Team6.Damoyeo.Like.Controller;

import Team6.Damoyeo.Like.Entity.Like;
import Team6.Damoyeo.Like.Service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLike(@RequestBody Map<Like,Integer> likeRequest) {
        int postId = likeRequest.get("postId");
        int userId = likeRequest.get("userId");

        boolean success = likeService.addLike(postId,userId);
        return ResponseEntity.ok().body(Map.of("success",success));
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removeLike(@RequestBody Map<Like,Integer> likeRequest) {
        int postId = likeRequest.get("postId");
        int userId = likeRequest.get("userId");

        boolean success = likeService.removeLike(postId,userId);
        return ResponseEntity.ok().body(Map.of("success",success));
    }
}
