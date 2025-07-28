<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Test Statistics</title>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<div class="main-container">
    <h2 class="page-title">Test Statistics</h2>
    <div class="attempts-info">
        Total Attempts: <span class="attempts-number">${attempts}</span>
    </div>
    <div class="main-menu-button">
        <form action="/secure/menu" method="get">
            <button type="submit" class="btn btn--primary">Return to Menu</button>
        </form>
    </div>
    <div class="table-container">

        <table id="testsTable">
            <thead>
            <tr>
                <th>Test Title</th>
                <th>Total Passed</th>
                <th>Max Possible Score</th>
                <th>Max User Score</th>
                <th>Last Passed</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${not empty stats}">
                    <c:forEach var="stat" items="${stats}">
                        <tr>
                            <td><c:out value="${stat.testTitle}"/></td>
                            <td><c:out value="${stat.totalPassed}"/></td>
                            <td><c:out value="${stat.totalQuestions}"/></td>
                            <td><c:out value="${stat.maxScore}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty stat.lastPassed}">
                                <fmt:parseDate value="${stat.lastPassed}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                                <fmt:formatDate value="${parsedDate}" pattern="dd.MM.yyyy HH:mm" />
                                    </c:when>
                                    <c:otherwise>-</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="5">No data to display</td>
                    </tr>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
        <div class="main-menu-button">
            <form action="/secure/menu" method="get">
                <button type="submit" class="btn btn--primary">Return to Menu</button>
            </form>
        </div>
        <div class="page-footer">
            <div class="footer">
                © 2025 TesterQuiz
            </div>
        </div>
    </div>
</div>

</body>
</html>
