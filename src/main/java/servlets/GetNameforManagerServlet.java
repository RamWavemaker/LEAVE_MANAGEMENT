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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/getnamesformanager")
public class GetNameforManagerServlet extends HttpServlet {
    private EmployeeService employeeService;
    private static final Logger logger = LoggerFactory.getLogger(GetNameforManagerServlet.class);

    @Override
    public void init() throws ServletException {
        employeeService = new EmployeeServiceImpl();
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
        List<Integer> employeesids = employeeService.getEmpUnderManager(userId);
        List<List<Employees>> employeeslist = new ArrayList<>();
        for (Integer employeesid : employeesids) {
            List<Employees> employeedetails = employeeService.getEmployeedetails(employeesid);
            employeeslist.add(employeedetails);
        }

        Gson gson = new Gson();
        String jsonEmployeeList = gson.toJson(employeeslist);

        // Write JSON to response
        res.getWriter().write(jsonEmployeeList);

    }
}
