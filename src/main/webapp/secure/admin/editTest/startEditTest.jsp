<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Edit Test- TesterQuiz</title>
  <link rel="stylesheet" href="../../../styles.css">
</head>
<body>
<div class="page page--create-test">
  <header class="header header--auth">
    <h1 class="header__title">Edit Test</h1>
    <p class="header__subtitle">Start by editing basic test information</p>
  </header>
  <main class="main">
    <c:if test="${not empty error}">
      <div class="alert alert--error">
          ${error}
      </div>
      <c:remove var="error" scope="session"/>
    </c:if>
    <c:if test="${not empty success}">
      <div class="alert alert--success">
          ${success}
      </div>
      <c:remove var="success" scope="session"/>
    </c:if>

    <form action="/tests" method="post" class="form">
      <input type="hidden" name="_method" value="PUT">
      <div class="form__group">
        <label for="title" class="form__label">New Test title</label>
        <input id="title" name="title" type="text" required class="form__control"
               placeholder="Enter new test title">
      </div>

      <div class="form__group">
        <label for="topic" class="form__label">New Test topic</label>
        <input id="topic" name="topic" type="text" required class="form__control"
               placeholder="Enter new test topic">
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn--secondary" onclick="window.location.href='/secure/tests.jsp'">
          Отменить
        </button>
        <button type="button" class="btn btn--info" onclick="window.location.href='/secure/admin/editTest/editQuestions.jsp'">
          Продолжить без редактирования
        </button>
        <button type="submit" class="btn btn--primary">Редактировать title и topic</button>
      </div>
    </form>
  </main>
  <footer class="footer">
    © 2025 TesterQuiz
  </footer>
</div>

</body>
</html>