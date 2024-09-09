package repository.impl;

import exceptions.DataAccessException;
import exceptions.DatabaseConnectionException;
import models.Employees;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.EmployeeRepository;
import utility.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    public static final Logger logger = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);
    DatabaseConnector db = null;
    Connection conn;

    public EmployeeRepositoryImpl(){
       try{
           db = new DatabaseConnector();
           conn = db.getConnection();
       }catch (SQLException e){
           throw new DatabaseConnectionException("Failed to establish database connection",e);
       } catch (ClassNotFoundException e) {
           throw new DatabaseConnectionException("Database driver class not found", e);
       }
    }

    private static final String GET_EMPLOYEES_UNDER_MANAGER = "SELECT ID FROM EMPLOYEES WHERE MANAGER_ID = ?";
    private static final String GET_EMPLOYEE_DETAILS = "SELECT * FROM EMPLOYEES WHERE LOGIN_ID = ?";

    public List<Integer> getEmpUnderManager(int managerId) {
        List<Integer> empIds = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_EMPLOYEES_UNDER_MANAGER)) {
            pst.setInt(1, managerId);
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    empIds.add(resultSet.getInt("ID"));
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception while getting employees under manager", e);
            throw new DataAccessException("Exception while getting employees under manager",e);
        }
        return empIds;
    }


    public List<Employees> getEmployeedetails(int loginid){
        List<Employees> employeesList = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_EMPLOYEE_DETAILS)) {
            pst.setInt(1, loginid);

            try (ResultSet resultSet = pst.executeQuery()) {
                if (resultSet.next()) {
                    Employees employee = new Employees();
                    employee.setId(resultSet.getInt("ID"));
                    employee.setName(resultSet.getString("NAME"));
                    employee.setGender(Employees.Gender.valueOf(resultSet.getString("GENDER")));
                    employee.setManagerId(resultSet.getInt("MANAGER_ID"));
                    employee.setLoginid(resultSet.getInt("LOGIN_ID"));
                    employeesList.add(employee);
                }
            }
        }catch (SQLException e){
            logger.debug("SQL Exception occurred while fetching employee details",e);
            throw new DataAccessException("SQL Exception occurred while fetching employee details",e);
        }
        return employeesList;
    }
}

