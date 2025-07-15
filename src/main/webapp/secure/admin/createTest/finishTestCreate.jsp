<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Test Saved - TesterQuiz</title>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<div class="page page--success">
    <header class="header header--auth">
        <h1 class="header__title">Success!</h1>
        <p class="header__subtitle">Your test has been saved</p>
    </header>

    <main class="main">
        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert--error">
                <c:out value="${sessionScope.error}"/>
            </div>
            <c:remove var="error" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert--success">
                <c:out value="${sessionScope.success}"/>
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>
        <div class="success-container">
            <div class="success-icon">
                <span class="icon-large">✅</span>
            </div>



            <div class="test-summary">
                <h2 class="test-name">"<c:out value="${sessionScope.currentTest.title}"/>"</h2>
                <div class="test-details">
                    <div class="detail-item">
                        <span class="detail-label">Topic:</span>
                        <span class="detail-value"><c:out value="${sessionScope.currentTest.topic}"/></span>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Total Questions:</span>
                        <span class="detail-value"><c:out value="${sessionScope.currentTest.questions.size()}"/></span>
                    </div>
                </div>
            </div>

            <div class="success-message">
                <p>Your test has been successfully saved to the system.</p>
                <p>Users can now access and take this test.</p>
            </div>

            <div class="action-buttons">
                <a href="/secure/menu" class="btn btn--large btn--primary">
                    Return to menu
                </a>
            </div>
        </div>
    </main>
</div>
</body>
</html>