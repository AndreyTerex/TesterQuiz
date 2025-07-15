<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Register - TesterQuiz</title>
  <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<div class="page page--auth">
    <header class="header header--auth">
        <h1 class="header__title">Join TesterQuiz</h1>
        <p class="header__subtitle">Create your account</p>
  </header>
  <main class="main">
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert--error">
            <c:out value="${sessionScope.error}"/>
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>
      <form action="/register" method="post" class="form form--register">
      <div class="form__group">
          <label for="username" class="form__label">Username</label>
          <input id="username" name="username" type="text" required class="form__control">
      </div>
      <div class="form__group">
          <label for="password" class="form__label">Password</label>
          <input id="password" name="password" type="password" required minlength="8" class="form__control">
      </div>
        <button type="submit" class="btn btn--primary btn--full">Create Account</button>
    </form>

    <div class="auth-switch">
      <span class="auth-switch__text">Already have an account?</span>
      <form action="/login" method="get" class="inline-form">
        <button type="submit" class="btn btn--link">Sign In</button>
      </form>
    </div>
  </main>
  <footer class="footer">
    © 2025 TesterQuiz
  </footer>
</div>
</body>
</html>
