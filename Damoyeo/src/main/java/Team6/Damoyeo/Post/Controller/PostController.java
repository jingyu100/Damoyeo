package Team6.Damoyeo.Post.Controller;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@PropertySource("classpath:config.properties")
public class PostController {

    // 좋아요 버튼 기능
    boolean like = false;

    @Value("${KAKAO_MAP_API_KEY}")
    private String API_KEY;

    private final PostService postService;
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/uploads/";

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

//    @GetMapping("/detail{id}")
//    public String detailPost(Model model, @PathVariable int id) {
//        return "post/detail";
//    }

    //상세 페이지
    @GetMapping("/detail{id}")
    public String detailPost(Model model, @PathVariable("id") Integer id) throws Exception {
        Post post = this.postService.findById(id);
        postService.updateView(id);
        model.addAttribute("post",post);
        model.addAttribute("apiKey", API_KEY);
        return "post/detail";
    }
    // 아직 사용자의 id 값 가져오는건 구현 안했음 ㅈㅅ
    @PostMapping("/create")
    public String savePost(@ModelAttribute("post") Post post, @RequestParam("photo") MultipartFile file,
                           RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/post/create"; // 업로드 실패 시 리다이렉트 경로 수정
        }

        try {
            // static/uploads 디렉토리에 파일 저장
            String uploadDir = UPLOAD_DIRECTORY;
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.createDirectories(path.getParent());  // 폴더가 없으면 생성
            file.transferTo(path);

            // 저장한 이미지의 URL을 Post 객체에 설정
            post.setPhotoUrl("/uploads/" + file.getOriginalFilename());

            // 게시물 저장
            postService.savePost(post);

            redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "사진 파일을 업로드 해주세요");
            return "redirect:/post/create"; // 업로드 실패 시 리다이렉트 경로 수정
        }

        return "redirect:/post/main";
    }



}
