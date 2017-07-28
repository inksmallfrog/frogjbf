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
</ul>
<form action="/frogJBF/dept" method="post">
	<input type="text" name="title">
	<input type="submit" value="提交">
</form>
</body>
</html>