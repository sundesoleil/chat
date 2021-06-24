package com.sun.chating.handler;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class SocketHandler extends TextWebSocketHandler {
	
	HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); // 웹소켓 세션 담을 용도
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		// 메시지 발송
		String msg = message.getPayload();
		JSONObject obj = JsonToObjectParser(msg);
		for(String key : sessionMap.keySet()) {
			WebSocketSession wss = sessionMap.get(key);
			try {
				wss.sendMessage(new TextMessage(msg));
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 소켓 연결
		super.afterConnectionEstablished(session);
		sessionMap.put(session.getId(), session);
		JSONObject obj = new JSONObject();
		obj.put("type", "getId"); // 생성된 세션을 저장하면 발신 메시지의 타입은 getId라고 명시
		obj.put("sessionId", session.getId()); // 생성된 세션 ID값을 클라이언트단으로 발송
		session.sendMessage(new TextMessage(obj.toJSONString()));
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 소켓 종료
		sessionMap.remove(session.getId());
		super.afterConnectionClosed(session, status);
	}
	
	private static JSONObject JsonToObjectParser(String jsonStr) { 
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject)parser.parse(jsonStr);
		}catch(ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
