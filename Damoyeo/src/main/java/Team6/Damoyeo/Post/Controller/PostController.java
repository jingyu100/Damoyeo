package Team6.Damoyeo.Post.Controller;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Entity.PostRequest;
import Team6.Damoyeo.Post.Service.AlarmService;
import Team6.Damoyeo.Post.Service.PostRequestService;
import Team6.Damoyeo.Post.Service.PostService;
import Team6.Damoyeo.Post.dto.PostWithRequest;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import Team6.Damoyeo.calendar.service.CalendarService;
import Team6.Damoyeo.chat.service.ChatService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

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

    private final AlarmService alarmService;

    private final ChatService chatService;

    private final CalendarService calendarService;

    // 파일 업로드 경로 상수
//     private static final String UPLOAD_DIRECTORY = "src/main/resources/static/uploads/";
    private static final String UPLOAD_DIRECTORY = "/home/ec2-user/uploads/";

    @GetMapping("/main")
    public String showMainPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @SessionAttribute(name = "userId", required = false) Integer userId,
            Model model,
            @Nullable @RequestParam(name = "search") String search,
            @Nullable @RequestParam(name = "location") String location,
            @Nullable @RequestParam(name = "tag") String tag,
            @Nullable @RequestParam(name = "sort") String sort) {

        int pageSize = 8;  // 페이지당 게시물 수
        Page<Post> postPage;

        if (search != null && !search.isEmpty()) {
            postPage = postService.searchPostsByTitle(search, PageRequest.of(page, pageSize));
        } else {
            // 필터링과 정렬 조건을 적용한 검색
            postPage = postService.findFilteredPosts(location, tag, sort, PageRequest.of(page, pageSize));
        }
        // 현재 페이지의 게시물 목록
        List<Post> posts = postPage.getContent();
        // 다음 페이지 여부
        boolean hasNextPage = postPage.hasNext();
        // 프로필 사진 넣기를 위한 user생성
        User user = null;

        if (userId != null) {
            user = userService.findByUser(userId);
        }
        //모델에 데이터 추가
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        model.addAttribute("page", page);
        model.addAttribute("hasNextPage", hasNextPage);
        model.addAttribute("userId", userId);
        model.addAttribute("search", search);
        model.addAttribute("location", location);
        model.addAttribute("tag", tag);
        model.addAttribute("sort", sort);

        return "post/main";
    }

    // 게시물 작성 페이지
    @GetMapping("/create")
    public String createPost(Model model,
                             @SessionAttribute(name = "userId", required = false) Integer userId) {
        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }
        // API 키와 새 Post 객체를 모델에 추가
        model.addAttribute("apiKey", API_KEY);
        model.addAttribute("post", new Post());
        model.addAttribute("userId", userId);
        return "post/create";  // 게시물 작성 페이지로 이동

    }

    @PostMapping("/create")
    public String savePost(@ModelAttribute("post") Post post, @RequestParam("photo") MultipartFile file,
                           RedirectAttributes redirectAttributes,
                           BindingResult bindingResult,
                           @SessionAttribute(name = "userId", required = false) Integer userId) throws Exception {
        // 새글일때
        if (post.getMaxParticipants() < 2) {
            bindingResult.rejectValue("maxParticipants", "error.maxParticipants",
                    "최대 참가자 수는 2명 이상이어야 합니다.");
        }

        if (bindingResult.hasErrors()) {
            return "post/create";
        }

        // 포스트 기본값
        post.setStatus("1");
        post.setPhotoUrl("nullDefult.png");

        // 게시물 저장하여 ID 받기 
        Post savedPost = postService.savePost(post, userId);

        // 파일이 존재한다면
        if (!file.isEmpty()) {
            // 파일명 앞에 게시글 ID 추가
            String newFilename = savedPost.getPostId() + "_" + file.getOriginalFilename();

            // 파일을 저장할 경로 설정
            Path path = Paths.get(UPLOAD_DIRECTORY + newFilename);
            Files.createDirectories(path.getParent()); // 폴더가 없으면 생성
            file.transferTo(path);  // 파일 저장

            // 저장된 이미지의 URL을 Post 객체에 설정
            savedPost.setPhotoUrl(newFilename);
            postService.updatePost(savedPost);
        }

        // 채팅방 생성
        chatService.createChatRoomForPost(savedPost, userId);
        return "redirect:/post/main";  // 메인 페이지로 리다이렉트
    }

    // 게시물 수정 페이지
    @GetMapping("/mositipy/{id}")
    public String mositipyPost(Model model, @PathVariable("id") Integer id,
                               @SessionAttribute(name = "userId", required = false) Integer userId) throws Exception {
        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }
        Post post = postService.findById(id);
        User user = userService.findByUser(userId);
        if (post.getUser() != user) {
            return "redirect:/post/main";
        }

        // API 키와
        model.addAttribute("apiKey", API_KEY);
        model.addAttribute("post", post);


        return "post/create";  // 게시물 작성 페이지로 이동

    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("post") Post post,
                             BindingResult bindingResult,
                             @RequestParam("photo") MultipartFile file,
                             @SessionAttribute(name = "userId", required = false) Integer userId) throws Exception {
        // 로그인 체크
        if (userId == null) {
            return "redirect:/user/login";
        }

        // 기존 게시물 조회 및 권한 확인
        Post testPost = postService.findById(post.getPostId());
        User user = userService.findByUser(userId);
        if (testPost.getUser() != user) {
            return "redirect:/post/main";
        }

        // 참가자 수 검증
        if (post.getMaxParticipants() < testPost.getNowParticipants()) {
            bindingResult.rejectValue("maxParticipants", "error.maxParticipants",
                    "최대 참가자 수는 현재 참가자 수(" + testPost.getNowParticipants() + "명)보다 커야 합니다.");
        }

        if (bindingResult.hasErrors()) {
            return "post/create";
        }

        // 파일 업로드 처리
        if (!file.isEmpty()) {
            String newFilename = post.getPostId() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIRECTORY + newFilename);
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            post.setPhotoUrl(newFilename);
        } else {
            // 기존 이미지 유지
            post.setPhotoUrl(testPost.getPhotoUrl());
        }

        // 기존 정보 유지
        post.setStatus(testPost.getStatus());
        post.setNowParticipants(testPost.getNowParticipants());
        post.setUser(testPost.getUser());
        post.setCreatedDate(testPost.getCreatedDate());

        // 이건 게시글 상태가 변경 됬는데 그 게시글의 인원이나 끝나는 시간이 변경되면 상태를 다시 1로 변환
        if (!post.getEndDate().equals(LocalDateTime.now()) && (post.getNowParticipants() != post.getMaxParticipants())) {
            post.setStatus("1");
        }

        if (post.getNowParticipants() == post.getMaxParticipants()) {
            post.setStatus("2");
        }
        // 게시물 업데이트
        postService.updatePost(post);
        return "redirect:/post/main";
    }

    // 게시물 상세 페이지
    @GetMapping("/detail{id}")
    public String detailPost(Model model, @PathVariable("id") Integer id,
                             @SessionAttribute(name = "userId", required = false) Integer userId) throws Exception {

        Post post = this.postService.findById(id);  // ID로 게시물 찾기
        if (post.getStatus().equals("4")) {
            model.addAttribute("title", "삭제된 게시글");
            model.addAttribute("message", "삭제된 게시글 입니다. 메인 화면으로 이동합니다.");
            return "error_alert";
        }
        // 조회수 업데이트
        postService.updateView(id);

        // 현재 게시물의 주소및 포스트 상태가 1인  관련된 다른 게시물 조회
        List<Post> nearby = postService.findByroadAddress(post.getRoadAddress(), id);
        // 프로필 사진 넣기를 위한 user생성
        User user = null;

        if (userId != null) {
            user = userService.findByUser(userId);
        }

        //포스트 아이디랑 유저 아이디로 포스트 신청에서 찾기
        PostRequest postRequest = postRequestService.findClick(post, user);

        // 모델에 데이터 추가
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        model.addAttribute("userId", userId);
        model.addAttribute("nearby", nearby);
        model.addAttribute("postRequest", postRequest);
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

                // postId로 event 다 찾고 삭제
                calendarService.deleteEventByPostId(post);

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
    public ResponseEntity<String> joinPost(@PathVariable("id") Integer postId,
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

    //참가 취소
    @PostMapping("/out/{id}")
    @ResponseBody
    public ResponseEntity<String> outPost(@PathVariable("id") Integer postId,
                                          @RequestParam("user_id") Integer userId) throws Exception {
        Post post = postService.findById(postId);
        User user = userService.findByUser(userId);

        PostRequest postRequest = postRequestService.findClick(post, user);

        postRequestService.delete(postRequest);
        return ResponseEntity.ok("참여가 취소되었습니다.");
    }

    @GetMapping("/alarm")
    public String alarm(Model model, @SessionAttribute(name = "userId", required = false) Integer userId) {
        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }
        // 1. 모임 신청한 사람
        List<PostWithRequest> postsWithRequests = alarmService.getPostsWithRequests(userId);
        // 2. 모임 거절 당한거
        List<PostWithRequest> rejectedPostsWithRequests = alarmService.rejectedPostsWithRequests(userId);
        // 3. 모임 게시글이 삭제 당한거
        List<PostWithRequest> deltedPostWithRequests = alarmService.deltedPostsWithRequests(userId);
        // 4. 모임 게시글이 작성자가 탈퇴한거
        List<PostWithRequest> outPostsWithRequests = alarmService.userOutPostsWithRequests(userId);
        // 5. 강퇴 당한거
        List<PostWithRequest> kickOutPostsWithRequests = alarmService.kickOutPostsWithRequests(userId);

        model.addAttribute("postsWithRequests", postsWithRequests);
        model.addAttribute("rejectedPostsWithRequests", rejectedPostsWithRequests);
        model.addAttribute("deltedPostWithRequests", deltedPostWithRequests);
        model.addAttribute("outPostsWithRequests", outPostsWithRequests);
        model.addAttribute("kickOutPostsWithRequests", kickOutPostsWithRequests);
        return "post/alarm";
    }

    //모임참가 수락
    @PostMapping("/requestAccept/{id}")
    public String requestAccept(@PathVariable("id") Integer prId) {
        postRequestService.accept(prId);
        return "redirect:/post/alarm";
    }

    //모임 참가 거절
    @PostMapping("/requestRefusal/{id}")
    public String requestRefusal(@PathVariable("id") Integer prId) {
        postRequestService.refusal(prId);
        return "redirect:/post/alarm";
    }

    //모임 참가 거절 확인
    @PostMapping("/requestRejected/{id}")
    public String requestRejected(@PathVariable("id") Integer prId) {
        // 일단은 삭제임
        postRequestService.rejected(prId);
        return "redirect:/post/alarm";
    }

    //모임 강퇴 당했을때
    @PostMapping("/requestKick/{id}")
    public String requestKick(@PathVariable("id") Integer prId) {
        // 일단은 삭제임
        postRequestService.kicked(prId);
        return "redirect:/post/alarm";
    }

}
