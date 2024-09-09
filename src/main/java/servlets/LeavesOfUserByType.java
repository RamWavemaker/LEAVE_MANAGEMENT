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
import services.HolidayService;
import services.LeaveService;
import services.impl.HolidayServiceImpl;
import services.impl.LeaveServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@WebServlet("/leaveofuserbytype")
public class LeavesOfUserByType extends HttpServlet {
    private LeaveService leaveService;
    private HolidayService holidayService;
    private static final Logger logger = LoggerFactory.getLogger(LeavesOfUserByType.class);

    @Override
    public void init() throws ServletException {
        leaveService = new LeaveServiceImpl();
        holidayService = new HolidayServiceImpl();
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

        Map<Leaves.LeaveType, Integer> leaveDaysMap = new EnumMap<>(Leaves.LeaveType.class);

        List<String> holidayDates = holidayService.getHolidaysDates();

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
        for (Leaves.LeaveType leaveType : Leaves.LeaveType.values()) {
            List<Integer> leaveIds = leaveService.getIdOfLeavesByTypeAndUser(leaveType,userId);
            logger.debug("LeaveIds for {}: {}", leaveType, leaveIds);
            int totalDays = leaveService.getNoOfDaysBetweenDates(leaveIds,holidays);
            //write here
            leaveDaysMap.put(leaveType, totalDays);
            logger.debug("No-of Days for {}: {}",leaveType,totalDays);
        }

        Gson gson = new Gson();
        String json = gson.toJson(leaveDaysMap);
        res.getWriter().write(json);
    }
}
