<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Welcome</title>
    <link rel="stylesheet" href="../../styles.css">
</head>
<body>

<div class="main-container">
    <div class="page-header-centered">
        <h2 class="page-title">Test Information</h2>
    </div>
    <c:choose>
        <c:when test="${not empty sessionScope.currentTest}">
            <c:set var="currentTest" value="${sessionScope.currentTest}" />

            <div class="test-info">
                <h3>Test: <c:out value="${currentTest.title}" /></h3>
                <p><strong>Topic:</strong> <c:out value="${currentTest.topic}" /></p>
                <p><strong>Questions:</strong> ${fn:length(currentTest.questions)}</p>
            </div>

            <div class="time-section">
                <h4>You've got 10 mins to do this test. Good Luck!</h4>
            </div>

            <div>
                <form action="/secure/startTest" method="post" class="start-test-button">
                    <button type="submit" class="btn btn--primary btn--full">Start Test</button>
                </form>
            </div>
        </c:when>
        <c:otherwise>
            <div class="error">
                <p>No test found in session. Please select a test first.</p>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="page-footer">
        <a href="/secure/tests.jsp" class="cancel-button">Cancel and back to test menu</a>
        <div class="footer">
            © 2025 TesterQuiz
        </div>
</div>

</body>
</html>
