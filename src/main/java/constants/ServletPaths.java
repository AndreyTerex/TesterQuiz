package constants;

public interface ServletPaths {
    // Servlets
    String LOGIN_PATH = "/login";
    String REGISTER_PATH = "/register";
    String LOGOUT_PATH = "/logout";
    String MENU_PATH = "/secure/menu";
    String TESTS_PATTERN_PATH = "/secure/tests/*";
    String RESULTS_PATTERN_PATH = "/secure/results/*";
    String EDIT_TEST_PATH = "/secure/admin/editTest";
    String NEXT_QUESTION_PATH = "/secure/nextQuestion";
    String START_TEST_PATH = "/secure/startTest";
    String WELCOME_TO_TEST_PATH = "/secure/welcomeToTest";
    String ADMIN_STATS_PATH = "/secure/admin/stats";
    String ERROR_PATH = "/error";
    String ADD_QUESTION_PATH = "/secure/tests/add-question";
    String PREPARE_EDIT_TEST_PATH = "/secure/tests/prepare-edit";

    // JSP
    String LOGIN_JSP = "/login.jsp";
    String REGISTER_JSP = "/register.jsp";
    String INDEX_JSP = "/index.jsp";
    String MENU_JSP = "/secure/menu.jsp";
    String TESTS_JSP = "/secure/tests.jsp";
    String ADD_QUESTION_JSP = "/secure/admin/createTest/addQuestion.jsp";
    String CONTINUE_OR_END_MENU_JSP = "/secure/admin/createTest/continueOrEndMenu.jsp";
    String START_EDIT_TEST_JSP = "/secure/admin/editTest/startEditTest.jsp";
    String EDIT_QUESTIONS_MENU_JSP = "/secure/admin/editTest/editQuestionsMenu.jsp";
    String EDIT_QUESTION_JSP = "/secure/admin/editTest/editQuestion.jsp";
    String FINISH_TEST_CREATE_JSP = "/secure/admin/createTest/finishTestCreate.jsp";
    String PASSING_TEST_JSP = "/secure/passingTheTest/passingTest.jsp";
    String TEST_WELCOME_PAGE_JSP = "/secure/passingTheTest/testWelcomePage.jsp";
    String TEST_END_RESULT_JSP = "/secure/passingTheTest/testEndAndResult.jsp";
    String ADMIN_STATS_JSP = "/secure/admin/adminStatisticPage.jsp";
    String TEST_HISTORY_JSP = "/secure/testStatsAndHistory/testHistory.jsp";
    String TEST_RESULT_DETAILS_JSP = "/secure/testStatsAndHistory/testResultDetails.jsp";
    String ERROR_JSP = "/WEB-INF/error.jsp";
}
