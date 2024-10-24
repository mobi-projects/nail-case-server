package com.nailcase.config.chatting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
	@Value("${spring.rabbitmq.host}")
	private String rabbitHost;

	@Value("${spring.rabbitmq.username}")
	private String rabbitUsername;

	@Value("${spring.rabbitmq.password}")
	private String rabbitPassword;

	@Value("${spring.rabbitmq.stomp.port}")
	private int rabbitStompPort;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp/chat")
			.setAllowedOrigins(
				"http://localhost:3000",
				"https://nail-case-client.vercel.app",
				"http://localhost:8081"
			)
			.withSockJS()
			.setHeartbeatTime(25000) // 클라이언트->서버 heartbeat 주기
			.setDisconnectDelay(5000); // 연결 종료 대기 시간
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/pub");
		registry.enableStompBrokerRelay("/exchange", "/queue", "/topic")
			.setRelayHost(rabbitHost)
			.setRelayPort(rabbitStompPort)
			.setClientLogin(rabbitUsername)
			.setClientPasscode(rabbitPassword)
			.setSystemLogin(rabbitUsername)
			.setSystemPasscode(rabbitPassword)
			.setSystemHeartbeatSendInterval(5000)    // 시스템 heartbeat 전송 주기
			.setSystemHeartbeatReceiveInterval(4000);  // 시스템 heartbeat 수신 주기
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.setMessageSizeLimit(8192) // default : 64 * 1024
			.setSendBufferSizeLimit(10 * 1024 * 1024) // default : 512 * 1024
			.setSendTimeLimit(20 * 10000); // default : 10 * 10000
	}

}