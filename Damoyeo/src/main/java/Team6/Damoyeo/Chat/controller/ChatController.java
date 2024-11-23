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
        // 로그인 상태가 아니면 로그인 페이지로 리다이렉트
        if (userId == null) {
            return "redirect:/user/login";
        }

        // 사용자 정보와 닉네임 조회
        User user = userService.findByUser(userId);
        String userNickName = userService.findByUserId(userId);

        // 사용자가 참여한 채팅방 목록 조회
        List<ChatRoomDto> chatRooms = chatService.getUserChatRooms(userId);

        // 사용자 정보와 채팅방 목록을 모델에 추가
        model.addAttribute("userId", userId);
        model.addAttribute("user", user);
        model.addAttribute("chatRooms", chatRooms);

        // 세션에 사용자 닉네임 저장
        HttpSession session = request.getSession();
        session.setAttribute("userNickName", userNickName);

        // 채팅 페이지 반환
        return "chat/chat";
    }

    // 메시지 전송을 위한 메서드 (특정 방 지원)
    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable("roomId") String roomId, ChatMessage chatMessage) {
        // 채팅방 ID와 메시지 정보 출력
        System.out.println("채팅방 " + roomId + "에서 메시지 수신: "
                + chatMessage.getSender() + " - " + chatMessage.getContent());

        // 메시지 타입을 채팅 메시지로 설정
        chatMessage.setType(ChatMessage.MessageType.CHAT);

        // 전송된 메시지 반환
        return chatMessage;
    }

    // 사용자 입장을 위한 메서드 (특정 방 지원)
    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage addUser(@DestinationVariable("roomId") String roomId, ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // 사용자가 채팅방에 입장했다는 메시지 출력
        System.out.println("사용자 " + chatMessage.getSender() + "가 채팅방 " + roomId + "에 입장");

        // WebSocket 세션에 사용자 이름 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        // 입장 메시지 생성
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + "님이 채팅방에 입장했습니다");

        // 입장 메시지 반환
        return chatMessage;
    }

    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessage leaveRoom(@DestinationVariable String roomId, ChatMessage chatMessage) {
        // 사용자가 채팅방을 나갔다는 메시지 출력
        System.out.println("사용자 " + chatMessage.getSender() + "가 채팅방 " + roomId + "에서 퇴장");

        // 메시지 타입을 퇴장 메시지로 설정
        chatMessage.setType(ChatMessage.MessageType.LEAVE);

        // 퇴장 메시지 반환
        return chatMessage;
    }
}