package Servlets.testsServlets;

import Servlets.BaseServlet;
import dto.ResultDTO;
import dto.TestDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet(urlPatterns = "/secure/results/*")
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
            results.sort((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
            session.setAttribute("results", results);
            redirectTo(resp, "/secure/testStatsAndHistory/testHistory.jsp");
        } else {
            String id = pathInfo.substring(1).trim();
            List<ResultDTO> results = (List<ResultDTO>) session.getAttribute("results");
            ResultDTO resultDTO = null;
            if(results != null){
                for(ResultDTO result : results){
                    if(result.getId().equals(UUID.fromString(id))){
                        resultDTO = result;
                        break;
                    }
                }
            }
          if(resultDTO == null){
              resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Result not found");
              return;
          }
            req.setAttribute("result", resultDTO);
            req.getRequestDispatcher("/secure/testStatsAndHistory/testResultDetails.jsp").forward(req, resp);
        }
    }
}
