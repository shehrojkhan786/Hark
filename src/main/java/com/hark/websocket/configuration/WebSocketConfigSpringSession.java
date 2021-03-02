package com.hark.websocket.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfigSpringSession extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

	@Value("${hark.chat.relay.host}")
	private String relayHost;

	@Value("${hark.chat.relay.port}")
	private Integer relayPort;

	protected void configureStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/messages").setAllowedOrigins("*").withSockJS();
	}

	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableStompBrokerRelay("/queue/", "/topic/")
			.setUserDestinationBroadcast("/topic/unresolved.user.dest")
			.setUserRegistryBroadcast("/topic/registry.broadcast")
			.setRelayHost(relayHost)
			.setRelayPort(relayPort);

		registry.setApplicationDestinationPrefixes("/discussionRoom");
	}
	
//	@Override
//	public void configureClientInboundChannel(ChannelRegistration registration) {
//	     registration.interceptors(rmeSessionChannelInterceptor());
//	}
	
//	@Bean
//	public RmeSessionChannelInterceptor rmeSessionChannelInterceptor() {
//	   return new RmeSessionChannelInterceptor();
//	}
}
