<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="entity.Test" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Create Test - Choose Action</title>
  <link rel="stylesheet" href="../../../styles.css">
</head>
<body>
<div class="page page--menu">
  <header class="header header--auth">
    <h1 class="header__title">Test Creation</h1>
    <p class="header__subtitle">Choose your next action</p>
  </header>

  <div class="test-info-card">
    <h3 class="test-info-title">${sessionScope.currentTest.title}</h3>
    <div class="test-info-details">
      <div class="test-info-item">
        <span class="test-info-label">Topic:</span>
        <span class="test-info-value">${sessionScope.currentTest.topic}</span>
      </div>
      <div class="test-info-item">
        <span class="test-info-label">Questions added:</span>
        <span class="test-info-value">${sessionScope.currentTest.questions.size()}</span>
      </div>
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

      <div class="choice-container">
          <h3>Choose your action:</h3>

      <div class="choice-buttons">
        <div class="choice-option">
          <a href="${pageContext.request.contextPath}/secure/admin/createTest/addQuestion.jsp"
             class="btn btn--large btn--primary">
            <span class="btn-icon">➕</span>
            <span class="btn-text">
              <strong>Add Question</strong>
              <small>Continue creating test</small>
            </span>
          </a>
        </div>

        <div class="choice-option">
          <form action="${pageContext.request.contextPath}/secure/admin/editTest" method="post">
            <input type="hidden" name="action" value="finish">
            <button type="submit" class="btn btn--large btn--success">
              <span class="btn-icon">✅</span>
              <span class="btn-text">
                <strong>Finish Test</strong>
                <small>Save and publish</small>
              </span>
            </button>
          </form>
        </div>
      </div>

      <div class="status-info">
        <div class="status-card">
          <h4>Test Status</h4>
          <p>Minimum recommended questions: <strong>5</strong></p>
          <p>Current questions count: <strong>${sessionScope.currentTest.questions.size()}</strong></p>
          <c:choose>
            <c:when test="${sessionScope.currentTest.questions.size() >= 5}">
              <span class="status-badge status--good">✓ Ready for publication</span>
            </c:when>
            <c:otherwise>
              <span class="status-badge status--warning">⚠ Consider adding more questions</span>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </main>
</div>
</body>
</html>

