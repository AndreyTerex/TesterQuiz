# 🚀 TesterQuiz: A Web-Based Quiz Platform

Welcome to TesterQuiz! This is a simple but powerful web application I built to let anyone create, share, and take tests online. It's a great tool for learning, teaching, or just challenging your friends.

## ✨ What's this all about?

TesterQuiz is a full-featured quiz application with two main roles: regular users and administrators.

*   **For Everyone (Users):**
    *   Sign up and create your own account.
    *   Browse a list of available tests on different topics.
    *   Take any test and see your score and results immediately.

*   **For the Boss (Admins):**
    *   All the powers of a regular user.
    *   **Create new tests:** Easily add new quizzes with titles and topics.
    *   **Manage questions:** Add, edit, and remove questions from any test.
    *   **View statistics:** Get insights into how users are performing on different tests.

## 🛠️ Under the Hood: The Tech Stack

This project is a classic Java web application built with some solid, industry-standard technologies:

*   **Backend:** Java, Servlets, JSP, and JSTL for the core logic and dynamic pages.
*   **Frontend:** Simple and clean HTML & CSS.
*   **Builds:** Managed by Apache Maven.
*   **Server:** Runs on an Apache Tomcat server.

## 🏁 Getting Started

Ready to run the project locally? Here’s how you can get it up and running.

### Prerequisites

*   Java Development Kit (JDK) 8 or higher.
*   Apache Maven.
*   Apache Tomcat.
*   Your favorite IDE (like IntelliJ IDEA or Eclipse).

### Installation & Setup

1.  **Clone the repo:**
    ```sh
    git clone https://github.com/your-username/web-test-project.git
    cd web-test-project
    ```
    *(Don't forget to replace `your-username` with the actual one!)*

2.  **Build the project:**
    Use Maven to build the project. This will download all the necessary dependencies and create a `.war` file in the `target/` directory.
    ```sh
    mvn clean install
    ```

3.  **Deploy to Tomcat:**
    Deploy the generated `.war` file to your Tomcat server. You can usually do this by copying the file into Tomcat's `webapps` directory or by configuring it directly in your IDE.

4.  **You're all set!**
    Open your browser and navigate to the application's URL (usually something like `http://localhost:8080/web-test-project-1.0-SNAPSHOT/`).

## 🤝 Want to contribute?

I'm always open to improvements and new ideas! If you find a bug or have a suggestion.

Thanks for checking out my project!
