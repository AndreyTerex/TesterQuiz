package Listener;

import dao.JsonFileDao;
import dao.ResultDao;
import dao.TestDao;
import dao.UserDao;
import Services.ResultService;
import Services.TestService;
import Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import entity.Result;
import entity.Test;
import entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).registerModule(new JavaTimeModule());


        String usersPath = servletContext.getRealPath("/WEB-INF/data/users/users.json");
        String testsPath = servletContext.getRealPath("/WEB-INF/data/tests/ALL_tests.json");
        String resultsPath = servletContext.getRealPath("/WEB-INF/data/results/results.json");

        File usersJsonFile = new File(usersPath);
        File testJsonFile = new File(testsPath);
        File resultsJsonFile = new File(resultsPath);

        JsonFileDao<User> baseUserDao = new JsonFileDao<>(User.class, "users", usersJsonFile,objectMapper);
        JsonFileDao<Test> baseTestDao = new JsonFileDao<>(Test.class, "tests", testJsonFile,objectMapper);
        JsonFileDao<Result> resultDao = new JsonFileDao<>(Result.class, "results", resultsJsonFile,objectMapper);

        UserDao userDao = new UserDao(baseUserDao);
        TestDao testDao = new TestDao(baseTestDao);
        ResultDao resultDao1 = new ResultDao(resultDao);

        UserService userService = new UserService(userDao, encoder);
        ResultService resultService = new ResultService(resultDao1);
        TestService testService = new TestService(testDao,resultService);

        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("testService", testService);
        servletContext.setAttribute("objectMapper", objectMapper);
        servletContext.setAttribute("resultService", resultService);



    }
}
