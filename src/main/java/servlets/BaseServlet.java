package servlets;

import services.ResultService;
import services.TestRunnerService;
import services.TestService;
import services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import constants.ServletPaths;
import dto.AnswerDTO;
import dto.UserDTO;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseServlet extends HttpServlet {
    protected UserService userService;
    protected TestService testService;
    protected ObjectMapper objectMapper;
    protected ResultService resultService;
    protected TestRunnerService testRunnerService;

    /**
     * Initializes the base servlet and gets services from the application context
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = (UserService) config.getServletContext().getAttribute("userService");
        testService = (TestService) config.getServletContext().getAttribute("testService");
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
        resultService = (ResultService) config.getServletContext().getAttribute("resultService");
        testRunnerService = (TestRunnerService) config.getServletContext().getAttribute("testRunnerService");
    }

    /**
     * Gets the current user from the HTTP session
     */
    protected UserDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (UserDTO) session.getAttribute("user");
    }

    /**
     * Checks if the user is authenticated
     */
    protected boolean isUserAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * Checks if the user has administrator rights
     */
    protected boolean isAdmin(HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * Sets an error message in the HTTP session
     */
    protected void setSessionError(HttpServletRequest request, String errorMessage) {
        request.getSession().setAttribute("error", errorMessage);
    }

    /**
     * Sets a success message in the HTTP session
     */
    protected void setSessionSuccess(HttpServletRequest request, String successMessage) {
        request.getSession().setAttribute("success", successMessage);
    }

    /**
     * Redirects the user with an error message
     */
    protected void redirectWithError(HttpServletRequest request, HttpServletResponse response,
                                     String errorMessage, String redirectPath) throws  IOException {
        setSessionError(request, errorMessage);
        response.sendRedirect(redirectPath);
    }


    /**
     * Sends a JSON response to the client
     */
    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), data);
    }

    /**
     * Checks authentication and redirects to the login page if necessary
     */
    protected boolean checkAuthenticationAndRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isUserAuthenticated(request)) {
            redirectTo(response, ServletPaths.LOGIN_PATH);
            return false;
        }
        return true;
    }

    /**
     * Checks for administrator rights and sends a 403 error if rights are missing
     */
    protected boolean checkAdminRightsAndSendError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    /**
     * Redirects the user to the specified path
     */
    protected void redirectTo(HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(path);
    }

    /**
     * Forwards the request to the specified path
     */
    protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }

    /**
     * Sets the current user in the HTTP session
     */
    protected void setCurrentUser(HttpServletRequest request, UserDTO user) {
        request.getSession().setAttribute("user", user);
    }

    /**
     * Sets session attributes for passing a test
     */
    protected void setTestSessionAttributes(HttpServletRequest request, dto.TestSessionDTO sessionAttributes) {
        HttpSession session = request.getSession();
        session.setAttribute("timeForTest", sessionAttributes.getRoundedEndTime());
        session.setAttribute("currentQuestion", sessionAttributes.getCurrentQuestion());
        session.setAttribute("result", sessionAttributes.getResult());
    }

    /**
     * Updates session attributes during a test
     */
    protected void updateTestProgress(HttpServletRequest request, dto.TestProgressDTO testProgress) {
        HttpSession session = request.getSession();
        session.setAttribute("result", testProgress.getResult());
        if (!testProgress.isTestFinished()) {
            session.setAttribute("currentQuestion", testProgress.getQuestion());
        }
    }

    /**
     * Sets the current test in the session
     */
    protected void setCurrentTest(HttpServletRequest request, dto.TestDTO test) {
        request.getSession().setAttribute("currentTest", test);
    }
    
    /**
     * Gets a parameter from the request with a check for emptiness
     */
    protected String getParam(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }

    /**
     * Clears the session of unnecessary data.
     * Removes temporary data related to passing and creating tests,
     * but keeps important user information, error messages, and success messages.
     */
    protected void clearSessionData(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("currentTest");
            session.removeAttribute("currentQuestion");
            session.removeAttribute("result");
            session.removeAttribute("timeForTest");
            session.removeAttribute("testTimeOut");
        }
    }

    /**
     * Extracts a list of answers from the HTTP request
     */
    protected List<AnswerDTO> extractAnswersFromRequest(HttpServletRequest request) {
        List<dto.AnswerDTO> answerDTOList = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            String answerText = getParam(request, "answer" + i);
            String isCorrect = getParam(request, "correct" + i);

            if (answerText != null && !answerText.trim().isEmpty()) {
               AnswerDTO answer = dto.AnswerDTO.builder()
                        .id(java.util.UUID.randomUUID())
                        .answerText(answerText)
                        .isCorrect("true".equals(isCorrect))
                        .build();

                answerDTOList.add(answer);
            }
        }
        
        return answerDTOList;
    }

    /**
     * Checks if there is at least one correct answer in the list
     */
    protected boolean hasCorrectAnswer(List<AnswerDTO> answerDTOList) {
        return answerDTOList.stream().anyMatch(AnswerDTO::isCorrect);
    }

}