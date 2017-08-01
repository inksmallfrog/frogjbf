<%--
  Created by IntelliJ IDEA.
  User: inksmallfrog
  Date: 17-7-27
  Time: 下午8:35
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h1>Hello ${ requestScope.name == null ? "frogJBF" : requestScope.name }</h1>
    <h2>Your favorite: </h2>
    <ul>
        <c:forEach items="${requestScope.favorite}"
            var="fav">
            <li>${fav}</li>
        </c:forEach>
    </ul>
    <form action="/frogJBF/hello" method="POST">
        <input type="text" name="name">
        <input type="checkbox" name="favorite" value="food">food
        <input type="checkbox" name="favorite" value="foot">foot
        <input type="submit" value="提交">
    </form>
</body>
</html>
