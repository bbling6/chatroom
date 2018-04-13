package cn.itcast.chatroom.web.websocket;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import cn.itcast.chatroom.dao.RecordsDao;
import cn.itcast.chatroom.domain.Records;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.HtmlUtils;

import cn.itcast.chatroom.domain.Message;
import cn.itcast.chatroom.domain.User;
import cn.itcast.utils.GsonUtils;


/**
 * 
 * 说明：WebSocket处理器
 * 
 */
@Component("chatWebSocketHandler")
public class ChatWebSocketHandler implements WebSocketHandler {

	@Autowired
	private RecordsDao recordsDao;

	//在线用户的SOCKETsession(存储了所有的通信通道)
	public static final Map<String, WebSocketSession> USER_SOCKETSESSION_MAP;
	
	//存储所有的在线用户
	static {
		USER_SOCKETSESSION_MAP = new HashMap<String, WebSocketSession>();
	}
	
	/**
	 * webscoket建立好链接之后的处理函数--连接建立后的准备工作
	 */
	
	public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
		
		//将当前的连接的用户会话放入MAP,key是用户编号
		User loginUser=(User) webSocketSession.getAttributes().get("loginUser");
		
		//判断是否已经登陆过的用户
		if(webSocketSession.getAttributes().get("having")!=null){
			System.out.println("***也走过这里了**");
			handleError(loginUser); //把旧的socket异常退出
		}
		
		
		USER_SOCKETSESSION_MAP.put(loginUser.getId(), webSocketSession);
		
		//群发消息告知大家
		Message msg = new Message(loginUser.getId(),loginUser.getNickname(),"-1",new Date());
		msg.setText("风骚的【"+loginUser.getNickname()+"】踩着轻盈的步伐来啦。。。大家欢迎！");


		
		//获取所有在线的WebSocketSession对象集合
		Set<Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
		
		//将最新的所有的在线人列表放入消息对象的list集合中，用于页面显示
		for (Entry<String, WebSocketSession> entry : entrySet) {
			msg.getUserList().add((User)entry.getValue().getAttributes().get("loginUser"));
		}
		
		//将消息转换为json
		TextMessage message = new TextMessage(GsonUtils.toJson(msg));
		
