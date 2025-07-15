# Web Test Project

This is a web application for creating and taking tests. The project is developed in Java using Maven, Tomcat, PostgreSQL, and Hibernate. The entire development and runtime environment is packaged in Docker containers.

## Technologies

- **Backend:** Java, Servlets
- **Server:** Apache Tomcat
- **Database:** PostgreSQL
- **ORM:** Hibernate
- **Build:** Maven
- **Containerization:** Docker, Docker Compose

## How to Run

To run the project, you need to have Docker and Docker Compose installed.

1.  **Build and First Run:**

    Open a terminal in the project's root folder and run the command:
    ```bash
    docker-compose up --build
    ```
    This command will build the application image, download the necessary images, and start all containers. The `init.sql` script will automatically create the required tables in the database.

2.  **Subsequent Runs:**

    For a normal start, use the command:
    ```bash
    docker-compose up
    ```

3.  **Stopping the Project:**

    To stop all containers, run:
    ```bash
    docker-compose down
    ```

4.  **Full Rebuild (with DB data deletion):**

    If you need to completely recreate the database and images, run the following commands:
    ```bash
    # 1. Stop and remove containers
    docker-compose down

    # 2. Remove the PostgreSQL data volume
    docker volume rm web-test-project_pgdata

    # 3. Build and run the project again
    docker-compose up --build
    ```

## Accessing Services

After a successful launch, the following endpoints will be available:

- **Web Application:** [http://localhost:8081](http://localhost:8081)
- **Adminer (web interface for DB management):** [http://localhost:8080](http://localhost:8080)
- **Direct connection to PostgreSQL:** `localhost:5433`

**Credentials for Adminer:**
- **System:** PostgreSQL
- **Server:** `postgres`
- **Username:** `myuser`
- **Password:** `mypassword`
- **Database:** `mydb`
