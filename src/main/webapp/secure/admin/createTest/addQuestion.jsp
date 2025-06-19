
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.Test" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Add Question - TesterQuiz</title>
  <link rel="stylesheet" href="../../../styles.css">
</head>
<body>
<div class="page page--create-test">
    <header class="header header--auth">
        <h1 class="header__title">Add Question</h1>
        <p class="header__subtitle">Adding to: ${sessionScope.currentTest.title}</p>
    </header>

    <div class="test-info-card">
        <div class="test-info-item">
            <span class="test-info-label">Topic:</span>
            <span class="test-info-value">${sessionScope.currentTest.topic}</span>
        </div>
        <div class="test-info-item">
            <span class="test-info-label">Questions added:</span>
            <span class="test-info-value">${sessionScope.currentTest.questions.size()}</span>
        </div>
    </div>
  <main class="main">
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert--error">
                ${sessionScope.error}
      </div>
        <c:remove var="error" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert--success">
                ${sessionScope.success}
      </div>
      <c:remove var="success" scope="session"/>
    </c:if>

      <form action="/secure/tests" method="post" class="form">
      <input type="hidden" name="_method" value="PATCH">

      <div class="form__group">
        <label for="question" class="form__label">Question Text</label>
        <input id="question" name="question" type="text" required class="form__control" placeholder="Enter question text">
      </div>

      <div class="form__group">
          <div class="alert alert--info">
              <strong>Note:</strong> You must mark at least one answer as correct
          </div>
      </div>

      <c:forEach var="i" begin="1" end="10">
        <div class="form__group">
          <label for="answer${i}" class="form__label">Answer ${i}</label>
          <input id="answer${i}" name="answer${i}" type="text" class="form__control" placeholder="Enter answer ${i}">
            <label class="checkbox-label">
            <input type="checkbox" name="correct${i}" value="true">
            <span class="checkbox-text">Correct</span>
          </label>
        </div>
      </c:forEach>

      <div class="form-actions">
        <button type="submit" class="btn btn--primary">Add Question</button>
      </div>
    </form>
  </main>
  <footer class="footer">
    © 2025 TesterQuiz
  </footer>
</div>
</body>
</html>