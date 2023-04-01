
var i = 1;
var paramSet = getParamSet();
pretreatment();

function pretreatment(){
	if(isUserFound){
		var button = document.getElementById('ban_button');
		button.style.display = 'block';
		updateBanButton(button);
	};
}

function getParamSet(){
	var result = {};
	var url = window.location.href;
	var paramQuery = url.split('?')[1];
	var paramSet = paramQuery.split('&');
	for(var i =0 ; i<paramSet.length; i++){
		var set = paramSet[i].split('=');
		var key = set[0];
		var value = set[1];
		result[key] = value;
	}
	
	return result;
}

function ban(){
	getConnection('/admin/ban',name);
	isBanUser = !isBanUser;
	var button = document.getElementById('ban_button');
	updateBanButton(button);
}

function updateBanButton(button){
	if(isBanUser){
		button.value = '복구하기';
	}else{
		button.value = '제제하기';
	}
}

function deleteItem(event){
	var itemName = event.target.id;
	getConnection('/admin/delete',itemName);
	event.target.parentNode.style.display = 'none';
}

function getConnection(url,param){
	var url = 'http://localhost:8080/orderer'+url+'?value='+param;
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
		if (xhr.readyState === xhr.DONE) {
            if (xhr.status === 200 || xhr.status === 201) {
            	//xhr.responseText
                window.alert("정상적으로 처리되었습니다.");
            }else{
            	window.alert("server error");
            }
        }
	}
	xhr.open("GET",url,false);
	xhr.send();
	xhr = null;
}