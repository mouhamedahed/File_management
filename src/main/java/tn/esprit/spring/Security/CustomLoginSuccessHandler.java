package tn.esprit.spring.Security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Service.Session_UserDetails;
import tn.esprit.spring.Service.User_Service;
@Component
public class CustomLoginSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler{
	@Autowired
	private User_Service I_User_Service;
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		 Session_UserDetails userDetails =  (Session_UserDetails) authentication.getPrincipal();
	        Dbo_User user = userDetails.getUser();	
	        if (user.getFailedAttempt() > 0) {
	        	I_User_Service.resetFailedAttempts(user.getEmail());
	        }
	        super.onAuthenticationSuccess(request, response, authentication);
	}
	
}
