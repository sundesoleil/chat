<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="/resources/css/reset.css">
<link rel="stylesheet" href="/resources/css/chat.css">
<script src="/resources/js/chat.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<title>Chating</title>
</head>
<body>
	<div id="container" class="container">
		<h1>채팅</h1>
		<div id="chating" class="chating"></div>
		<div id="yourName">
			<table class="inputTable">
				<tr>
					<th>사용자명</th>
					<th><input type="text" name="userName" id="userName"></th>
					<th><button onclick="chatName()" id="startBtn">이름등록</button></th>
				</tr>
			</table>
		</div>
			<div id="yourMsg">
				<table class="inputTable">
				<tr>
					<th>메시지</th>
					<th><input id="chatting" placeholder="메시지를 입력하세요"></th>
					<th><button onclick="send()" id="sendBtn">전송하기</button></th>
				</tr>
				</table>
			</div>
	</div>
</body>
</html>