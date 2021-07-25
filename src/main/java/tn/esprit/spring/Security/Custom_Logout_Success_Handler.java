package tn.esprit.spring.Security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import tn.esprit.spring.Repository.User_Repository;
import tn.esprit.spring.Service.User_Service;

public class Custom_Logout_Success_Handler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

		@Autowired
		User_Repository userRepository; 
		@Autowired
		User_Service userService;

		
		
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication )
			throws IOException, ServletException {
		String SessionId =request.getSession().getId();
		Date logOutTime = new Date();
		String email = getCurrentUserLogin();
		Long idUserToSet = userService.FindIDUserByEmail(email);
		userRepository.updateUserTimeLoggedOut(idUserToSet, logOutTime);
		userRepository.updateUserSessionId(SessionId, idUserToSet);
		logger.info("attribute 'Time_Logged_Out' for user with Email :" + email + " with SessionId :" + SessionId
				+ " has been approved successelly");
		Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(0);
        response.addCookie(cookie);
         
        
        if(request.getParameter("expired") != null)
        {
        	 
            response.sendRedirect(request.getContextPath()+"/login?expired=true");
        }
        else
        {
        	
            response.sendRedirect(request.getContextPath() + "/login?logout=true");
        }
	}
	
	public String getCurrentUserLogin() {
		org.springframework.security.core.context.SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		String login = null;
		if (authentication != null)
			if (authentication.getPrincipal() instanceof UserDetails)
				login = ((UserDetails) authentication.getPrincipal()).getUsername();
			else if (authentication.getPrincipal() instanceof String)
				login = (String) authentication.getPrincipal();

		return login;

	}
}
