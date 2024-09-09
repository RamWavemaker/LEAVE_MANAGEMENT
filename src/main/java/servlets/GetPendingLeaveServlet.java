package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Leaves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LeaveService;
import services.impl.LeaveServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
@WebServlet("/pendingleavesServlet")
public class GetPendingLeaveServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(GetPendingLeaveServlet.class);
    private LeaveService leaveService;

    @Override
    public void init() throws ServletException {
        leaveService = new LeaveServiceImpl();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"User not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("LOGIN_USER_ID");
        Leaves.Status status = Leaves.Status.PENDING;
        List<Leaves> leavesList = leaveService.getLeavesByStatus(status,userId);

        logger.debug(leavesList.toString());

        Gson gson = new Gson();
        String jsonLeavesList = gson.toJson(leavesList);

        // Write JSON to response
        res.getWriter().write(jsonLeavesList);
    }
}
