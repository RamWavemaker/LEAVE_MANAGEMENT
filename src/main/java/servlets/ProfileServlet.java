package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Employees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.EmployeeService;
import services.impl.EmployeeServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/profileservlet")
public class ProfileServlet extends HttpServlet {
    private EmployeeService employeeService;
    private static Logger logger = LoggerFactory.getLogger(LeaveServlet.class);
    private static final Gson gson = new Gson();
    @Override
    public void init() throws ServletException {
        employeeService = new EmployeeServiceImpl();
        logger.debug("Hey i am comming into profile");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"User not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("LOGIN_USER_ID");


        List<Employees> employeesList = employeeService.getEmployeedetails(userId);
        String jsonEmployeeList = gson.toJson(employeesList);
        res.setContentType("application/json");
        res.getWriter().write(jsonEmployeeList);
    }
}
