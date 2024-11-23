package Team6.Damoyeo.chat.controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import Team6.Damoyeo.chat.dto.ChatMessage;
import Team6.Damoyeo.chat.dto.ChatRoomDto;
import Team6.Damoyeo.chat.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final UserService userService;

    private final ChatService chatService;

    @GetMapping("/chat")
    public String chat(Model model, @SessionAttribute(name = "userId", required = false) Integer userId,
                       HttpServletRequest request) {

        if (userId == null) {
            return "redirect:/user/login";
        }

        User user = userService.findByUser(userId);
        String userNickName = userService.findByUserId(userId);

        // 사용자가 참여한 채팅방 목록 조회
        List<ChatRoomDto> chatRooms = chatService.getUserChatRooms(userId);

        // 모델에 데이터 추가
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("chatRooms", chatRooms);

        HttpSession session = request.getSession();
        session.setAttribute("userNickName", userNickName);

        return "chat/chat";
    }

    // 메시지 전송을 위한 메서드 (특정 방 지원)
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable("roomId") String roomId, ChatMessage chatMessage) {
        System.out.println("Received message in room " + roomId + ": "
                + chatMessage.getSender() + " - " + chatMessage.getContent());

        chatMessage.setType(ChatMessage.MessageType.CHAT);

        return chatMessage;
    }

    // 사용자 입장을 위한 메서드 (특정 방 지원)
    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage addUser(@DestinationVariable("roomId") String roomId, ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        System.out.println("User " + chatMessage.getSender() + " joined room " + roomId);

        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + "님이 채팅방에 입장했습니다.");

        return chatMessage;
    }
}