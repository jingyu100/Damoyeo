package Team6.Damoyeo.chat.controller;

import Team6.Damoyeo.User.Entity.User;
import Team6.Damoyeo.User.Service.UserService;
import Team6.Damoyeo.chat.Entity.ChatMessage;
import Team6.Damoyeo.chat.dto.ChatMessageDto;
import Team6.Damoyeo.chat.dto.ChatMessageHistoryDto;
import Team6.Damoyeo.chat.dto.ChatRoomDto;
import Team6.Damoyeo.chat.dto.ParticipantDto;
import Team6.Damoyeo.chat.service.ChatMessageService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final UserService userService;
    private final ChatService chatService;
    private final ChatMessageService chatMessageService;

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

        return "chat/chat";
    }

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDto sendMessage(@DestinationVariable("roomId") String roomId,
                                      ChatMessageDto chatMessageDto) {
        // 메시지 수신 로그 출력
        System.out.println("채팅방 " + roomId + "에서 메시지 수신: "
                + chatMessageDto.getSender() + " - " + chatMessageDto.getContent());

        try {
            // DB에 메시지 저장
            ChatMessage savedMessage = chatMessageService.saveChatMessage(chatMessageDto);

            // 메시지 타입을 채팅으로 설정
            chatMessageDto.setType(ChatMessageDto.MessageType.CHAT);

            return chatMessageDto;

        } catch (Exception e) {
            // 에러 발생 시 로그 출력
            System.err.println("메시지 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            // 에러가 발생해도 메시지는 전송되도록 처리
            chatMessageDto.setType(ChatMessageDto.MessageType.CHAT);
            return chatMessageDto;
        }
    }

    @MessageMapping("/chat.addUser/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDto addUser(@DestinationVariable("roomId") String roomId,
                                  ChatMessageDto chatMessageDto,
                                  SimpMessageHeaderAccessor headerAccessor) {
        // 사용자 입장 로그 출력
        System.out.println("사용자 " + chatMessageDto.getSender() + "가 채팅방 " + roomId + "에 입장");

        // WebSocket 세션에 사용자 이름 저장
        headerAccessor.getSessionAttributes().put("username", chatMessageDto.getSender());

        try {
            // 채팅방의 이전 메시지 내역 조회
            List<ChatMessage> chatMessages = chatMessageService.findChatMessageByChatRoom_id(Long.valueOf(roomId));

            // 채팅 메시지를 DTO로 변환
            List<ChatMessageHistoryDto> chatHistoryDtos = chatMessages.stream()
                    .map(message -> {
                        ChatMessageHistoryDto dto = new ChatMessageHistoryDto();
                        dto.setSender(message.getSender());
                        dto.setContent(message.getContent());
                        dto.setSentAt(message.getCreatedAt());
                        return dto;
                    })
                    .collect(Collectors.toList());

            // 입장 메시지 설정 및 채팅 히스토리 추가
            chatMessageDto.setType(ChatMessageDto.MessageType.JOIN);
            chatMessageDto.setContent(chatMessageDto.getSender() + "님이 채팅방에 입장했습니다");
            chatMessageDto.setChatHistory(chatHistoryDtos);

        } catch (Exception e) {
            // 에러 발생 시 빈 채팅 히스토리로 처리
            System.err.println("채팅 히스토리 로딩 중 오류 발생: " + e.getMessage());
            chatMessageDto.setType(ChatMessageDto.MessageType.JOIN);
            chatMessageDto.setContent(chatMessageDto.getSender() + "님이 채팅방에 입장했습니다");
            chatMessageDto.setChatHistory(new ArrayList<>());
        }

        return chatMessageDto;
    }

    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    public ChatMessageDto leaveRoom(@DestinationVariable String roomId, ChatMessageDto chatMessageDto) {
        // 사용자 퇴장 로그 출력
        System.out.println("사용자 " + chatMessageDto.getSender() + "가 채팅방 " + roomId + "에서 퇴장");

        // 퇴장 메시지 설정
        chatMessageDto.setType(ChatMessageDto.MessageType.LEAVE);

        return chatMessageDto;
    }

    @MessageMapping("/chat.getParticipants/{roomId}")
    @SendTo("/topic/chat/{roomId}/participants")
    public List<ParticipantDto> getParticipants(@DestinationVariable("roomId") String roomId) {
        // 채팅방의 현재 참가자 목록을 조회하는 로직
        return chatService.getRoomParticipants(roomId);
    }
}