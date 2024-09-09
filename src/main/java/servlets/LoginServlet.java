package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.AuthenticateUserImpl;
import services.LoginService;
import services.impl.LoginServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
@WebServlet("/loginservlet")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private AuthenticateUserImpl authenticateUser;
    private LoginService loginService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        authenticateUser = new AuthenticateUserImpl();
        loginService = new LoginServiceImpl();
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        String Email = req.getParameter("email");
        String Password = req.getParameter("password");

        if (authenticateUser == null) {
            logger.error("AuthenticateUser instance is null");
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"message\": \"Authentication service is not available\"}");
            return;
        }

        boolean isAuthenticated = false;

        try{
            isAuthenticated = authenticateUser.Authenticate(Email,Password);
        } catch (Exception e) {
            logger.error("Authentication error", e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.getWriter().write("{\"message\": \"Authentication error\"}");
            return;
        }

        if(isAuthenticated){
            int loginUserId = loginService.getLoginId(Email);
            logger.debug("User ID is: {}", loginUserId);

            HttpSession session = req.getSession();
            session.setAttribute("LOGIN_USER_ID",loginUserId);
            String sessionid = session.getId();
            Cookie sessionCookie = new Cookie("SESSIONID", sessionid);
            sessionCookie.setHttpOnly(true);
            sessionCookie.setSecure(true);
            res.addCookie(sessionCookie);

            res.getWriter().write("Login successful!"+sessionid);
            String path = req.getContextPath() + "/index.html";
            res.sendRedirect(path);
        }else{
            String path = req.getContextPath() + "/";
            res.sendRedirect(path);
        }

    }
}
