package listener;

import dao.impl.UserDaoImpl;
import services.interfaces.ResultServiceInterface;
import services.ResultService;
import services.interfaces.TestRunnerServiceInterface;
import services.TestRunnerService;
import services.interfaces.TestServiceInterface;
import services.TestService;
import services.interfaces.UserServiceInterface;
import mappers.*;
import org.mapstruct.factory.Mappers;
import services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.impl.JsonFileDaoImpl;
import dao.impl.ResultDaoImpl;
import dao.impl.TestDaoImpl;
import entity.Result;
import entity.Test;
import entity.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import validators.ValidatorTestRunnerService;
import validators.ValidatorTestService;
import validators.ValidatorUserService;
import validators.ValidatorUtil;

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

        UserDaoImpl userDaoImpl = new UserDaoImpl(new JsonFileDaoImpl<>(User.class, "users", new File(usersPath), objectMapper));
        TestDaoImpl testDaoImpl = new TestDaoImpl(new JsonFileDaoImpl<>(Test.class, "tests", new File(testsPath), objectMapper), servletContext.getRealPath("/WEB-INF/data/tests"));
        ResultDaoImpl resultDaoImpl = new ResultDaoImpl(new JsonFileDaoImpl<>(Result.class, "results", new File(resultsPath), objectMapper));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ValidatorUserService validatorUserService = new ValidatorUserService();
        ValidatorTestService validatorTestService = new ValidatorTestService();
        ValidatorTestRunnerService validatorTestRunnerService = new ValidatorTestRunnerService();

        AnswerMapper answerMapper = Mappers.getMapper(AnswerMapper.class);
        QuestionMapper questionMapper = Mappers.getMapper(QuestionMapper.class);
        ResultMapper resultMapper = Mappers.getMapper(ResultMapper.class);
        TestMapper testMapper = Mappers.getMapper(TestMapper.class);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);

        UserServiceInterface userService = new UserService(userDaoImpl, encoder, validatorUserService, userMapper);
        ResultServiceInterface resultService = new ResultService(resultDaoImpl, resultMapper);
        TestServiceInterface testService = new TestService(testDaoImpl, validatorTestService, testMapper, questionMapper, answerMapper);
        TestRunnerServiceInterface testRunnerService = new TestRunnerService(testDaoImpl, resultService, validatorTestRunnerService, testMapper, questionMapper, resultMapper);

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
