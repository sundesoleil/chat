var ws;

function wsOpen(){
	ws = new WebSocket("ws://" + location.host + "/chating");
	wsEvt();
}

function wsEvt(){
	ws.onopen = function(data){
		// 소켓 오픈시 초기화 세팅
	}
	
	ws.onmessage = function(data){
		
		// 메시지 받으면 동작
		var msg = data.data;
		
		if(msg != null && msg.trim() != ""){
			
			var x = JSON.parse(msg);
			
			if(x.type == "getId"){
				var si = x.sessionId != null ? x.sessionId : "";
				if(si != ""){
					$("#sessionId").val(si);
				}
			}
			
			else if(x.type == "message"){
				if(x.sessionId == $("#sessionId").val()){
					$("#chating").append("<p class='me'>나: " + x.msg + "</p>");
				}else{
					$("#chating").append("<p class='others'>" + x.userName + ": " + x.msg + "</p>");
				}
				
			}else{
				console.warn("unknown type")
			}
		}
	}
	document.addEventListener("keypress", function(e){
		if(e.keycode == 13){
			send();
		}
	});
}

function chatName(){
	var userName = $("#userName").val();
	
	if(userName == null || userName.trim() == ""){
		alert("사용자 이름을 입력해주세요.");
		$("#userName").focus();
	}else{
		wsOpen();
		$("#yourName").hide();
		$("#yourMsg").show();
	}
}

function send(){
	var option = {
		type: "message",
		sessionId: $("#sessionId").val(),
		userName: $("#userName").val(),
		msg: $("#chatting").val()
	}
	ws.send(JSON.stringify(option));
	$("#chatting").val("");
}