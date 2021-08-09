<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "javax.servlet.ServletException" %>
<%@ page isErrorPage = "true" %>
<%
	Throwable rootCause = null;
	if (exception instanceof ServletException) {
    rootCause = ((ServletException)exception).getRootCause();
	} else {
	rootCause = exception.getCause();
	}
	if (rootCause != null) {
	rootCause.printStackTrace();
    do {
    	 rootCause = rootCause.getCause();
    } while(rootCause != null);
}
    
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>

</body>
</html>