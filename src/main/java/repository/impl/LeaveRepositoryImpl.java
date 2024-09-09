package repository.impl;

import exceptions.*;
import models.Leaves;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LeaveRepository;
import utility.DatabaseConnector;

import java.sql.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class LeaveRepositoryImpl implements LeaveRepository {
    public static final Logger logger = LoggerFactory.getLogger(LeaveRepositoryImpl.class);
    DatabaseConnector db = null;
    Connection conn;
    public LeaveRepositoryImpl(){
        try{
            db = new DatabaseConnector();
            conn = db.getConnection();
        }catch (SQLException e){
            throw new DatabaseConnectionException("Failed to establish database connection",e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Database driver class not found", e);
        }
    }

    private static final String ADD_LEAVES = "INSERT INTO LEAVES (FROM_DATE, TO_DATE,APPLIED_DATE, EMPLOYEE_ID, LOGIN_ID,LEAVE_TYPE,LEAVE_COMMENT) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_LEAVES = "SELECT * FROM LEAVES WHERE LOGIN_ID = ?";
    private static final String STATUS_CHANGE = "UPDATE LEAVES SET STATUS = ? WHERE ID = ?";   //will go to leaves
    private static final String GET_LEAVES_BY_STATUS = "SELECT * FROM LEAVES WHERE STATUS = ? AND EMPLOYEE_ID = ?";
    private static final String GET_ID_OF_LEAVES_BY_TYPE_AND_USER = "SELECT ID FROM LEAVES WHERE LEAVE_TYPE = ? AND EMPLOYEE_ID = ?";
    private static final String GET_NO_OF_DAYS_BETWEEN_DATES = "SELECT FROM_DATE, TO_DATE FROM LEAVES WHERE ID = ?";



    public int addLeaves(Date fromDate, Date toDate, Date AppliedDate, int employeeId, int loginId, String leaveType, String leaveComment) {
        logger.debug("Adding leave for Employee ID: {}", employeeId);

        try (PreparedStatement pst = conn.prepareStatement(ADD_LEAVES, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pst.setDate(1, fromDate);
            pst.setDate(2, toDate);
            pst.setDate(3, AppliedDate);
            pst.setInt(4, employeeId);
            pst.setInt(5, loginId);
            pst.setString(6, leaveType);
            pst.setString(7, leaveComment);

            int affectedRows = pst.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Insertion failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Returning the generated ID
                } else {
                    throw new NoIdObtainedException("Creating leave failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception occurred while adding leave", e);
            throw new DataAccessException("Database error occurred while adding leave", e);
        }
    }


    public List<Leaves> getLeaves(int userid) {
        logger.debug("Getting User by id: {}", userid);
        List<Leaves> leavesList = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_LEAVES)) {
            pst.setInt(1, userid);
            ResultSet resultSet = pst.executeQuery();
            while (resultSet.next()) {
                Leaves leaves = new Leaves();
                leaves.setId(resultSet.getInt("ID"));
                leaves.setFromDate(resultSet.getDate("FROM_DATE"));
                leaves.setToDate(resultSet.getDate("TO_DATE"));
                leaves.setAppliedDate(resultSet.getDate("APPLIED_DATE"));
                leaves.setLoginid(resultSet.getInt("LOGIN_ID"));
                leaves.setStatus(Leaves.Status.valueOf(resultSet.getString("Status")));
                leaves.setEmpid(resultSet.getInt("EMPLOYEE_ID"));
                leaves.setLeaveType(Leaves.LeaveType.valueOf(resultSet.getString("LEAVE_TYPE")));
                leaves.setLeaveComment(resultSet.getString("LEAVE_COMMENT"));
                leavesList.add(leaves);
            }
        } catch (SQLException e) {
            logger.debug("SQL Exception while fetching leaves",e);
            throw new DataAccessException("SQL Exception while fetching leaves",e);
        }
        return leavesList;
    }

    public List<Leaves> getLeavesOfEmployees(List<Integer> empIds) {
        List<Leaves> leavesList = new ArrayList<>();
        if (empIds == null || empIds.isEmpty()) {
            throw new EmptyEmployeeIdsException("The employee IDs list cannot be null or empty.");
        }

        // Create a string of placeholders for the prepared statement
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < empIds.size(); i++) {
            if (i > 0) {
                placeholders.append(",");
            }
            placeholders.append("?");
        }

        String query = "SELECT * FROM LEAVES WHERE EMPLOYEE_ID IN (" + placeholders.toString() + ")";

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            // Set the parameters for the prepared statement
            for (int i = 0; i < empIds.size(); i++) {
                pst.setInt(i + 1, empIds.get(i));
            }

            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    Leaves leaves = new Leaves();
                    leaves.setId(resultSet.getInt("ID"));
                    leaves.setFromDate(resultSet.getDate("FROM_DATE"));
                    leaves.setToDate(resultSet.getDate("TO_DATE"));
                    leaves.setAppliedDate(resultSet.getDate("APPLIED_DATE"));
                    leaves.setLoginid(resultSet.getInt("LOGIN_ID"));
                    leaves.setStatus(Leaves.Status.valueOf(resultSet.getString("STATUS")));
                    leaves.setEmpid(resultSet.getInt("EMPLOYEE_ID"));
                    leaves.setLeaveType(Leaves.LeaveType.valueOf(resultSet.getString("LEAVE_TYPE")));
                    leaves.setLeaveComment(resultSet.getString("LEAVE_COMMENT"));
                    leavesList.add(leaves);
                }
            } catch (SQLException e) {
                logger.error("Error processing result set", e);
                throw new ResultSetProcessingException("Error processing result set",e);
            }
        } catch (SQLException e) {
            logger.error("SQL Exception during query execution", e);
            throw new DataAccessException("SQL Exception while fetching getting Leaves Of Employees",e);
        }
        return leavesList;
    }


    public boolean statusChange(Leaves.Status newStatus, int leaveId) {
        try (PreparedStatement pst = conn.prepareStatement(STATUS_CHANGE)) {
            pst.setString(1, newStatus.name()); // Use the name() method to get the string representation of the enum
            pst.setInt(2, leaveId);
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error occurred while updating leave status", e);
            throw new DataAccessException("Error occurred while updating leave status",e);
        }
    }

    public List<Leaves> getLeavesByStatus(Leaves.Status status, int userid) {
        logger.debug("I am coming into this");
        List<Leaves> leaves = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_LEAVES_BY_STATUS)) {
            pst.setString(1, String.valueOf(status));
            pst.setInt(2, userid);
            ResultSet resultSet = pst.executeQuery();
            logger.debug("I am coming into this");
            while (resultSet.next()) {
                logger.debug("I am coming into this");
                Leaves leave = new Leaves();
                leave.setId(resultSet.getInt("ID"));
                leave.setEmpid(resultSet.getInt("EMPLOYEE_ID"));
                leave.setFromDate(resultSet.getDate("FROM_DATE"));
                leave.setToDate(resultSet.getDate("TO_DATE"));
                leave.setAppliedDate(resultSet.getDate("APPLIED_DATE"));
                leave.setLeaveType(Leaves.LeaveType.valueOf(resultSet.getString("LEAVE_TYPE")));
                leave.setLeaveComment(resultSet.getString("LEAVE_COMMENT"));
                leave.setStatus(Leaves.Status.valueOf(resultSet.getString("STATUS")));
                leaves.add(leave);
            }
        } catch (SQLException e) {
            logger.debug("Sql Exception while getting leavesbystatus",e);
            throw new DataAccessException("Sql Exception while getting leavesbystatus",e);
        }
        return leaves;
    }


    public List<Integer> getIdOfLeavesByTypeAndUser(Leaves.LeaveType leaveType, int userid) {
        List<Integer> leaveIds = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_ID_OF_LEAVES_BY_TYPE_AND_USER)) {
            pst.setString(1, leaveType.name());
            pst.setInt(2, userid);
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    leaveIds.add(resultSet.getInt("ID"));
                }
            }
        }catch (SQLException e){
            logger.debug("SQL Exception while fetching getId of leaves by type and user",e);
            throw new DataAccessException("SQL Exception while fetching getId of leaves by type and user",e);
        }
        return leaveIds;
    }



    public int getNoOfDaysBetweenDates(List<Integer> leaveIds, List<LocalDate> holidays){
        int totalDays = 0;

        try (PreparedStatement preparedStatement = conn.prepareStatement(GET_NO_OF_DAYS_BETWEEN_DATES)) {
            for (Integer leaveId : leaveIds) {
                preparedStatement.setInt(1, leaveId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Date fromDate = resultSet.getDate("FROM_DATE");
                        Date toDate = resultSet.getDate("TO_DATE");

                        if (fromDate != null && toDate != null) {
                            LocalDate startDate = fromDate.toLocalDate();
                            LocalDate endDate = toDate.toLocalDate();
                            totalDays += calculateWorkingDays(startDate, endDate, holidays);
                        } else {
                            logger.warn("Warning: Null dates found for LEAVE_ID: {}", leaveId);
                        }
                    } else {
                        logger.error("Warning: No records found for LEAVE_ID: {}", leaveId);
                    }
                }
            }
        }catch (SQLException e){
            logger.error("SQL Exception while fetching get NoOfDays Between Dates",e);
            throw new DataAccessException("SQL Exception while fetching get NoOfDays Between Dates",e);
        }

        return totalDays;
    }

    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate, List<LocalDate> holidays) {
        int workingDays = 0;
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && !holidays.contains(date)) {
                workingDays++;
            }
            date = date.plusDays(1);
        }

        return workingDays;
    }

}
