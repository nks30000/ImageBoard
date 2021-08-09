<%@ page contentType = "text/html; charset=euc-kr" %>
<%
    request.setCharacterEncoding("euc-kr");
%>
<jsp:forward page="/board/template/template.jsp">
    <jsp:param name="CONTENTPAGE" value="/board/updateForm_view.jsp" />
</jsp:forward>
