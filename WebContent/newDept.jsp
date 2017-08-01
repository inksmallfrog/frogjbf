<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<h1>All Departments</h1>
<ul>
	<c:forEach items="${ requestScope.depts }"
		var="dept">
		<li>
			<span> id: ${dept.id} </span>
			<span> title: ${dept.title} </span>
		</li>	
	</c:forEach>
	<c:set var="pageUrl" value="/frogJBF/dept?page="/>
	<c:set var="lastPage" value="${requestScope.pageInfo.curPage-1}"/>
	<c:set var="nextPage" value="${requestScope.pageInfo.curPage+1}"/>
	<c:set var="lastPageUrl" value="${pageUrl}${lastPage}"/>
	<c:set var="nextPageUrl" value="${pageUrl}${nextPage}"/>
	<c:if test="${lastPage > 0 }"><a href="${lastPageUrl }">上一页</a></c:if>
	第${requestScope.pageInfo.curPage}页
	<c:if test="${nextPage <= requestScope.pageInfo.totalPage }"><a href="${nextPageUrl }">下一页</a></c:if>
</ul>
<form action="/frogJBF/dept" method="post">
	<input type="text" name="title">
	<input type="submit" value="提交">
</form>
</body>
</html>