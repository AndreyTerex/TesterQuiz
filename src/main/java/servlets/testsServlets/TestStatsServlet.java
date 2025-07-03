package servlets.testsServlets;

import servlets.BaseServlet;
import constants.ServletPaths;
import dto.TestDTO;
import dto.TestStatsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = ServletPaths.ADMIN_STATS_PATH)
public class TestStatsServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<TestDTO> allTestsDTO = testService.findAllTestsDTO();
        List<TestStatsDTO> stats = resultService.getStats(allTestsDTO);
        Integer attempts = resultService.countAttempts();
        req.setAttribute("stats", stats);
        req.setAttribute("attempts", attempts);
        forwardTo(req, resp, ServletPaths.ADMIN_STATS_JSP);

    }
}
