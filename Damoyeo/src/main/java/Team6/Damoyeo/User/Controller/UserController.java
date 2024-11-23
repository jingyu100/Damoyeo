package Team6.Damoyeo.User.Controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_USER = "src/main/resources/static/uploads/";
    private final HttpSession httpSession;

    // 회원가입 폼 페이지로 이동
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        // registerForm에서 post 전송할 때 User 타입 반환을 위함
        model.addAttribute("user", new User());

        return "user/register";
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               RedirectAttributes redirectAttributes) {

        // 이메일 중복 체크 에러 메시지
        if (userService.emailCheck(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "이미 사용 중인 이메일입니다.");
            return "redirect:/user/register";
        }

        // 가입일 기본값 설정
        user.setJoinDate(LocalDateTime.now());

        // 프로필 디폴트 사진 설정
        user.setPhotoUrl("nullDefult.png");

        // 회원가입 서비스 호출
        userService.registerUser(user);

        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        // 회원가입 페이지나 로그인 페이지에서 왔을 경우 리다이렉트 URL을 저장하지 않음
        if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
            request.getSession().setAttribute("redirectUrl", referer);
        }
        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("userEmail") String userEmail,
                            @RequestParam("userPassword") String userPassword,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();

            // 유저 로그인 처리
            User user = userService.loginUser(userEmail, userPassword);
            session.setAttribute("userId", user.getUserId());

            // 리다이렉트 URL 가져오기
            String redirectUrl = (String) session.getAttribute("redirectUrl");

            // 리다이렉트 URL이 없거나, 회원가입/로그인 페이지인 경우 홈으로 이동
            if (redirectUrl == null || redirectUrl.isEmpty() ||
                    redirectUrl.contains("/register") || redirectUrl.contains("/login")) {
                redirectUrl = "/";
            }

            // 세션에서 리다이렉트 URL 삭제
            session.removeAttribute("redirectUrl");

            redirectAttributes.addFlashAttribute("message", "로그인에 성공했습니다!");
            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("userEmail", userEmail);
            return "redirect:/user/login";
        }
    }

    // 이메일 중복 체크 API
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam("email") String email) {

        // 이메일 중복 여부 확인
        boolean exists = userService.isEmailExists(email);

        // 응답 데이터 설정
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);

    }

    // 이메일 중복 체크 API
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Boolean>> checkNickname(@RequestParam("nickname") String nickname) {

        // 이메일 중복 여부 확인
        boolean exists = userService.isNicknameExists(nickname);

        // 응답 데이터 설정
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);

    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {

        // 세션 종료
        HttpSession httpSession = request.getSession();
        httpSession.invalidate();

        return "redirect:/";

    }

    // 프로필 페이지로 이동
    @GetMapping("/myprofile")
    public String myProfile(Model model, @SessionAttribute(name = "userId", required = false) Integer userId) {
        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }
        // 세션에 저장된 userId로 사용자 정보 조회
        User user = userService.findByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("isOwner", true);

        return "user/profile";

    }
    
    //다른 사용자 프로필 확인용
    @GetMapping("/userprofile{otherUserId}")
    public String userProfile(@PathVariable("otherUserId") Integer otherUserId,
                              Model model,
                              @SessionAttribute(name = "userId", required = false) Integer userId) {

        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }

        // 대상 사용자 정보 조회
        User user = userService.findByUser(otherUserId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("isOwner", userId.equals(otherUserId));

        return "user/profile";
    }

    // 프로필 수정 폼 페이지로 이동
    @GetMapping("/editprofile")
    public String editProfile(Model model, @SessionAttribute(name = "userId", required = false) Integer userId) {
        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }
        // 세션에 저장된 userId로 사용자 정보 조회
        User user = userService.findByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);

        return "user/editprofile";

    }

    // 프로필 수정 요청 처리
    @PostMapping("/editprofile")
    public String updateProfile(@SessionAttribute(name = "userId") Integer userId,
                                @RequestParam("photo") MultipartFile file,
                                @ModelAttribute("user") User user) throws IOException {

        // userId로 기존 사용자 정보 가져오기
        User existingUser = userService.findByUser(userId);

        // 파일 업로드 여부 확인 파일이 존재하면
        if (!file.isEmpty()) {
            // 파일 업로드 경로 설정
            String uploadDir = UPLOAD_USER;
            Path path = Paths.get(uploadDir + file.getOriginalFilename());

            // 폴더 생성
            Files.createDirectories(path.getParent());
            file.transferTo(path);
            existingUser.setPhotoUrl(file.getOriginalFilename());
        } else {
            //파일이 존재 하지않다면 기존 파일 유지
            user.setPhotoUrl(existingUser.getPhotoUrl());
        }

        // 사용자 정보 업데이트
        existingUser.setNickname(user.getNickname());
        existingUser.setComment(user.getComment());
        existingUser.setArea(user.getArea());

        userService.updateUser(existingUser);

        // 프로필 페이지로 리다이렉트
        return "redirect:/user/profile";

    }

    @PostMapping("/delete")
    public String deleteUser(HttpServletRequest request, @SessionAttribute(name = "userId") Integer userId) {
        HttpSession session = request.getSession();
        session.invalidate();
        userService.deleteUser(userId);
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String myPage(Model model, @SessionAttribute(name = "userId", required = false) Integer userId){
        //혹시 url로 드갈수도 있으니 들어가면 로그인으로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }
        // 세션에 저장된 userId로 사용자 정보 조회
        User user = userService.findByUser(userId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "user/mypage";
    }
}
