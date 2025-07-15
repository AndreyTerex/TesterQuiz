<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Result Details</title>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<div class="main-container">
    <div class="page-header-centered">
        <h2 class="page-title">Test Result Details</h2>
    </div>
    <c:if test="${empty result}">
        <div class="alert alert--info">No result data found.</div>
    </c:if>
    <c:if test="${not empty result}">
        <div class="test-stats">
            <h3>Test: <c:out value="${result.testTitle}" /></h3>
            <p><strong>Date:</strong>
                <fmt:parseDate value="${result.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDate" />
                <fmt:formatDate value="${parsedDate}" pattern="dd.MM.yyyy" />
            </p>
            <p><strong>Time:</strong>
                <fmt:parseDate value="${result.date}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedTime" />
                <fmt:formatDate value="${parsedTime}" pattern="HH:mm" />
            </p>
            <p><strong>Correct answers:</strong> <c:out value="${result.score}" /></p>
            <p><strong>Total questions:</strong> <c:out value="${fn:length(result.answersInResults)}" /></p>
            <p><strong>Success Rate:</strong>
                <c:choose>
                    <c:when test="${fn:length(result.answersInResults) > 0}">
                        <fmt:formatNumber value="${(result.score * 100.0) / fn:length(result.answersInResults)}" type="number" maxFractionDigits="1" />%
                    </c:when>
                    <c:otherwise>0%</c:otherwise>
                </c:choose>
            </p>
        </div>
        <h4>Questions and Answers</h4>
        <c:forEach var="answer" items="${result.answersInResults}" varStatus="qStatus">
            <div class="question-block">
                <p><strong>Q${qStatus.index + 1}:</strong> <c:out value="${answer.question.questionText}" /></p>
                <p><strong>Your answers:</strong>
                    <c:forEach var="ans" items="${answer.selectedAnswers}" varStatus="aStatus">
                        <c:out value="${ans.answerText}" />
                        <c:if test="${!aStatus.last}">, </c:if>
                    </c:forEach>
                </p>
            </div>
        </c:forEach>
    </c:if>
    <div class="main-menu-button">
        <form action="/secure/testStatsAndHistory/testHistory.jsp" method="get">
            <button type="submit" class="btn btn--primary">Back to History</button>
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
