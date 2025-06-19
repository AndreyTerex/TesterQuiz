
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Create Test - TesterQuiz</title>
    <link rel="stylesheet" href="../../../styles.css">
</head>
<body>
<div class="page page--create-test">
    <header class="header header--auth">
        <h1 class="header__title">Create New Test</h1>
        <p class="header__subtitle">Start by entering basic test information</p>
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

        <form action="/secure/tests" method="post" class="form">
            <div class="form__group">
                <label for="title" class="form__label">Test title</label>
                <input id="title" name="title" type="text" required class="form__control"
                       placeholder="Enter test title">
            </div>

            <div class="form__group">
                <label for="topic" class="form__label">Test topic</label>
                <input id="topic" name="topic" type="text" required class="form__control"
                       placeholder="Enter test topic">
            </div>

            <div class="form-actions">
                <button type="button" class="btn btn--secondary" onclick="window.location.href='/secure/menu'">
                    Cancel
                </button>
                <button type="submit" class="btn btn--primary">Next step</button>
            </div>
        </form>
    </main>
    <footer class="footer">
        © 2025 TesterQuiz
    </footer>
</div>
</body>
</html>
