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

    // UserService 주입
    private final UserService userService;

    // 채팅 화면으로 이동하는 메서드
    @GetMapping("chat")
    public String chat(Model model, @SessionAttribute(name = "userId", required = false) Integer userId,
                       HttpServletRequest request) {

        // 로그인 확인: 사용자 ID가 없으면 로그인 페이지로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }

        // 사용자 닉네임을 가져와 세션과 모델에 설정
        HttpSession session = request.getSession();
        String userNickName = userService.findByUserId(userId);
        model.addAttribute("userId", userId);
        session.setAttribute("userNickName", userNickName);

        return "chat/chat";  // chat 페이지 반환

    }

    // 채팅 메시지를 전송하는 메서드
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(ChatMessage chatMessage) {

        return chatMessage;  // 메시지를 그대로 반환하여 모든 구독자에게 전송

    }

    // 새로운 사용자가 채팅에 참여할 때 호출되는 메서드
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor,
                               @SessionAttribute(name = "userId", required = false) Integer userId) {

        // 사용자 이름을 세션에 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        // 메시지 타입을 JOIN으로 설정하여 참가 메시지로 전환
        chatMessage.setType(ChatMessage.MessageType.JOIN);

        return chatMessage;  // 참가 메시지를 모든 구독자에게 전송

    }
}
