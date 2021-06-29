package com.sun.chating.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
public class SocketHandler extends TextWebSocketHandler {
	
	// HashMap<String, WebSocketSession> sessionMap = new HashMap<>(); // 웹소켓 세션 담을 용도
	List<HashMap<String, Object>> rls = new ArrayList<>();
	private static final String FILE_UPLOAD_PATH = "C:/test/websocket/";
	static int fileUploadIdx = 0;
	static String fileUploadSession = "";
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		// 메시지 발송
		String msg = message.getPayload();
		JSONObject obj = JsonToObjectParser(msg);
		
		String rN = (String) obj.get("roomNumber");
		String msgType = (String) obj.get("type");
		HashMap<String, Object> temp = new HashMap<String, Object>();
		
		if(rls.size() > 0) {
			for(int i=0; i<rls.size(); i++) {
				String roomNumber = (String) rls.get(i).get("roomNumber"); // 세션 리스트에 저장된 방번호 get
				if(roomNumber.equals(rN)) {
					temp = rls.get(i); // 같은 값의 방이 존재하면 해당 방번호 세션리스트의 모든 object값 get
					fileUploadIdx = i;
					fileUploadSession = (String) obj.get("sessionId");
					break;
				}
			}

		if(!msgType.equals("fileUpload")) { // 메시지 타입이 파일 업로드 아닐 경우 전송
		// 해당 방 세션만 찾아서 메시지 발송
		for(String k : temp.keySet()) {
			if(k.equals("roomNumber")) { 
				continue;
			}
			WebSocketSession wss = (WebSocketSession) temp.get(k);
			if(wss != null) {
				try {
					wss.sendMessage(new TextMessage(obj.toJSONString()));
				}catch(IOException e) {
					e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	@Override
	public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
		ByteBuffer byteBuffer = message.getPayload();
		String fileName = "temp.jpg";
		File dir = new File(FILE_UPLOAD_PATH);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(FILE_UPLOAD_PATH, fileName);
		FileOutputStream out = null;
		FileChannel outChannel = null;
		
		try {
			byteBuffer.flip(); // byteBuffer 읽기 위한 세팅
			out = new FileOutputStream(file, true);
			outChannel = out.getChannel();
			byteBuffer.compact();
			outChannel.write(byteBuffer);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(out != null) {
					out.close();
				}
				if(outChannel != null) {
					outChannel.close();
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		byteBuffer.position(0); // 파일 저장하면서 position값이 변경되므로 0으로 초기화
		HashMap<String, Object> temp = rls.get(fileUploadIdx); // 파일쓰기 완료시 이미지 발송
		for(String k : temp.keySet()) {
			if(k.equals("roomNumber")) {
				continue;
			}
			WebSocketSession wss = (WebSocketSession) temp.get(k);
			try {
				wss.sendMessage(new BinaryMessage(byteBuffer)); // 초기화된 버퍼 발송
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
		

	@SuppressWarnings("unchecked")
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 소켓 연결
		super.afterConnectionEstablished(session);
		boolean flag = false;
		String url = session.getUri().toString();
		System.out.println(url);
		
		String roomNumber = url.split("/chating/")[1];
		int idx = rls.size(); // 방의 사이즈 조사
		
		if(rls.size() > 0) {
			for(int i=0; i<rls.size(); i++) {
				String rN = (String) rls.get(i).get("roomNumber");
				if(rN.equals(roomNumber)) {
					flag = true;
					idx = i;
					break;
				}
			}
		}
		if(flag) {
			HashMap<String, Object> map = rls.get(idx); 
			map.put(session.getId(), session); // 이미 존재하는 방일 경우, 세션만 추가
		}else {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roomNumber", roomNumber);
			map.put(session.getId(), session);
			rls.add(map);
		}

		JSONObject obj = new JSONObject();
		obj.put("type", "getId"); // 생성된 세션을 저장하면 발신 메시지의 타입은 getId라고 명시
		obj.put("sessionId", session.getId()); // 생성된 세션 ID값을 클라이언트단으로 발송
		session.sendMessage(new TextMessage(obj.toJSONString()));
		
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 소켓 종료
		if(rls.size() > 0) {
			for(int i=0; i<rls.size(); i++) {
				rls.get(i).remove(session.getId()); // 소켓 종료시 해당 세션값 삭제
			}
		}
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
