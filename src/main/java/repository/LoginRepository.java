package repository;

public interface LoginRepository {
    boolean AuthenticateUser(String Email, String Password);
    int getLoginId(String Email);
}
