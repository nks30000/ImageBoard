<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "javax.servlet.ServletException" %>
<%@ page import = "imageboard.service.rootCuase" %>
<%@ page isErrorPage = "true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
에러 발생
<%= exception.getMessage() %><br>
<% exception.printStackTrace(); %><br>
예외 추적:
<%= rootCause.getMessage() %><br>

</body>
</html>