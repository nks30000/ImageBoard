<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Map" %>
<%@ page import = "imageboard.Theme" %>
<%@ page import = "imageboard.ThemeManager" %>
<%@ page import = "imageboard.ThemeManagerException" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="/imageboard/service/list.jsp"></jsp:include>
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>

<table border="1">
<tr>
    <td>
    	${list.startRow}-${list.endRow} 
		[${listModel.requestPage}/${list.count}]
    </td>
</tr>
</table>

<table border="1">
<tr>
    <td>이미지</td>
    <td>제목</td>
    <td>작성자</td>
    <td>작성일</td>
</tr>
<c:if test="${empty list}">
<tr>
    <td align="center">
    등록된 이미지가 없습니다.
    </td>
</tr>
</c:if>
<c:if test="${! empty list}">
<c:forEach var="theme" items="${list}">
<tr>
    <td><c:if test="${! empty theme.image}">
    <img src="/chap18/image/${theme.image}.small.jpg" width="50">
    </c:if></td>
    <td><a href="javascript:goView(${theme.id})">${theme.title}</a></td>
    <td>${theme.name}</td>
    <td>
    	<fmt:formatDate value="${theme.register}" pattern="yyyy-MM-dd" />
    </td>
</tr>
</c:forEach>
</c:if>
<tr>
    <td colspan="4"><a href="writeForm.jsp">[이미지등록]</a></td>
</tr>
</table>

<script language="JavaScript">
function goPage(pageNo) {
    document.move.action = "list_view.jsp";
    document.move.page.value = pageNo;
    document.move.submit();
}
function goView(id) {
    document.move.action = "read.jsp";
    document.move.id.value = id;
    document.move.submit();
}
</script>

<%-- <c:set var="count" value="<%= Integer.toString(count) %>" />
<c:set var="PAGE_SIZE" value="<%= Integer.toString(PAGE_SIZE) %>" />
<c:set var="currentPage" value="<%= Integer.toString(currentPage) %>" /> --%>

<c:if test="${count > 0}">
    <c:set var="pageCount" 
        value="${count / PAGE_SIZE + (count % PAGE_SIZE == 0 ? 0 : 1)}" />
    <c:set var="startPage" value="${currentPage - (currentPage % 10) + 1}" />
    <c:set var="endPage" value="${startPage + 10}" />
    
    <c:if test="${endPage > pageCount}">
        <c:set var="endPage" value="${pageCount}" />
    </c:if>
    <c:if test="${startPage > 10}">
        <a href="javascript:goPage(${startPage - 10})">[이전]</a>
    </c:if>
    <c:forEach var="pageNo" begin="${startPage}" end="${endPage}">
        <c:if test="${currentPage == pageNo}"><b></c:if>
        <a href="javascript:goPage(${pageNo})">[${pageNo}]</a>
        <c:if test="${currentPage == pageNo}"></b></c:if>
    </c:forEach>
    <c:if test="${endPage < pageCount}">
        <a href="javascript:goPage(${startPage + 10})">[다음]</a>
    </c:if>
</c:if>

<%-- <form name="move" method="post">
    <input type="hidden" name="id" value="">
    <input type="hidden" name="page" value="${currentPage}">
    <c:if test="<%= searchCondTitle %>">
    <input type="hidden" name="search_cond" value="title">
    </c:if>
    <c:if test="<%= searchCondName %>">
    <input type="hidden" name="search_cond" value="name">
    </c:if>
    <c:if test="${! empty param.search_key}">
    <input type="hidden" name="search_key" value="${param.search_key}">
    </c:if>
</form> --%>

<form name="search" action="list.jsp" method="post">
    <input type="checkbox" name="search_cond" value="title">제목
    <input type="checkbox" name="search_cond" value="name">이름
    <input type="text" name="search_key" value="" size="10">
    <input type="submit" value="검색">
    <input type="button" value="전체목록" 
           onClick="location.href='list_view.jsp?page=1'">;
</form>
</body>
</html>