<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Result</title>
    <link rel="stylesheet" href="../../styles.css">

</head>
<body>

<div class="main-container">
    <h2>Test Result</h2>

    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert--error"><c:out value="${sessionScope.error}"/></div>
        <c:remove var="error" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert--success"><c:out value="${sessionScope.success}"/></div>
        <c:remove var="success" scope="session"/>
    </c:if>


    <c:if test="${not empty sessionScope.currentQuestion and not empty sessionScope.result}">

        <c:set var="currentTest" value="${sessionScope.currentTest}" />
        <c:set var="result" value="${sessionScope.result}" />


        <div class="test-stats">
            <h3>Test: <c:out value="${currentTest.title}" /></h3>
            <p><strong>Questions :</strong> <c:out value="${fn:length(currentTest.questions)}" /></p>
            <p><strong>Correct answers :</strong> <c:out value="${result.score}" /></p>
            <p><strong>Success Rate :</strong>
                <c:out value="${(result.score / fn:length(currentTest.questions)) * 100}" />%
            </p>
        </div>

        <div class="main-menu-button">
            <form action="/secure/tests/${currentTest.id}/submit" method="post">
                <button type="submit" class="btn btn--primary">Save result</button>
            </form>
        </div>
        <div class="page-footer">
            <form action="/secure/menu" method="get">
                <button type="submit" class="btn cancel-button">Cancel and back to main menu</button>
            </form>
        </div>
    </c:if>

    <c:if test="${empty sessionScope.currentQuestion or empty sessionScope.currentTest}">
        <p>No test found. <a href="/secure/tests.jsp">Go to Tests</a></p>
    </c:if>

    <div class="page-footer">
        <div class="footer">
            © 2025 TesterQuiz
        </div>
    </div>
</div>

</body>
</html>