# Web Test Project

## Overview

This project is a full-featured Java web application for creating, taking, and managing quizzes. It supports user registration, authentication, role-based access (admin/user), test creation, test participation, and result tracking. Data is stored in PostgreSQL, and deployment is streamlined with Docker.

The app is built with modern Java technologies and demonstrates practical use of ORM, servlets, validation, error handling, and containerization.

## Key Features

- **User Management:** Registration, login, and role-based access (admin and user)
- **Test Creation:** Admins can create tests with questions and answer options
- **Taking Tests:** Users can take timed quizzes with instant scoring
- **Results:** Users can view their scores and selected answers
- **Validation & Error Handling:** Input validation and custom exceptions for business logic and access control
- **Database:** PostgreSQL with Hibernate ORM
- **Deployment:** Docker and Docker Compose (app, database, and Adminer for DB management)

## Technologies Used

- **Java 21**, Jakarta Servlets, JSP, JSTL
- **PostgreSQL**
- **Hibernate 7**
- **Maven**
- **Logback, SLF4J**
- **Lombok, MapStruct, Jackson, Spring Security Crypto, Jakarta Validation**
- **JUnit 5, Mockito, JaCoCo**
- **Docker, Docker Compose**
- **Apache Tomcat**

## Architecture

The project follows a layered architecture:
- **Entities:** User, Test, Question, Answer, Result (with Lombok)
- **Services:** Business logic (UserService, TestService, ResultService, etc.), CRUD, validation, and transactions via Hibernate
- **Servlets:** Handle HTTP requests, with shared logic (like authentication) in BaseServlet
- **Validators:** Separate classes for data validation
- **Utilities:** HibernateUtil, ValidatorUtil, and more
- **Frontend:** JSP pages with JSTL
- **Database:** Schema initialized via init.sql, with tables for users, tests, questions, answers, results, and relationships
- **Exceptions:** Custom hierarchy for clean error handling

The app is packaged as a WAR and runs on Tomcat, with PostgreSQL as the backend.

## How to Run

You’ll need Docker and Docker Compose installed.

1. **First Run:**
   Open a terminal in the project root and run:
   ```bash
   docker-compose up --build
   ```
   This builds the images, downloads dependencies, and starts the containers. The `init.sql` script sets up the database tables.

2. **Subsequent Runs:**
   ```bash
   docker-compose up
   ```

3. **Stopping the App:**
   ```bash
   docker-compose down
   ```

4. **Full Rebuild (with DB reset):**
   ```bash
   docker-compose down
   docker volume rm web-test-project_pgdata
   docker-compose up --build
   ```

## Accessing Services

- **Web App:** [http://localhost:8081](http://localhost:8081)
- **Adminer (DB UI):** [http://localhost:8080](http://localhost:8080)
- **PostgreSQL direct:** `localhost:5433`

**Adminer credentials:**
- **System:** PostgreSQL
- **Server:** `postgres`
- **Username:** `myuser`
- **Password:** `mypassword`
- **Database:** `mydb`

## Usage

1. Go to http://localhost:8081
2. Register a new user or log in (admin can be added via DB)
3. Admins can create new tests
4. Users can select and take tests
5. Results are available after completion

## What I Learned

- Building web apps with servlets (without Spring)
- Integrating Hibernate for ORM and database work
- Implementing role-based authentication and session management
- Containerizing and setting up the environment with Docker
- Best practices in validation, error handling, and testing
- Using Maven for builds and dependency management

This project was a great hands-on experience in Java web development and helped me deepen my understanding of backend and full-stack engineering.