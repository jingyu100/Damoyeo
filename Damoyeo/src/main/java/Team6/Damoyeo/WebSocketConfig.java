package Team6.Damoyeo;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 클라이언트가 메시지를 받을 경로 설정
        config.enableSimpleBroker("/topic");

        // 클라이언트가 메시지를 보낼 경로 설정
        config.setApplicationDestinationPrefixes("/app");

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // WebSocket 엔드포인트 설정
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();

    }
}

