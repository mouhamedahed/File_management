package tn.esprit.spring.Security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Repository.User_Repository;
import tn.esprit.spring.Service.User_Service;
@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	@Autowired
	private User_Service I_User_Service;
	 
	@Autowired
	User_Repository Jpa_User_Repository;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String email = request.getParameter("email");
		Dbo_User user = Jpa_User_Repository.findByEmail(email);
		if(Jpa_User_Repository.getaccountNonLocked(email)==true && Jpa_User_Repository.isActif(email) == false) {
			if(Jpa_User_Repository.getfailedAttempt(email) < 	User_Service.MAX_FAILED_ATTEMPTS - 1) {
				I_User_Service.increaseFailedAttempts(user);
			}
			else {
				I_User_Service.lock(user);
				exception = new LockedException("Your account has been locked due to 3 failed attempts."
                        + " It will be unlocked after 24 hours.");
			}
		}
			else if(Jpa_User_Repository.getaccountNonLocked(email) == false){
				if(I_User_Service.unlockWhenTimeExpired(user)){
                    exception = new LockedException("Your account has been unlocked. Please try to login again.");
				}
			}
		
		super.onAuthenticationFailure(request, response, exception);
	}
}
