package repository.impl;

import exceptions.DataAccessException;
import exceptions.DatabaseConnectionException;
import models.Holidays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.HolidayRepository;
import utility.DatabaseConnector;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class HolidayRepositoryImpl implements HolidayRepository {
    public static final Logger logger = LoggerFactory.getLogger(HolidayRepositoryImpl.class);
    DatabaseConnector db = null;
    Connection conn;

    public HolidayRepositoryImpl(){
        try{
            db = new DatabaseConnector();
            conn = db.getConnection();
        }catch (SQLException e){
            throw new DatabaseConnectionException("Failed to establish database connection",e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Database driver class not found", e);
        }
    }

    private static final String GET_HOLIDAYS_DATES = "SELECT HOLIDAY_DATE FROM HOLIDAYS";
    private static final String GET_HOLIDAYS = "SELECT * FROM HOLIDAYS";


    public List<String> getHolidaysDates(){
        List<String> holidaysDatesList = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_HOLIDAYS_DATES)) {
            ResultSet resultSet = pst.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            while (resultSet.next()) {
                Date sqlDate = resultSet.getDate("HOLIDAY_DATE");
                if (sqlDate != null) {
                    String holidayDate = dateFormat.format(sqlDate);
                    holidaysDatesList.add(holidayDate);
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception occurred while fetching holiday dates" + e.getMessage());
            throw new DataAccessException("SQL Exception occurred while fetching holiday dates",e);
        }
        return holidaysDatesList;
    }


    public List<Holidays> getHolidays(){
        List<Holidays> holidays = new ArrayList<>();
        try (PreparedStatement pst = conn.prepareStatement(GET_HOLIDAYS)) {
            try (ResultSet resultSet = pst.executeQuery()) {
                while (resultSet.next()) {
                    Holidays holiday = new Holidays();
                    holiday.setId(resultSet.getInt("ID"));
                    holiday.setHolidayDate(resultSet.getString("HOLIDAY_DATE")); // Use the correct column name
                    holiday.setOccasion(resultSet.getString("OCCASION"));

                    holidays.add(holiday);
                }
            }
        }catch (SQLException e){
            logger.error("SQL Exception occurred while fetching holiday ",e);
            throw new DataAccessException("SQL Exception occurred while fetching holiday ",e);
        }
        return holidays;
    }
}
