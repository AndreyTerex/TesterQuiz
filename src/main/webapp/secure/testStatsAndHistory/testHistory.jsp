<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test History</title>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<div class="main-container">
    <div class="page-header-centered">
        <h2 class="page-title">Test Results History</h2>
    </div>
    <c:if test="${empty sessionScope.results}">
        <div class="alert alert--info">No results found.</div>
    </c:if>
    <c:if test="${not empty sessionScope.results}">
        <table id="testsTable">
            <thead>
                <tr>
                    <th>#</th>
                    <th>Test Name</th>
                    <th>Score</th>
                    <th>Success Rate</th>
                    <th>Date</th>
                    <th>Details</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="result" items="${sessionScope.results}" varStatus="status">
                    <tr>
                        <td>${status.index + 1}</td>
                        <td>${result.testTitle}</td>
                        <td>${result.score}</td>
                        <td> <c:choose>
                            <c:when test="${fn:length(result.answersInResults) > 0}">
                                <fmt:formatNumber value="${(result.score * 100.0) / fn:length(result.answersInResults)}" type="number" maxFractionDigits="1" />%
                            </c:when>
                            <c:otherwise>0%</c:otherwise>
                        </c:choose></td>
                        <td>
                            <fmt:parseDate value="${result.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                            <fmt:formatDate value="${parsedDate}" pattern="dd.MM.yyyy HH:mm" />
                        </td>
                        <td>
                            <form action="/secure/results/${result.id}" method="get">
                                <button type="submit" class="btn btn--primary">Details</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
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
</body>
</html>
