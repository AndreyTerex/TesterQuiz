<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Test Passing</title>
  <link rel="stylesheet" href="../../styles.css">

</head>
<body>

<div class="main-container">
  <h2>Test Passing</h2>

  <c:if test="${not empty sessionScope.error}">
    <div class="alert alert--error">
        ${sessionScope.error}
    </div>
    <c:remove var="error" scope="session"/>
  </c:if>
  <c:if test="${sessionScope.testTimeOut == true}">
    <div class="alert alert--info">
      The test has ended because the allotted time has expired. Please return to the test menu.
    </div>
    <a href="/secure/tests.jsp" class="btn btn--primary">Go to Test Menu</a>
  </c:if>

  <c:if test="${not empty sessionScope.currentQuestion and not empty sessionScope.currentTest and sessionScope.testTimeOut != true}">
    <c:set var="timeForTest" value="${sessionScope.timeForTest}"/>
    <c:set var="currentQuestion" value="${sessionScope.currentQuestion}" />
    <c:set var="currentTest" value="${sessionScope.currentTest}" />

    <div class="timeForTest">
      <h2>Please complete the test by: <c:out value="${timeForTest}"/></h2>
    </div>

    <div class="form__group">
      <div class="alert alert--info">
        <strong>Note:</strong> You must mark at least one answer as correct
      </div>
    </div>

    <div class="test-info">
      <h3>Test: <c:out value="${currentTest.title}" /></h3>
      <p><strong>Question number:</strong> <c:out value="${currentQuestion.question_number}" /></p>
      <p><strong>Question text:</strong> <c:out value="${currentQuestion.question_text}" /></p>
    </div>

    <form action="/nextQuestion" method="post">
      <div class="answers">
        <c:forEach var="answer" items="${currentQuestion.answers}" varStatus="status">
          <div class="answer-option">
            <label>
              <input type="checkbox" name="selectedAnswers" value="${answer.id}">
              <c:out value="${answer.answer_text}" />
            </label>
          </div>
        </c:forEach>
      </div>

      <button type="submit" class="btn btn--primary">Next Question</button>
    </form>
  </c:if>

  <c:if test="${empty sessionScope.currentQuestion or empty sessionScope.currentTest}">
    <p>No test found. <a href="/secure/tests.jsp">Go to Tests</a></p>
  </c:if>

  <div class="page-footer">
    <a href="/secure/tests.jsp" class="cancel-button">Cancel and back to test menu</a>
    <div class="footer">
      © 2025 TesterQuiz
    </div>
  </div>
</div>

</body>
</html>