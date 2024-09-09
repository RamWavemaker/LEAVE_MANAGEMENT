package repository.impl;

import exceptions.DataAccessException;
import exceptions.DatabaseConnectionException;
import models.Leaves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LeaveTypeManagementRepository;
import utility.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LeaveTypeManagementRepositoryImpl implements LeaveTypeManagementRepository {
    public static final Logger logger = LoggerFactory.getLogger(LeaveTypeManagementRepositoryImpl.class);
    DatabaseConnector db = null;
    Connection conn;
    public LeaveTypeManagementRepositoryImpl(){
        try{
            db = new DatabaseConnector();
            conn = db.getConnection();
        }catch (SQLException e){
            throw new DatabaseConnectionException("Failed to establish database connection",e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Database driver class not found", e);
        }
    }
    private static final String GET_LIMITED_LEAVES_BY_TYPE = "SELECT LEAVE_TYPE, DAYS_ALLOWED FROM LEAVETYPE_MANAGEMENT";

    public List<Map.Entry<Leaves.LeaveType, Integer>> getLimitedLeavesByType() {

        // Initialize EnumMap
        EnumMap<Leaves.LeaveType, Integer> leaveDaysByType = new EnumMap<>(Leaves.LeaveType.class);

        try (PreparedStatement stmt = conn.prepareStatement(GET_LIMITED_LEAVES_BY_TYPE);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String leaveTypeStr = rs.getString("LEAVE_TYPE");
                int daysAllowed = rs.getInt("DAYS_ALLOWED");
                Leaves.LeaveType leaveType = Leaves.LeaveType.valueOf(leaveTypeStr);
                leaveDaysByType.put(leaveType, daysAllowed);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception while fetching get Limited Leaves By Type",e);
            throw new DataAccessException("SQL Exception while fetching get Limited Leaves By Type",e);
        }
        List<Map.Entry<Leaves.LeaveType, Integer>> leaveList = new ArrayList<>(leaveDaysByType.entrySet());

        return leaveList;
    }
}
