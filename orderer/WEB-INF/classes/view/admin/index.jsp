<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script type="text/javascript">
	function parseBoolean(bool) { return bool==='true'; }
	var isUserFound = parseBoolean('${isFound}');
	var isBanUser = parseBoolean('${isBan}');
	var name = '${userName}';
</script>
<script type = "text/javascript" defer src="../view/admin/active.js"></script>
<script type = "text/javascript" defer src="./view/admin/active.js"></script>
<meta charset="UTF-8">
<title>관리자 접속 페이지</title>
</head>
<body>
	<form action = '#' name = "delete_manually" accept-charset = "UTF-8" method = 'get'>
		<div><label><input type='text' id='typping' name='search-text'/></label><input type='submit' value='검색'/></div>
	</form>
	<div style = 'width : 300px; height : 600px; border : solid #000000 1px; margin-top : 10px;'>
		<div id = 'title' style = 'width : 100%; height : 20px; background-color : #eeeeee; border-bottom : solid #000000 1px; position : relative'>
			<div style = 'width : 100%; height : 100%; position : absolute; text-align : center'>${userName}</div>
			<input type='button' id='ban_button' style='padding : 0px; width : 60px; height : 100%; right : 0px; display : block; position : absolute; display : none;' onclick = 'ban()'/>
		</div>
		<div style = 'width : 100%; height : 580px; overflow : auto;'>
			<ul id = 'item_list'>
				<c:forEach var="item" items="${itemNameList}" varStatus="status">
					<li><c:out value="${item}" /> <button id="${itemCodeList[status.index]}" type='button' style='margin-left : 10px' onclick='deleteItem(event)'>삭제하기</button></li>
				</c:forEach>
			</ul>
		</div>
	</div>
</body>
</html>