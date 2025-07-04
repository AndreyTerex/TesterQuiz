<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<c:set var="user" value="${sessionScope.user}" />
<c:set var="role" value="${not empty user and not empty user.role ? user.role : ''}" />
<c:set var="isAdmin" value="${role == 'ADMIN'}" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Questions for edit</title>
    <link rel="stylesheet" href="../../../styles.css">
</head>
<body>
<div>
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

    <div class="page-header-centered">
        <h2 class="page-title">Edit Questions</h2>
        <c:if test="${not empty sessionScope.currentTest}">
            <h3 class="page-subtitle">Test: <c:out value="${sessionScope.currentTest.title}"/></h3>
        </c:if>
    </div>

    <div class="return-btn-container">
        <button class="nav-btn" onclick="window.location.href='/secure/menu'">Return to Menu</button>
    </div>

    <c:if test="${not empty sessionScope.currentTest and not empty sessionScope.currentTest.questions}">
        <div class="table-container">
            <table class="questions-table">
                <thead>
                    <tr>
                        <th>Question Number</th>
                        <th>Question Text</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="question" items="${sessionScope.currentTest.questions}" varStatus="status">
                        <tr>
                            <td class="question-number"><c:out value="${question.questionNumber}"/></td>
                            <td class="question-text"><c:out value="${question.questionText}"/></td>
                            <td class="actions">
                                <button class="edit-btn" onclick="editQuestion('${question.id}')">Edit Question</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>

    <c:if test="${empty sessionScope.currentTest}">
        <div class="alert alert--error">
            No test selected for editing. Please select a test first.
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.currentTest and empty sessionScope.currentTest.questions}">
        <div class="alert alert--info">
            This test has no questions yet. Add some questions to start editing.
        </div>
    </c:if>

    <c:if test="${not empty sessionScope.currentTest}">
        <div class="add-question-container">
            <button class="btn btn--primary" onclick="addQuestion()">Add Questions</button>
        </div>
    </c:if>

    <footer class="footer">
        © 2025 TesterQuiz
    </footer>

</div>

<script>
function editQuestion(questionId) {
    window.location.href = '/secure/admin/editTest/editQuestion.jsp?questionId=' + questionId;
}

function addQuestion() {
    window.location.href = '/secure/admin/createTest/addQuestion.jsp';
}
</script>

</body>
</html>
