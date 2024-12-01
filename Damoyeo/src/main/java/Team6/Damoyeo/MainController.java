package Team6.Damoyeo;

import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final PostService postService;
    private final UserService userService;

    // 조회수 기준으로 내림차순 정렬해서 6개 노출
    @GetMapping("/")
    public String goMain(@SessionAttribute(name = "userId", required = false) Integer userId, Model model) {

        int limit = 6;

        List<Post> topPosts = postService.findTopPostsByViews(limit);

        User user = null;

        if (userId != null) {
            user = userService.findByUser(userId);
        }

        model.addAttribute("posts", topPosts);
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);

        return "main";

    }


    private final MailService mailService;

    // 각 이메일에 대한 인증번호를 저장할 Map
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    @PostMapping("/mailSend")
    public ResponseEntity<?> mailSend(@RequestParam("email") String mail) {
        System.out.println(mail);
        try {
            int number = mailService.sendMail(mail);
            // 해당 이메일의 인증번호 저장
            verificationCodes.put(mail, String.valueOf(number));
            log.info("Verification email sent to: " + mail);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            return ResponseEntity.badRequest().body("Failed to send email");
        }
    }

    @GetMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestParam(name="userNumber") String userNumber,
                                       @RequestParam(name="email") String email) {
        String savedNumber = verificationCodes.get(email);
        Map<String, Object> response = new HashMap<>();

        if (savedNumber == null) {
            response.put("verified", false);
            response.put("message", "인증번호가 만료되었습니다. 다시 시도해주세요.");
            return ResponseEntity.ok(response);
        }

        boolean isMatch = userNumber.equals(savedNumber);
        if (isMatch) {
            verificationCodes.remove(email);
            response.put("verified", true);
            response.put("message", "인증이 완료되었습니다.");
        } else {
            response.put("verified", false);
            response.put("message", "인증번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok(response);
    }


}
