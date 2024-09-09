package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Leaves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LeaveService;
import services.impl.LeaveServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/getleavesbyid")
public class LeavesByIdServlet extends HttpServlet {
    private LeaveService leaveService;
    private static final Logger logger = LoggerFactory.getLogger(LeaveServlet.class);

    @Override
    public void init() throws ServletException {
        leaveService = new LeaveServiceImpl();
        logger.debug("Hey i am comming into leavesbyid");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String employeeId = req.getParameter("id");

        if (employeeId == null || employeeId.isEmpty()) {
            logger.error("Employee ID is missing in the request");
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Employee ID is missing.");
            return;
        }

        try {
            int empId = Integer.parseInt(employeeId);
            List<Leaves> leavesList = leaveService.getLeaves(empId);
            Gson gson = new Gson();
            String jsonLeavesList = gson.toJson(leavesList);
            res.getWriter().write(jsonLeavesList);
            logger.info("Successfully fetched leave data for employee ID: {}", empId);
        } catch (NumberFormatException e) {
            logger.error("Invalid employee ID format: {}", employeeId, e);
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID format.");
        } catch (Exception e) {
            logger.error("Error fetching leave data for employee ID: {}", employeeId, e);
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while fetching leave data.");
        }
    }
}
