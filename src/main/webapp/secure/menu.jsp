<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Main Menu - TesterQuiz</title>
  <link rel="stylesheet" href="../Styles.css">
</head>
<body>
<div class="page page--menu">
  <header class="header header--menu">
    <h1 class="header__title">Dashboard</h1>
    <p class="header__subtitle">Welcome back, <c:out value="${sessionScope.user.username}"/>!</p>
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

    <div class="menu-sections">
      <div class="menu-section__items">
        <a href="/secure/tests.jsp" class="menu-card">
          <div class="menu-card__icon">📝</div>
          <h3 class="menu-card__title">Take Test</h3>
          <p class="menu-card__description">Browse and take available tests</p>
        </a>
        <a href="/secure/results" class="menu-card">
          <div class="menu-card__icon">📊</div>
          <h3 class="menu-card__title">My Tests History</h3>
          <p class="menu-card__description">View result test history</p>
        </a>

        <c:if test="${sessionScope.user.role eq 'ADMIN'}">
          <a href="/secure/admin/createTest/createTestStart.jsp" class="menu-card">
            <div class="menu-card__icon">➕</div>
            <h3 class="menu-card__title">Add Test</h3>
            <p class="menu-card__description">Create new tests</p>
          </a>
          <a href="/secure/admin/stats" class="menu-card">
            <div class="menu-card__icon">📊</div>
            <h3 class="menu-card__title">Admin Statistics</h3>
            <p class="menu-card__description">View test statistics</p>
          </a>
        </c:if>

        <a href="/logout" class="menu-card menu-card--logout">
          <div class="menu-card__icon">🚪</div>
          <h3 class="menu-card__title">Log Out</h3>
          <p class="menu-card__description">Sign out of your account</p>
        </a>
      </div>
    </div>
  </main>
  <footer class="footer">
    © 2025 TesterQuiz
  </footer>
</div>
</body>
</html>