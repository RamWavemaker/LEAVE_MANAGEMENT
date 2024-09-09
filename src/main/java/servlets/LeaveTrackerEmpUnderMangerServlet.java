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
import services.*;
import services.impl.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@WebServlet("/leavetrackerempundermanager")
public class LeaveTrackerEmpUnderMangerServlet extends HttpServlet {
    private HolidayService holidayService;
    private EmployeeService employeeService;
    private LeaveService leaveService;
    private static final Logger logger = LoggerFactory.getLogger(LeaveTrackerEmpUnderMangerServlet.class);

    @Override
    public void init() throws ServletException {
        employeeService = new EmployeeServiceImpl();
        holidayService = new HolidayServiceImpl();
        leaveService = new LeaveServiceImpl();
        logger.debug("Servlet initialized: LeavesOfUserByType");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER_ID") == null) {
            logger.debug("User not logged in");
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("{\"error\":\"User not logged in\"}");
            return;
        }

        int userId = (int) session.getAttribute("LOGIN_USER_ID");
        List<Integer> empIdsUnderManager = employeeService.getEmpUnderManager(userId);

        Map<Integer, Map<Leaves.LeaveType, Integer>> allEmployeeLeaveData = new HashMap<>();

        List<String> holidayDates = holidayService.getHolidaysDates();
        // Converting holiday dates from String to LocalDate
        List<LocalDate> holidays = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (String dateStr : holidayDates) {
            try {
                Date date = dateFormat.parse(dateStr);
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                holidays.add(localDate);
            } catch (ParseException e) {
                logger.warn("Invalid date format in holidays list: " + dateStr, e);
            }
        }

        for (int empId : empIdsUnderManager) {
            Map<Leaves.LeaveType, Integer> leaveDaysMap = new EnumMap<>(Leaves.LeaveType.class);

            for (Leaves.LeaveType leaveType : Leaves.LeaveType.values()) {
                List<Integer> leaveIds = leaveService.getIdOfLeavesByTypeAndUser(leaveType,empId);
                logger.debug("LeaveIds for employee {} and leave type {}: {}", empId, leaveType, leaveIds);
                int totalDays = leaveService.getNoOfDaysBetweenDates(leaveIds,holidays);
                leaveDaysMap.put(leaveType, totalDays);
                logger.debug("No-of Days for employee {} and leave type {}: {}", empId, leaveType, totalDays);
            }

            allEmployeeLeaveData.put(empId, leaveDaysMap);
        }
        Gson gson = new Gson();
        String json = gson.toJson(allEmployeeLeaveData);
        res.getWriter().write(json);
    }
}
