package Team6.Damoyeo.chat.controller;

import Team6.Damoyeo.User.Service.UserService;
import Team6.Damoyeo.chat.dto.ChatMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;

    @GetMapping("chat")
    public String chat(Model model, @SessionAttribute(name = "userId", required = false) Integer userId,
                       HttpServletRequest request) {
        HttpSession session = request.getSession();
        String userNickName = userService.findByUserId(userId);
        model.addAttribute("userId", userId);
        session.setAttribute("userNickName", userNickName);
        return "chat/chat";
    }


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor,
                               @SessionAttribute(name = "userId", required = false) Integer userId) {
        // 세션에 사용자 이름을 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        return chatMessage;
    }

}

