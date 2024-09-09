package security;

import exceptions.DatabaseConnectionException;
import exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.LoginService;
import services.impl.LoginServiceImpl;

import java.sql.SQLException;

public class AuthenticateUserImpl implements AuthenticateUser{
    private static Logger logger = LoggerFactory.getLogger(AuthenticateUserImpl.class);
    LoginService loginService;
    public AuthenticateUserImpl(){
        loginService = new LoginServiceImpl();
    }

    public boolean Authenticate(String email, String password){
        try {
            return loginService.AuthenticateUser(email, password);
        } catch (UserNotFoundException e){
            logger.error("User Not Found in Database",e);
            throw new RuntimeException("User Not found",e);
        }
        catch (Exception e) {
            logger.error("Unexpected error during authentication", e);
            throw new RuntimeException("Unexpected error", e);
        }
    }
}
