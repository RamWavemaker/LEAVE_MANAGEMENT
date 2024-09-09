package repository.impl;

import exceptions.DataAccessException;
import exceptions.DatabaseConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LoginRepository;
import utility.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepositoryImpl implements LoginRepository {
    public static final Logger logger = LoggerFactory.getLogger(LoginRepositoryImpl.class);
    DatabaseConnector db = null;
    Connection conn;

    public LoginRepositoryImpl(){
        try{
            db = new DatabaseConnector();
            conn = db.getConnection();
        }catch (SQLException e){
            throw new DatabaseConnectionException("Failed to establish database connection",e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Database driver class not found", e);
        }
    }
    private static final String AUTHENTICATE_USER = "SELECT * FROM LOGIN WHERE EMAIL_ID = ? AND PASSWORD = ?";
    private static final String GETLOGIN_ID = "SELECT ID FROM LOGIN WHERE EMAIL_ID = ?";

    public boolean AuthenticateUser(String Email, String Password) {
        try (PreparedStatement pst = conn.prepareStatement(AUTHENTICATE_USER)) {
            pst.setString(1, Email);
            pst.setString(2, Password);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.error("Error while authenticating user", e);
            throw new DataAccessException("SQL Exception while authenticating user",e);
        }
    }


    public int getLoginId(String Email)  {
        logger.debug("Entered get UserId method with email: {}", Email);
        try (PreparedStatement pst = conn.prepareStatement(GETLOGIN_ID)) {
            pst.setString(1, Email);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("ID");
                    logger.debug("The user ID is: {}", userId);
                    return userId;
                } else {
                    return -1;
                }
            }
        }catch (SQLException e){
            throw new DataAccessException("SQL Exception while fetching loginId",e);
        }
    }
}
