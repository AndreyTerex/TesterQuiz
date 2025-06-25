package Listener;

import Services.ResultService;
import Services.TestRunnerService;
import Services.TestService;
import Services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.JsonFileDao;
import dao.ResultDao;
import dao.TestDao;
import dao.UserDao;
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

        ValidatorUtil.init();

        ServletContext servletContext = sce.getServletContext();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String usersPath = servletContext.getRealPath("/WEB-INF/data/users/users.json");
        String testsPath = servletContext.getRealPath("/WEB-INF/data/tests/tests.json");
        String resultsPath = servletContext.getRealPath("/WEB-INF/data/results/results.json");

        UserDao userDao = new UserDao(new JsonFileDao<>(User.class, "users", new File(usersPath), objectMapper));
        TestDao testDao = new TestDao(new JsonFileDao<>(Test.class, "tests", new File(testsPath), objectMapper), servletContext.getRealPath("/WEB-INF/data/tests"));
        ResultDao resultDao = new ResultDao(new JsonFileDao<>(Result.class, "results", new File(resultsPath), objectMapper));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserService userService = new UserService(userDao, encoder);
        ResultService resultService = new ResultService(resultDao);
        TestService testService = new TestService(testDao);
        TestRunnerService testRunnerService = new TestRunnerService(testDao, resultService);

        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("testService", testService);
        servletContext.setAttribute("resultService", resultService);
        servletContext.setAttribute("objectMapper", objectMapper);
        servletContext.setAttribute("testRunnerService", testRunnerService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Close ValidatorUtil resources
        ValidatorUtil.close();
    }
}
