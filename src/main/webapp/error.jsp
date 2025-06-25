<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Oops! Something went wrong</title>
    <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
    <div class="error-container">
        <h1>Oops! It seems we've hit a snag.</h1>
        <p>Don't worry, our team of highly trained hamsters has been dispatched to fix the problem.</p>
        <div class="error-message">
            <p>${sessionScope.errorMessage}</p>
        </div>
        <a href="${pageContext.request.contextPath}/">Go back to the Homepage</a>
        <img src="https://http.cat/500" alt="Sad cat looking at a server error">
    </div>
</body>
</html>
