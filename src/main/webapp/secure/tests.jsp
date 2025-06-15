<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="user" value="${sessionScope.user}" />
<c:set var="role" value="${not empty user and not empty user.role ? user.role : ''}" />
<c:set var="isAdmin" value="${role == 'ADMIN'}" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tests</title>
    <link rel="stylesheet" href="../styles.css">
</head>
<body>

<div class="main-container">
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert--error">
                ${sessionScope.error}
        </div>
        <c:remove var="error" scope="session"/>
    </c:if>
    <div class="page-header-centered">
        <h2 class="page-title">Available Tests</h2>
    </div>

    <div class="return-btn-container">
        <button class="nav-btn" onclick="window.location.href='/secure/menu'">Return to Menu</button>
    </div>

    <div class="topic-filter">
        <label for="topicSelect">Filter by Topic:</label>
        <select id="topicSelect" onchange="filterTestsByTopic()">
            <option value="">All Topics</option>
        </select>
    </div>

    <table id="testsTable">
        <thead>
        <tr>
            <th>Test Name</th>
            <th>Topic</th>
            <th>Questions</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <script>
        var isAdmin = ${isAdmin};
        var allTests = [];

        function loadTests() {
            fetch('/tests')
                .then(function (response) {
                    return response.json();
                })
                .then(function (tests) {
                    allTests = tests; //
                    displayTests(allTests);
                    populateTopicFilter(allTests);
                })
                .catch(function (error) {
                    console.error('Error:', error);
                    alert('Error loading tests');
                });
        }

        function displayTests(tests) {
            var tbody = document.querySelector('#testsTable tbody');
            tbody.innerHTML = '';

            for (var i = 0; i < tests.length; i++) {
                var test = tests[i];
                var row = document.createElement('tr');

                var questionCount = test.questions ? test.questions.length : 0;

                var actionsHTML = '<div class="test-actions">' +
                    '<form class="action-form" action="/WelcomeToTheTestServlet" method="get">' +
                    '<input type="hidden" name="id" value="' + test.id + '">' +
                    '<button type="submit" class="btn btn-start">Start</button>' +
                    '</form>';
                
                if (isAdmin) {
                    actionsHTML += '<form class="action-form" action="/tests/' + test.id + '" method="post">' +
                        '<input type="hidden" name="_method" value="PUT">' +
                        '<button type="submit" class="btn btn-edit">Edit</button>' +
                        '</form>';
                    
                    actionsHTML += '<form class="action-form" action="/tests/' + test.id + '" method="post">' +
                        '<input type="hidden" name="_method" value="DELETE">' +
                        '<button type="submit" class="btn btn-delete" onclick="return confirm(\'Are you sure you want to delete this test?\')">Delete</button>' +
                        '</form>';
                }
                
                actionsHTML += '</div>';

                row.innerHTML =
                    '<td>' + test.title + '</td>' +
                    '<td>' + test.topic + '</td>' +
                    '<td>' + questionCount + '</td>' +
                    '<td>' + actionsHTML + '</td>';

                tbody.appendChild(row);
            }
        }

        function populateTopicFilter(tests) {
            var topicSelect = document.getElementById('topicSelect');
            var topics = new Set(tests.map(test => test.topic));
            topics.forEach(function (topic) {
                var option = document.createElement('option');
                option.value = topic;
                option.textContent = topic;
                topicSelect.appendChild(option);
            });
        }

        function filterTestsByTopic() {
            var selectedTopic = document.getElementById('topicSelect').value;
            var filteredTests = allTests.filter(function (test) {
                return selectedTopic === "" || test.topic === selectedTopic;
            });
            displayTests(filteredTests);
        }

        loadTests();
    </script>

    <footer class="footer">
        © 2025 TesterQuiz
    </footer>
</div>
</body>
</html>