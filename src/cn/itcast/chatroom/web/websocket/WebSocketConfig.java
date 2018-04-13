package cn.itcast.chatroom.web.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 
 * 说明：WebScoket配置处理器
 * 把处理器和拦截器注册到spring websocket中
 */
@Component("webSocketConfig")
//配置开启WebSocket服务用来接收ws请求
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	//注入处理器
	@Autowired
	private ChatWebSocketHandler webSocketHandler;
	
	@Autowired
	private ChatHandshakeInterceptor chatHandshakeInterceptor;

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		//添加一个处理器还有定义处理器的处理路径
		registry.addHandler(webSocketHandler, "/ws").addInterceptors(chatHandshakeInterceptor);
		/*
		 */
		registry.addHandler(webSocketHandler, "/ws/sockjs").addInterceptors(chatHandshakeInterceptor).withSockJS();
	}
	

}
