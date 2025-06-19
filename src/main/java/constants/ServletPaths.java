package constants;

/**
 * Интерфейс содержащий все пути, используемые в сервлетах
 */
public interface ServletPaths {
    
    // Servlet URL patterns (для аннотаций @WebServlet)
    String LOGIN_PATH = "/login";
    String REGISTER_PATH = "/register";
    String LOGOUT_PATH = "/logout";
    String MENU_PATH = "/secure/menu";
    String TESTS_PATTERN_PATH = "/secure/tests/*";
    String START_TEST_PATH = "/secure/startTest";
    String NEXT_QUESTION_PATH = "/secure/nextQuestion";
    String WELCOME_TO_TEST_PATH = "/secure/WelcomeToTheTestServlet";
    
    // JSP страницы
    String LOGIN_JSP = "/login.jsp";
    String REGISTER_JSP = "register.jsp";
    String INDEX_JSP = "/index.jsp";
    String MENU_JSP = "/secure/menu.jsp";
    String TESTS_JSP = "/secure/tests.jsp";
    
    // Страницы прохождения тестов
    String PASSING_TEST_JSP = "/secure/passingTheTest/passingTest.jsp";
    String TEST_WELCOME_PAGE_JSP = "/secure/passingTheTest/testWelcomePage.jsp";
    String TEST_END_RESULT_JSP = "/secure/passingTheTest/testEndAndResult.jsp";
    
    // Админские страницы создания тестов
    String CREATE_TEST_START_JSP = "/secure/admin/createTest/createTestStart.jsp";
    String ADD_QUESTION_JSP = "/secure/admin/createTest/addQuestion.jsp";
    String CONTINUE_OR_END_MENU_JSP = "/secure/admin/createTest/continueOrEndMenu.jsp";
    
    // Системные пути
    String WEB_INF_DATA_TESTS = "/WEB-INF/data/tests";
    String WEB_INF_DATA_USERS = "/WEB-INF/data/users";
    String WEB_INF_DATA_TEST_RESULTS = "/WEB-INF/data/results";
    
    // Filter URL patterns
    String SECURE_FILTER_PATTERN = "/secure/*";
    String ADMIN_FILTER_PATTERN = "/secure/admin/*";
    String HTTP_METHOD_FILTER_PATTERN = "/*";
}