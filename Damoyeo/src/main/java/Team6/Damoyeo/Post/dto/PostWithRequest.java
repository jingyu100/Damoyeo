package Team6.Damoyeo.Post.dto;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostWithRequest {
    private Post post;
    private List<PostRequest> requests;

}
