package servlets.testsServlets;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.ResultDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

@WebServlet(urlPatterns = ServletPaths.RESULTS_PATTERN_PATH)
public class TestPassingHistoryServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(!checkAuthenticationAndRedirect(req, resp)){
            return;
        }
        HttpSession session = req.getSession();
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo) || pathInfo.isEmpty()) {
            List<ResultDTO> results = resultService.getAllResultsByUserId(getCurrentUser(req).getId());
            session.setAttribute("results", results);
            redirectTo(resp, ServletPaths.TEST_HISTORY_JSP);
        } else {
            String id = pathInfo.substring(1).trim();
            ResultDTO resultDTO = resultService.findByIdWithDetails(UUID.fromString(id));
            req.setAttribute("result", resultDTO);
            req.getRequestDispatcher(ServletPaths.TEST_RESULT_DETAILS_JSP).forward(req, resp);
        }
    }
}
