package Team6.Damoyeo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class MainController {
    @GetMapping("/")
    public String goMain(@SessionAttribute(name = "userId", required = false)Integer userId , Model model) {
        model.addAttribute("userId", userId);
        return "main";
    }

}
