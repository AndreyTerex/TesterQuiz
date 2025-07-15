<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1">
  <title>Edit Test- TesterQuiz</title>
  <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<div class="page page--create-test">
  <header class="header header--auth">
    <h1 class="header__title">Edit Test</h1>
    <p class="header__subtitle">Start by editing basic test information</p>
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

      <form action="/secure/tests/${sessionScope.currentTest.id}" method="post" class="form">
      <input type="hidden" name="_method" value="PUT">
      <div class="form__group">
        <label for="title" class="form__label">New Test title</label>
        <input id="title" name="title" type="text" class="form__control"
               placeholder="Enter new test title">
      </div>

      <div class="form__group">
        <label for="topic" class="form__label">New Test topic</label>
        <input id="topic" name="topic" type="text" class="form__control"
               placeholder="Enter new test topic">
      </div>

      <div class="form-actions">
        <button type="button" class="btn btn--secondary" onclick="window.location.href='/secure/tests.jsp'">
          Cancel
        </button>
        <button type="button" class="btn btn--info" onclick="window.location.href='editQuestionsMenu.jsp'">
          Continue without editing
        </button>
        <button type="submit" class="btn btn--primary">Edit title and topic</button>
      </div>
    </form>
  </main>
  <footer class="footer">
    © 2025 TesterQuiz
  </footer>
</div>

</body>
</html>