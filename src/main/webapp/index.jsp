<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Welcome to TesterQuiz</title>
    <link rel="stylesheet" href="/static/styles.css">
</head>
<body>
<header class="header">
    <h1 class="header__logo">TesterQuiz</h1>
</header>
<main class="main main--landing">
    <section class="intro">
        <h2>Create and share your own quizzes</h2>
        <p>Join our community: add tests, challenge friends, and track your progress.</p>
        <div class="intro__actions">
            <form action="/login" method="get" class="inline-form">
                <button type="submit" class="btn btn--primary">Log In</button>
            </form>
            <form action="/register" method="get" class="inline-form">
                <button type="submit" class="btn btn--secondary">Register</button>
            </form>
        </div>
    </section>
    <section class="features">
        <div class="feature">
            <h3>Easy to use</h3>
            <p>Quickly create tests with our intuitive interface.</p>
        </div>
        <div class="feature">
            <h3>Shareable</h3>
            <p>Send links to your friends or embed quizzes on any site.</p>
        </div>
        <div class="feature">
            <h3>Track progress</h3>
            <p>See results and statistics for each user.</p>
        </div>
        <div class="feature">
            <h3>Secure & Reliable</h3>
            <p>Your data is protected with modern security measures.</p>
        </div>
    </section>
</main>
<footer class="footer">
    © 2025 TesterQuiz
</footer>
</body>
</html>