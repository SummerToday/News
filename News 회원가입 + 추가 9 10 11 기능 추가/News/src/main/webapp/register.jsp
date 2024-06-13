<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
    rel="stylesheet"
    integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH"
    crossorigin="anonymous">
<script
    src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"
    integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz"
    crossorigin="anonymous"></script>
<script>
function validateForm() {
    var username = document.forms["signupForm"]["username"].value;
    var password = document.forms["signupForm"]["password"].value;
    var nickname = document.forms["signupForm"]["nickname"].value;

    if (username.length < 4 || username.length > 16) {
        alert("아이디는 4자에서 16자 이내로 입력해주세요.");
        return false;
    }

    if (password.length < 8 || !/^\d+$/.test(password)) {
        alert("패스워드는 8자 이상이고 숫자로만 입력해주세요.");
        return false;
    }

    if (nickname.length < 2 || nickname.length > 20) {
        alert("닉네임은 2자에서 20자 이내로 입력해주세요.");
        return false;
    }

    return true;
}
</script>
</head>
<body>
    <div class="container w-50 mt-5 mx-auto">
        <h2>회원가입</h2>
        <hr>
        <form name="signupForm" method="post" action="NewsController?action=signup" class="mt-3" onsubmit="return validateForm()">
            <div class="mb-3">
                <label for="username" class="form-label">아이디</label>
                <input type="text" class="form-control" id="username" name="username" placeholder="4~16자 이내의 아이디" required>
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">비밀번호</label>
                <input type="password" class="form-control" id="password" name="password" placeholder="8자 이상의 숫자 패스워드" required>
            </div>
            <div class="mb-3">
                <label for="nickname" class="form-label">닉네임</label>
                <input type="text" class="form-control" id="nickname" name="nickname" placeholder="2~20자 이내의 닉네임" required>
            </div>
            <button type="submit" class="btn btn-primary">회원가입</button>
        </form>
        <hr>
        <a href="login.jsp">로그인</a>
    </div>
</body>
</html>
