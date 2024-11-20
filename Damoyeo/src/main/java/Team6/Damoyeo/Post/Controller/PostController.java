package Team6.Damoyeo.Post.Controller;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostRequestService;
import Team6.Damoyeo.Post.Service.PostService;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@PropertySource("classpath:config.properties")
@Slf4j
public class PostController {

    // 좋아요 기능의 상태를 저장하는 변수
    boolean like = false;

    // Kakao 지도 API 키를 config.properties 파일에서 가져옴
    @Value("${KAKAO_MAP_API_KEY}")
    private String API_KEY;

    // Post 관련 서비스 클래스
    private final PostService postService;

    // Post 가입 신청 관련 서비스 클래스
    private final PostRequestService postRequestService;

    // User관련 서비스 클래스
    private final UserService userService;

    // 파일 업로드 경로 상수
    private static final String UPLOAD_DIRECTORY = "src/main/resources/static/uploads/";

    // 메인 페이지
    @GetMapping("/main")
    public String showMainPage(@RequestParam(name = "page", defaultValue = "0") int page,
                               @SessionAttribute(name = "userId", required = false) Integer userId, Model model,
                               @Nullable @RequestParam(name = "search") String search,
                               @RequestParam(name = "tag", required = false) String tag) {

        int pageSize = 6;  // 페이지당 게시물 수
        Page<Post> postPage;

        // 검색 조건에 따라 페이지 요청
        if (search != null && !search.isEmpty()) {
            postPage = postService.searchPostsByTitle(search, PageRequest.of(page, pageSize));
        } else if (tag != null && !tag.isEmpty()) {
            postPage = postService.searchPostByTag(tag, PageRequest.of(page, pageSize));
        } else {
            postPage = postService.findPostsByPage(page, pageSize);
        }

        List<Post> posts = postPage.getContent();  // 현재 페이지의 게시물 목록
        boolean hasNextPage = postPage.hasNext();  // 다음 페이지 여부
        // 프로필 사진 넣기를 위한 user생성
        User user = null;

        if (userId != null) {
            user = userService.findByUser(userId);
        }
        // 모델에 데이터 추가
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("hasNextPage", hasNextPage);
        model.addAttribute("userId", userId);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);

        return "post/main";  // 메인 페이지로 이동

    }

    // 게시물 작성 페이지
    @GetMapping("/create")
    public String createPost(Model model) {

        // API 키와 새 Post 객체를 모델에 추가
        model.addAttribute("apiKey", API_KEY);
        model.addAttribute("post", new Post());

        return "post/create";  // 게시물 작성 페이지로 이동

    }

    // 게시물 저장 처리
    @PostMapping("/create")
    public String savePost(@ModelAttribute("post") Post post, @RequestParam("photo") MultipartFile file,
                           RedirectAttributes redirectAttributes,
                           @SessionAttribute(name = "userId", required = false) Integer userId) throws IOException {

        // 파일 업로드 여부 확인
        if (file.isEmpty()) {
            post.setPhotoUrl("nullDefult.png");  // 기본 이미지 설정
        } else {

            // 파일을 저장할 경로 설정
            String uploadDir = UPLOAD_DIRECTORY;
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.createDirectories(path.getParent());  // 폴더가 없으면 생성
            file.transferTo(path);  // 파일 저장

            // 저장된 이미지의 URL을 Post 객체에 설정
            post.setPhotoUrl(file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());

        }

        // 게시물 저장
        postService.savePost(post, userId);

        return "redirect:/post/main";  // 메인 페이지로 리다이렉트

    }

    // 게시물 상세 페이지
    @GetMapping("/detail{id}")
    public String detailPost(Model model, @PathVariable("id") Integer id,
                             @SessionAttribute(name = "userId", required = false) Integer userId) throws Exception {

        Post post = this.postService.findById(id);  // ID로 게시물 찾기

        // 조회수 업데이트
        postService.updateView(id);

        // 현재 게시물의 주소와 관련된 다른 게시물 조회
        List<Post> nearby = postService.findByroadAddress(post.getRoadAddress(), id);
        // 프로필 사진 넣기를 위한 user생성
        User user = null;

        if (userId != null) {
            user = userService.findByUser(userId);
        }
        // 모델에 데이터 추가
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("userId", userId);
        model.addAttribute("nearby", nearby);
        model.addAttribute("apiKey", API_KEY);

        return "post/detail";  // 상세 페이지로 이동

    }

    //게시물 삭제
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable("id") Integer id,
                             @SessionAttribute(name = "userId", required = false) Integer userId) {
        try {
            Post post = postService.findById(id);

            if (post.getUser().getUserId().equals(userId)) {
                postService.deletePost(post);
                return "redirect:/post/main";
            } else {
                return "redirect:/post/main";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
    //참가 신청
    @PostMapping("/join/{id}")
    @ResponseBody
    public ResponseEntity<String> joinPost( @PathVariable("id") Integer postId,
                                            @RequestParam("message") String message,
                                            @RequestParam("user_id") Integer userId) {
        try {
            postRequestService.saveRequest(userId, postId, message);
            return ResponseEntity.ok("성공적으로 신청되었습니다.");
        } catch (Exception e) {
            log.error("Error in joinPost: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("오류가 발생했습니다: " + e.getMessage());
        }
    }
}
