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
		var msg = data.data;
		if(msg != null && msg.trim() != ""){
			$("#chating").append("<p>" + msg + "</p>");
		}
	}
	
	document.addEventListener("keypress", function(e){
		if(e.keyCode == 13){
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
	var uN = $("#userName").val();
	var msg = $("#chatting").val();
	ws.send(uN + ":" + msg);
	$("#chatting").val("");
}