<%@ page contentType = "text/html; charset=euc-kr" %>
<%
    request.setCharacterEncoding("euc-kr");
%>
<jsp:forward page="/board/template/template.jsp">
    <jsp:param name="CONTENTPAGE" value="/board/deleteForm_view.jsp" />
</jsp:forward>
