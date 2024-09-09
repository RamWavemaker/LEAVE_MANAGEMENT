package services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LoginRepository;
import repository.impl.LoginRepositoryImpl;
import services.LoginService;

public class LoginServiceImpl implements LoginService {
    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
    LoginRepository loginRepository;
    public LoginServiceImpl(){
        loginRepository = new LoginRepositoryImpl();
    }
    @Override
    public boolean AuthenticateUser(String Email, String Password) {
        return loginRepository.AuthenticateUser(Email,Password);
    }

    @Override
    public int getLoginId(String Email) {
        return loginRepository.getLoginId(Email);
    }
}
