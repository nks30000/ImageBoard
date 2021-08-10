<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>

<%@ page contentType = "text/html; charset=euc-kr" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Map" %>
<%@ page import = "imageboard.Theme" %>
<%@ page import = "imageboard.ThemeManager" %>
<%@ page import = "imageboard.ThemeManagerException" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%!
    static int PAGE_SIZE = 5;
%>
<%
    String pageNum = request.getParameter("page");
    if (pageNum == null) pageNum = "1";
    int currentPage = Integer.parseInt(pageNum);
    
    String[] searchCond = request.getParameterValues("search_cond");
    String searchKey = request.getParameter("search_key");
    
    List whereCond = null;
    Map whereValue = null;
    
    boolean searchCondName = false;
    boolean searchCondTitle = false;
    
    if (searchCond != null && searchCond.length > 0 && searchKey != null) {
        whereCond = new java.util.ArrayList();
        whereValue = new java.util.HashMap();
        
        for (int i = 0 ; i < searchCond.length ; i++) {
            if (searchCond[i].equals("name")) {
                whereCond.add("NAME = ?");
                whereValue.put(new Integer(1), searchKey);
                searchCondName = true;
            } else if (searchCond[i].equals("title")) {
                whereCond.add("TITLE LIKE '%"+searchKey+"%'");
                searchCondTitle = true;
            }
        }
    }
    
    ThemeManager manager = ThemeManager.getInstance();
    
    int count = manager.count(whereCond, whereValue);
    int totalPageCount = 0; // 전체 페이지 개수를 저장한다.
    int startRow = 0, endRow = 0; // 시작 행과 끝 행의 개수를 구한다.
    if (count > 0) {
        totalPageCount = count / PAGE_SIZE;
        if (count % PAGE_SIZE > 0) totalPageCount++;
        
        startRow = (currentPage - 1) * PAGE_SIZE + 1;
        endRow = currentPage * PAGE_SIZE;
        if (endRow > count) endRow = count;
    }
    
    List list = manager.selectList(whereCond, whereValue, 
        startRow-1, endRow-1);
%>

<c:set var="list" value="<%= list %>" />
<c:if test="<%= searchCondTitle || searchCondName %>">
검색 조건:  [
    <c:if test="<%= searchCondTitle %>">제목</c:if>
    <c:if test="<%= searchCondName %>">이름</c:if>
    = ${param.search_key} ]
</c:if>

<c:if test="<%= count > 0 %>">
</c:if>
<jsp:forward page="/imageboard/list_view.jsp" />
<!DOCTYPE html>
<html>
<head>
<meta charset="EUC-KR">
<title>Insert title here</title>
</head>
<body>

</body>
</html>