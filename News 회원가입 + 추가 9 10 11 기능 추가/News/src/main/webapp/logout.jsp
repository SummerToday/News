<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그아웃 완료</title>
</head>
<body>
	<%
		session.invalidate();
	%>
	<table align="center">
		<tr>
			<td>
				<b> 로그아웃을 완료하였습니다.</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center" height="50">
				<form method="post" action="login.jsp">
					<input type="submit" value="로그인하기">
				</form>
			</td>
		</tr>
	</table>
</body>
</html>
