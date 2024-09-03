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
import services.DatabaseServices;
import services.DatabaseServicesImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@WebServlet("/leavetrackerempundermanager")
public class LeaveTrackerEmpUnderMangerServlet extends HttpServlet {
    private DatabaseServices dbservices;
    private static final Logger logger = LoggerFactory.getLogger(LeaveTrackerEmpUnderMangerServlet.class);

    @Override
    public void init() throws ServletException {
        dbservices = new DatabaseServicesImpl();
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

        int userId;
        try {
            userId = (int) session.getAttribute("LOGIN_USER_ID");
        } catch (ClassCastException e) {
            logger.debug("Invalid session data", e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"error\":\"Invalid session data\"}");
            return;
        }

        List<Integer> empIdsUnderManager = dbservices.getEmpUnderManager(userId);

        Map<Integer, Map<Leaves.LeaveType, Integer>> allEmployeeLeaveData = new HashMap<>();

        try{

            List<String> holidayDates = dbservices.getHolidaysDates();
            // Convert holiday dates from String to LocalDate
            List<LocalDate> holidays = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            for (String dateStr : holidayDates) {
                try {
                    java.util.Date date = dateFormat.parse(dateStr);
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    holidays.add(localDate);
                } catch (ParseException e) {
                    logger.warn("Invalid date format in holidays list: " + dateStr, e);
                }
            }

            for (int empId : empIdsUnderManager) {
                Map<Leaves.LeaveType, Integer> leaveDaysMap = new EnumMap<>(Leaves.LeaveType.class);

                for (Leaves.LeaveType leaveType : Leaves.LeaveType.values()) {
                    try {
                        List<Integer> leaveIds = dbservices.getNoOfLeavesByTypeAndUser(leaveType, empId);
                        logger.debug("LeaveIds for employee {} and leave type {}: {}", empId, leaveType, leaveIds);
                        int totalDays = dbservices.getNoOfDaysBetweenDates(leaveIds,holidays);
                        leaveDaysMap.put(leaveType, totalDays);
                        logger.debug("No-of Days for employee {} and leave type {}: {}", empId, leaveType, totalDays);
                    } catch (SQLException e) {
                        logger.error("Error fetching or processing leave data for employee {} and leave type {}", empId, leaveType, e);
                        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        res.getWriter().write("{\"error\":\"An error occurred while processing leave data for employee " + empId + "\"}");
                        return;
                    }
                }

                allEmployeeLeaveData.put(empId, leaveDaysMap);
            }
        } catch (SQLException e) {
            logger.error("Error fetching holiday dates", e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"error\":\"An error occurred while processing your request\"}");
            return;
        }
        Gson gson = new Gson();
        String json = gson.toJson(allEmployeeLeaveData);
        res.getWriter().write(json);
    }
}
