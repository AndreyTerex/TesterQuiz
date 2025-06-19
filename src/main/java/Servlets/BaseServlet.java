package Servlets;

import Services.ResultService;
import Services.TestService;
import Services.UserService;
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

    /**
     * Инициализирует базовый сервлет и получает сервисы из контекста приложения
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        userService = (UserService) config.getServletContext().getAttribute("userService");
        testService = (TestService) config.getServletContext().getAttribute("testService");
        objectMapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
        resultService = (ResultService) config.getServletContext().getAttribute("resultService");
    }

    /**
     * Получает текущего пользователя из HTTP сессии
     */
    protected UserDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (UserDTO) session.getAttribute("user");
    }

    /**
     * Проверяет, аутентифицирован ли пользователь
     */
    protected boolean isUserAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }

    /**
     * Проверяет, имеет ли пользователь права администратора
     */
    protected boolean isAdmin(HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        return user != null && "ADMIN".equals(user.getRole());
    }

    /**
     * Устанавливает сообщение об ошибке в HTTP сессию
     */
    protected void setSessionError(HttpServletRequest request, String errorMessage) {
        request.getSession().setAttribute("error", errorMessage);
    }

    /**
     * Устанавливает сообщение об успехе в HTTP сессию
     */
    protected void setSessionSuccess(HttpServletRequest request, String successMessage) {
        request.getSession().setAttribute("success", successMessage);
    }

    /**
     * Перенаправляет пользователя с сообщением об ошибке
     */
    protected void redirectWithError(HttpServletRequest request, HttpServletResponse response,
                                     String errorMessage, String redirectPath) throws ServletException, IOException {
        setSessionError(request, errorMessage);
        response.sendRedirect(redirectPath);
    }


    /**
     * Отправляет JSON ответ клиенту
     */
    protected void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), data);
    }

    /**
     * Проверяет аутентификацию и перенаправляет на страницу входа при необходимости
     */
    protected boolean checkAuthenticationAndRedirect(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!isUserAuthenticated(request)) {
            redirectTo(response, ServletPaths.LOGIN_PATH);
            return false;
        }
        return true;
    }

    /**
     * Проверяет права администратора и отправляет ошибку 403 при отсутствии прав
     */
    protected boolean checkAdminRightsAndSendError(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    /**
     * Получает реальный путь к файлу в файловой системе сервера
     */
    protected String getRealPath(HttpServletRequest request, String relativePath) {
        return request.getServletContext().getRealPath(relativePath);
    }

    /**
     * Перенаправляет пользователя на указанный путь
     */
    protected void redirectTo(HttpServletResponse response, String path) throws IOException {
        response.sendRedirect(path);
    }

    /**
     * Пересылает запрос на указанный путь
     */
    protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String path)
            throws ServletException, IOException {
        request.getRequestDispatcher(path).forward(request, response);
    }
    
    /**
     * Получает параметр из запроса с проверкой на пустоту
     */
    protected String getParam(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return (value != null && !value.trim().isEmpty()) ? value.trim() : null;
    }

    /**
     * Очищает сессию от ненужных данных.
     * Удаляет временные данные, связанные с прохождением тестов и создании тестов,
     * но сохраняет важную информацию о пользователе, error massage и success message.
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
     * Извлекает список ответов из HTTP запроса
     */
    protected List<AnswerDTO> extractAnswersFromRequest(HttpServletRequest request) {
        List<dto.AnswerDTO> answerDTOList = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            String answerText = getParam(request, "answer" + i);
            String isCorrect = getParam(request, "correct" + i);

            if (answerText != null && !answerText.trim().isEmpty()) {
               AnswerDTO answer = dto.AnswerDTO.builder()
                        .id(java.util.UUID.randomUUID())
                        .answer_text(answerText)
                        .isCorrect("true".equals(isCorrect))
                        .build();

                answerDTOList.add(answer);
            }
        }
        
        return answerDTOList;
    }

    /**
     * Проверяет, есть ли хотя бы один правильный ответ в списке
     */
    protected boolean hasCorrectAnswer(List<AnswerDTO> answerDTOList) {
        return answerDTOList.stream().anyMatch(AnswerDTO::isCorrect);
    }

}