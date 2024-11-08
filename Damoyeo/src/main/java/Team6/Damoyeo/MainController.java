package Team6.Damoyeo;

import jakarta.validation.constraints.Max;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String goMain(){
        return "main";
    }
}
