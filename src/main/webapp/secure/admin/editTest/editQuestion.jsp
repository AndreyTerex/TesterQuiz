<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:set var="questionId" value="${param.questionId}" />

<c:set var="currentQuestion" value="${null}" />
<c:if test="${not empty sessionScope.currentTest and not empty sessionScope.currentTest.questions}">
    <c:forEach var="question" items="${sessionScope.currentTest.questions}">
        <c:if test="${question.id == questionId}">
            <c:set var="currentQuestion" value="${question}" />
        </c:if>
    </c:forEach>
</c:if>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Edit Question - TesterQuiz</title>
    <link rel="stylesheet" href="../../../styles.css">
</head>
<body>
<div class="page page--create-test">
    <header class="header header--auth">
        <h1 class="header__title">Edit Question</h1>
        <p class="header__subtitle">Test: <c:out value="${sessionScope.currentTest.title}"/></p>
    </header>

    <main class="main">
        <c:if test="${not empty sessionScope.error}">
            <div class="alert alert--error"><c:out value="${sessionScope.error}"/></div>
            <c:remove var="error" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert--success"><c:out value="${sessionScope.success}"/></div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <div class="return-btn-container">
            <button class="nav-btn" onclick="window.location.href='editQuestionsMenu.jsp'">← Back to Questions</button>
        </div>

        <c:if test="${not empty currentQuestion}">
            <div class="current-data">
                <h3>Current Question Data:</h3>
                <p><strong>Question <c:out value="${currentQuestion.questionNumber}"/>:</strong> <c:out value="${currentQuestion.questionText}"/></p>
                <p><strong>Answers:</strong></p>
                <ul>
                    <c:forEach var="answer" items="${currentQuestion.answers}" varStatus="status">
                        <li>
                            <c:out value="${answer.answerText}"/>
                            <c:if test="${answer.correct}"> ✓</c:if>
                        </li>
                    </c:forEach>
                </ul>
            </div>

            <form action="/secure/admin/editTest" method="post" class="form">
                <input type="hidden" name="questionId" value="${currentQuestion.id}">
                <input type="hidden" name="_method" value="PATCH">

                <div class="form__group">
                    <label for="question" class="form__label">New Question Text</label>
                    <input id="question" name="question" type="text" required class="form__control" 
                           value="<c:out value="${currentQuestion.questionText}"/>">
                </div>

                <div class="alert alert--info">
                    <strong>Note:</strong> Mark at least one answer as correct
                </div>

                <c:forEach var="i" begin="1" end="10">
                    <c:set var="answerIndex" value="${i - 1}" />
                    <c:set var="currentAnswer" value="${null}" />
                    <c:if test="${answerIndex < currentQuestion.answers.size()}">
                        <c:set var="currentAnswer" value="${currentQuestion.answers[answerIndex]}" />
                    </c:if>

                    <div class="form__group">
                        <label for="answer${i}" class="form__label">Answer ${i}</label>
                        <c:choose>
                            <c:when test="${not empty currentAnswer}">
                                <input id="answer${i}" name="answer${i}" type="text" class="form__control" 
                                       value="<c:out value="${currentAnswer.answerText}"/>">
                            </c:when>
                            <c:otherwise>
                                <input id="answer${i}" name="answer${i}" type="text" class="form__control" 
                                       value="">
                            </c:otherwise>
                        </c:choose>
                        <label class="checkbox-label">
                            <c:choose>
                                <c:when test="${not empty currentAnswer and currentAnswer.correct}">
                                    <input type="checkbox" name="correct${i}" value="true" checked>
                                </c:when>
                                <c:otherwise>
                                    <input type="checkbox" name="correct${i}" value="true">
                                </c:otherwise>
                            </c:choose>
                            <span class="checkbox-text">Correct Answer</span>
                        </label>
                    </div>
                </c:forEach>

                <div class="form-actions">
                    <button type="button" class="btn btn--secondary" onclick="window.location.href='editQuestionsMenu.jsp'">Cancel</button>
                    <button type="submit" class="btn btn--primary">Update Question</button>
                </div>
            </form>
        </c:if>

        <c:if test="${empty currentQuestion}">
            <div class="alert alert--error">
                <p>Question not found!</p>
                <button class="btn btn--primary" onclick="window.location.href='editQuestionsMenu.jsp'">Back to Questions</button>
            </div>
        </c:if>
    </main>

    <footer class="footer">© 2025 TesterQuiz</footer>
</div>
</body>
</html>
