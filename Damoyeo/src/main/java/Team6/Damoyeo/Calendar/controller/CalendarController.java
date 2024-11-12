package Team6.Damoyeo.calendar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @GetMapping("main")
    public String main( @SessionAttribute(name = "userId", required = false)Integer userId,
                        Model model) {
        model.addAttribute("userId", userId);
        return "calendar/main";
    }

}
