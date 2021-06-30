var ws;

function wsOpen(){
	// 웹소켓 전송시 현재 방 번호 넘기기
	ws = new WebSocket("ws://" + location.host + "/chating/" + $("#roomNumber").val());
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
		}else{
			var url = URL.createObjectURL(new Blob[msg]);
			$("#chating").append("<div class='img'><img class='msgImg' src="+url+"></div><div class='clearBoth'></div>");
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
	var option = {
		type: "message",
		roomNumber:$("#roomNumber").val(),
		sessionId: $("#sessionId").val(),
		userName: $("#userName").val(),
		msg: $("#chatting").val()
	}
	ws.send(JSON.stringify(option))
	$("#chatting").val("");
}

function fileSend(){
	var file = document.querySelector("#fileUpload").files[0];
	var fileReader = new FileReader();
	fileReader.onload = function(){
		var param = {
			type: "fileUpload",
			file: file,
			roomNumber: $("#roomNumber").val(),
			sessionId: $("#sessionId").val(),
			msg: $("#chatting").val(),
			userName: $("#userName").val()
		}
		ws.send(JSON.stringify(param));
	
	arrayBuffer = this.result;
	ws.send(arrayBuffer);
	};
	fileReader.readAsArrayBuffer(file);
}