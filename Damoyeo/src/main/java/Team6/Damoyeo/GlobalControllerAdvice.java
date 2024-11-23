package Team6.Damoyeo;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final UserService userService;
    
    @ModelAttribute("logInUser")
    public User addLoggedInUser(@SessionAttribute(name = "userId", required = false) Integer userId) {
        if (userId != null) {
            return userService.findByUser(userId);
        }
        return null;
    }
}
