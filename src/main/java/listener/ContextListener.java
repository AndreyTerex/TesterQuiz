package listener;

import dao.impl.ResultDAOImpl;
import dao.impl.TestDAOImpl;
import dao.impl.UserDAOImpl;
import services.ResultServiceImpl;
import services.TestRunnerServiceImpl;
import services.TestServiceImpl;
import services.UserServiceImpl;
import services.interfaces.ResultService;
import services.interfaces.TestRunnerService;
import services.interfaces.TestService;
import services.interfaces.UserService;
import mappers.*;
import org.mapstruct.factory.Mappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import util.HibernateUtil;
import validators.ValidatorTestRunnerService;
import validators.ValidatorTestService;
import validators.ValidatorUserService;
import util.ValidatorUtil;


@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HibernateUtil.init();
        ValidatorUtil.init();

        ServletContext servletContext = sce.getServletContext();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        ValidatorUserService validatorUserService = new ValidatorUserService();
        ValidatorTestService validatorTestService = new ValidatorTestService();
        ValidatorTestRunnerService validatorTestRunnerService = new ValidatorTestRunnerService();

        AnswerMapper answerMapper = Mappers.getMapper(AnswerMapper.class);
        QuestionMapper questionMapper = Mappers.getMapper(QuestionMapper.class);
        ResultMapper resultMapper = Mappers.getMapper(ResultMapper.class);
        TestMapper testMapper = Mappers.getMapper(TestMapper.class);
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);


        TestDAOImpl testDAO = new TestDAOImpl();
        UserDAOImpl userDAO = new UserDAOImpl();
        ResultDAOImpl resultDAO = new ResultDAOImpl();


        UserService userService = new UserServiceImpl(userDAO, encoder, validatorUserService, userMapper);
        TestService testService = new TestServiceImpl(testDAO,userService, validatorTestService, testMapper, answerMapper);
        ResultService resultService = new ResultServiceImpl(resultDAO, resultMapper,testService,userService);
        TestRunnerService testRunnerService = new TestRunnerServiceImpl(testDAO, resultService, validatorTestRunnerService, testMapper, questionMapper, resultMapper, userMapper);

        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("testService", testService);
        servletContext.setAttribute("resultService", resultService);
        servletContext.setAttribute("objectMapper", objectMapper);
        servletContext.setAttribute("testRunnerService", testRunnerService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ValidatorUtil.close();
        HibernateUtil.shutdown();
    }
}
