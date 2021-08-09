<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page errorPage = "error_view.jsp" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>
<table boader="1">
<tr>
<td>
<jsp:include page="${param.CONTENTPAGE}" flush="false" />
</td>
</tr>

</table>
</body>
</html>