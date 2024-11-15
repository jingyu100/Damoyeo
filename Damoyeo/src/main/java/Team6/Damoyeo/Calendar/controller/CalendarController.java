package Team6.Damoyeo.calendar.controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {


    private final UserService userService;

    @GetMapping("main")
    public String main(@SessionAttribute(name = "userId", required = false) Integer userId,
                       Model model) {
        // 프로필 사진 넣기를 위한 user생성
        User user = null;

        if (userId != null) {
            user = userService.findByUser(userId);
        }
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);

        return "calendar/main";

    }

}
