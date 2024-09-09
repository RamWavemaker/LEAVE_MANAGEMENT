package services;

public interface LoginService {
    boolean AuthenticateUser(String Email, String Password);
    int getLoginId(String Email);
}
