package Team6.Damoyeo.User.Controller;

import Team6.Damoyeo.MailService;
import Team6.Damoyeo.Post.Entity.Post;
import Team6.Damoyeo.Post.Service.PostService;
import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private static final String UPLOAD_USER = "src/main/resources/static/uploads/";
    private final HttpSession httpSession;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;

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
        // 이미 로그인한 사용자인지 체크
        HttpSession session = request.getSession();
        if (session != null && session.getAttribute("userId") != null) {
            // 이미 로그인된 경우 홈페이지로 리다이렉트
            return "redirect:/";
        }
        String referer = request.getHeader("Referer");
        // 회원가입 페이지나 로그인 페이지에서 왔을 경우 리다이렉트 URL을 저장하지 않음
        if (referer != null && !referer.contains("/login") && !referer.contains("/register") && !referer.contains("/find_password") && !referer.contains("/find_email")) {
            request.getSession().setAttribute("redirectUrl", referer);
        }
        return "user/login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("userEmail") String userEmail,
                            @RequestParam("userPassword") String userPassword,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request,
                            Model model) {

        try {
            HttpSession session = request.getSession();

            // 유저 로그인 처리
            User user = userService.loginUser(userEmail, userPassword);

            //탈퇴한 계정이면 로그인 방지
            if (user.getStatus().equals("0")){
                model.addAttribute("title", "탈퇴한 계정");
                model.addAttribute("message","탈퇴한 계정 입니다. 메인으로 돌아갑니다. ");
                return "error_alert";
            }
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

    //사용자 프로필 확인용
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

        if (user.getStatus().equals("0")){
            model.addAttribute("title", "탈퇴한 계정 입니다.");
            model.addAttribute("message","메인 화면으로 이동합니다.");
            return "error_alert";
        }

        List<Post> posts = postService.postSerch(otherUserId);

        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        model.addAttribute("isOwner", userId.equals(otherUserId));
        model.addAttribute("posts", posts);

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
        existingUser.setInterests(user.getInterests());
        userService.updateUser(existingUser);

        // 프로필 페이지로 리다이렉트
        return "redirect:/user/userprofile" + userId;

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
    
    // 현재 비밀번호 확인 페이지 이동 메서드
    @GetMapping("/check_password")
    public String checkpassword() {
        return "user/check_password";
    }
    
    // 현재 비밀번호가 맞으면 설정 페이지로 이동
    @PostMapping("/setting")
    public String setting(@SessionAttribute(name = "userId", required = false) Integer userId,
                                @RequestParam("password") String password,
                                Model model) {

        User user = userService.findByUser(userId);

        if(userId == null) {
            return "redirect:/user/login";
        }


        if(userService.checkPassword(user, password)) {
            return "redirect:/user/setting";
        }else {
            model.addAttribute("error","비밀번호가 올바르지 않습니다");
            return "user/check_password";
        }
    }
    
    //개인정보설정 페이지로 이동
    @GetMapping("/setting")
    public String setting(Model model, @SessionAttribute(name = "userId", required = false) Integer userId) {
        if(userId == null) {
            return "redirect:/user/login";
        }

        User user = userService.findByUser(userId);
        if (user == null) {
            return "redirect:/user/login";
        }

        model.addAttribute("user", user);

        return "user/setting";
    }
    
    // 비밀번호 수정 메서드
    @PostMapping("/change_user")
    public String updatePassword(@SessionAttribute(name = "userId", required = false) Integer userId,
                                 Model model,
                                 @RequestParam("check_password") String check_password,
                                 @RequestParam("new_password") String new_password,
                                 @RequestParam("new_check_password") String new_check_password,
                                 @ModelAttribute User user) {
//                                 @RequestParam("email") String email) {

        User myuser = userService.findByUser(userId);
        
        //현재 비밀번호가 맞는지 체크
        if(!passwordEncoder.matches(check_password,myuser.getPassword())) {
            model.addAttribute("error","현재 비밀번호가 일치 하지 않습니다");
            return "user/setting";
        }
        
        // 현재 비밀번호와 새 비밀번호 비교
        if(check_password !=null && check_password.equals(new_password)) {
            model.addAttribute("error","현재 비밀번호와 새 비밀번호가 일치합니다");
            return "user/setting";
        }

        //새로운 비밀번호 확인 조건
        if(!new_password.equals(new_check_password)) {
            model.addAttribute("error","새 비밀번호가 일치 하지 않습니다");
            return "user/setting";
        }
        
        // 새로운 비밀번호 정규식
        if (!isValidPassword(new_password)) {
            model.addAttribute("error", "비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다");
            return "user/setting";
        }

        myuser.setPassword(passwordEncoder.encode(new_password));
        userService.updateUser(myuser);

        return "redirect:/user/check_password";
    }
    
    // 정규식 비밀번호 메서드
    public boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&^])[A-Za-z\\d@$!%*?&^]{8,}$";
        return password != null && password.matches(passwordPattern);
    }

    // 정규식 이메일 메서드
    public boolean isValidEmail(String email) {
        String EmailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return email != null && email.matches(EmailPattern);
    }

    // 이메일 변경 메서드
    @PostMapping("/change_Email")
    public String updateEmail(@SessionAttribute(name = "userId", required = false) Integer userId,
                              @RequestParam("email") String email,
                              @RequestParam("verificationCode") String verificationCode,
                              Model model) {

        if(userId == null) {
            return "redirect:/user/login";
        }
        System.out.println(userId + "     asdfasdfasdfasdfsadfasdfasdfasd     " + email + "   ahsdfuihasdiufasuidfhasuidfhiasdhfasi   " + verificationCode);
        User myuser = userService.findByUser(userId);

        // 이메일 변경 처리
        if (email != null && !email.equals(myuser.getEmail())) {
            // 이메일 유효성 검사
            if (!isValidEmail(email)) {
                model.addAttribute("error", "유효하지 않은 이메일 형식입니다.");
                return "user/setting";
            }

            // 이메일 중복 확인
            if (userService.emailCheck(email)) {
                model.addAttribute("error", "이미 사용 중인 이메일입니다.");
                return "user/setting";
            }

            // 인증번호 검증
            if (!verificationCode.equals(""+MailService.number)) {
                model.addAttribute("error", "인증번호가 올바르지 않습니다.");
                return "user/setting";
            }

            myuser.setEmail(email);
            userService.updateUser(myuser);
        }
        return "redirect:/user/check_password";
    }

    // 아이디 찾기 페이지로 이동
    @GetMapping("find_email")
    public String findUserEmail() {

        return "user/find_email";
    }

    // 아이디 찾는 메서드
    @PostMapping("find_email")
    public String findUserEmail(@RequestParam("name") String name,@RequestParam("phone") String phone, Model model) {
        Optional<User> user = userService.findByUserId(name,phone);

        if(user.isPresent()) {
            model.addAttribute("user", user.get());
            return "user/find_email";
        }else {
            model.addAttribute("error","사용자를 찾을 수 없습니다");
            return "user/find_email";
        }

    }
    
    //비밀번호 찾기 페이지로 이동
    @GetMapping("find_password")
    public String findUserPassword() {
        return "user/find_password";
    }
    
    //비밀번호 찾는 메서드
    @PostMapping("find_password")
    public String findUserPassword(@RequestParam("email") String email,
                                   @RequestParam("new_password") String new_password,
                                   @RequestParam("new_check_password") String new_check_password,
                                   Model model) {
        User user = userService.findByUserEmail(email);

        if(user == null) {
            model.addAttribute("error","유저를 찾을 수 없습니다");
            return "user/find_password";
        }

        //새로운 비밀번호 확인 조건
        if(!new_password.equals(new_check_password)) {
            model.addAttribute("error","새 비밀번호가 일치 하지 않습니다");
            return "user/find_password";
        }

        // 새로운 비밀번호 정규식
        if (!isValidPassword(new_password)) {
            model.addAttribute("error", "비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다");
            return "user/find_password";
        }

        user.setPassword(passwordEncoder.encode(new_password));
        userService.updateUser(user);
        return "redirect:/user/login";
    }


}