		//群发消息
		sendMessageToAll(message);

		
	}

	
	/**
     * 客户端发送服务器的消息时的处理函数，在这里收到消息之后可以分发消息
     */
	//处理消息：当一个新的WebSocket到达的时候，会被调用（在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理）
	public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> message) throws Exception {
		
		//如果消息没有任何内容，则直接返回
		if(message.getPayloadLength()==0)return;
		
		//反序列化服务端收到的json消息
		Message msg = GsonUtils.fromJson(message.getPayload().toString(), Message.class);
		msg.setDate(new Date());
		
		//处理html的字符，转义：
		String text = msg.getText();
		
		//转换为HTML转义字符表示
		String htmlEscapeText = HtmlUtils.htmlEscape(text);
		msg.setText(htmlEscapeText);
		System.out.println("消息（可存数据库作为历史记录）:"+message.getPayload().toString());

		//
		//判断是群发还是单发，并将聊天记录转存数据库中
		if(msg.getTo()==null||msg.getTo().equals("-1")){
			//群发
			sendMessageToAll(new TextMessage(GsonUtils.toJson(msg)));
			saveMessageToDB(msg); //更新数据库记录
		}else{
			//单发
			sendMessageToUser(msg.getTo(), new TextMessage(GsonUtils.toJson(msg)));
			saveMessage(msg);  //将信息更新到数据库
		}
	}

	
	/**
     * 消息传输过程中出现的异常处理函数
     * 处理传输错误：处理由底层WebSocket消息传输过程中发生的异常
     */
	public void handleTransportError(WebSocketSession webSocketSession, Throwable exception) throws Exception {
		// 记录日志，准备关闭连接
		System.out.println("Websocket异常断开:" + webSocketSession.getId() + "已经关闭");
		
		//一旦发生异常，强制用户下线，关闭session
		if (webSocketSession.isOpen()) {
			webSocketSession.close();
		}
		
		//获取异常的用户的会话中的用户编号
		User loginUser=(User)webSocketSession.getAttributes().get("loginUser");

		//群发消息告知大家,该用户发出的
		Message msg = new Message(loginUser.getId(),loginUser.getNickname(),"-1",new Date());
		
		//获取所有的用户的会话
		Set<Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
		
		//并查找出在线用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
		for (Entry<String, WebSocketSession> entry : entrySet) {
			if(entry.getKey().equals(loginUser.getId())){

				msg.setText("万众瞩目的【"+loginUser.getNickname()+"】已经退出。。。！");
				//把控制器 群发的消息，当做该用户群发的最后一句话。

				//清除在线会话
				USER_SOCKETSESSION_MAP.remove(entry.getKey());
				//记录日志：
				System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
				break;
			}
		}
		
		//并查找出在线用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
		for (Entry<String, WebSocketSession> entry : entrySet) {
			msg.getUserList().add((User)entry.getValue().getAttributes().get("loginUser"));
		}
		
		TextMessage message = new TextMessage(GsonUtils.toJson(msg));
		sendMessageToAll(message);
		saveMessageToDB(msg);
		
	}

	//自定义的重复登陆，异常处理函数
	public void handleError(User user) throws Exception {
		
		WebSocketSession webSocketSession = USER_SOCKETSESSION_MAP.get(user.getId());
		
		// 记录日志，准备关闭连接
		System.out.println("Websocket异常断开:" + webSocketSession.getId() + "已经关闭");
		
		//一旦发生异常，强制用户下线，关闭session
		if (webSocketSession.isOpen()){
			webSocketSession.close();
		}
	
	}
	
	/**
     * websocket链接关闭的回调
     * 连接关闭后：一般是回收资源等
     */
	public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
		// 记录日志，准备关闭连接
		System.out.println("Websocket正常断开:" + webSocketSession.getId() + "已经关闭");
		

		
		//获取异常的用户的会话中的用户编号
		User loginUser=(User)webSocketSession.getAttributes().get("loginUser");
		Set<Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();

		//群发消息告知大家，并存在数据库中
		Message msg = new Message(loginUser.getId(),loginUser.getNickname(),"-1",new Date());
		
		//并查找出该用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
		for (Entry<String, WebSocketSession> entry : entrySet) {
			if(entry.getKey().equals(loginUser.getId())){
				
				//群发消息告知大家
				msg.setText("万众瞩目的【"+loginUser.getNickname()+"】已经有事先走了，大家继续聊...");

				//清除在线会话列表中
				USER_SOCKETSESSION_MAP.remove(entry.getKey());
				
				//记录日志：
				System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
				break;
			}
		}
		
		//并查找出每个在线用户的WebSocketSession（会话），刷新在线用户列表
		for (Entry<String, WebSocketSession> entry : entrySet) {
			msg.getUserList().add((User)entry.getValue().getAttributes().get("loginUser"));
		}
		
		TextMessage message = new TextMessage(GsonUtils.toJson(msg));
		sendMessageToAll(message);
		saveMessageToDB(msg);
	}

	
	 /**
     * 是否支持处理拆分消息，返回true返回拆分消息
     */
	//是否支持部分消息：如果设置为true，那么一个大的或未知尺寸的消息将会被分割，并会收到多次消息（会通过多次调用方法handleMessage(WebSocketSession, WebSocketMessage). ）
	//如果分为多条消息，那么可以通过一个api：org.springframework.web.socket.WebSocketMessage.isLast() 是否是某条消息的最后一部分。
	//默认一般为false，消息不分割
	public boolean supportsPartialMessages() {
		return false;
	}

	/**
	 * 
	 * 说明：给某个人发信息
	 * @param id
	 * @param message
	 */
	private void sendMessageToUser(String id, TextMessage message) throws IOException{
		//获取到要接收消息的用户的session
		WebSocketSession webSocketSession = USER_SOCKETSESSION_MAP.get(id);
		
		if (webSocketSession != null && webSocketSession.isOpen()) {
			//发送消息
			webSocketSession.sendMessage(message);

		}
	}

	//保存群发信息到当前在线用户
	private void saveMessageToDB(Message message){

		Set<String> entrySet = USER_SOCKETSESSION_MAP.keySet();
		for(String sessionId:entrySet){

			//通过id查找记录
			Records records = recordsDao.getOneBySessionId(sessionId);

			//如果是新用户
			if(records.getRecordList()==null){
				List<Message> list = new ArrayList<Message>();
				records.setRecordList(list);
			}

			List<Message> recordList = records.getRecordList();
			recordList.add(message);

			recordsDao.findAndModify(records,recordList); //更新

		}
	}

	//保存群发信息到当前在线用户
	private void saveMessage(Message message){

			//通过id查找记录
			Records records = recordsDao.getOneBySessionId(message.getTo());

			//如果是新用户
			if(records.getRecordList()==null){
				List<Message> list = new ArrayList<Message>();
				records.setRecordList(list);
			}

			List<Message> recordList = records.getRecordList();
			recordList.add(message);

			recordsDao.findAndModify(records,recordList); //更新数据库

	}
	
	/**
	 * 
	 * 说明：群发信息：给所有在线用户发送消息
	 */
	private void sendMessageToAll(final TextMessage message){
		
		//对用户发送的消息内容进行转义
		//获取到所有在线用户的SocketSession对象
		Set<Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
		for (Entry<String, WebSocketSession> entry : entrySet) {
			
			//某用户的WebSocketSession
			final WebSocketSession webSocketSession = entry.getValue();
			
			//判断连接是否仍然打开的
			if(webSocketSession.isOpen()){
				
				//开启多线程发送消息（效率高）
				new Thread(new Runnable() {
					public void run() {
						try {
							if (webSocketSession.isOpen()) {

								webSocketSession.sendMessage(message);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}).start();
				
			}
		}
	}
	
}
