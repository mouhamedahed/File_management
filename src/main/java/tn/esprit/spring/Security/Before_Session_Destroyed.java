package tn.esprit.spring.Security;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import tn.esprit.spring.Repository.User_Repository;
import tn.esprit.spring.Service.User_Service;

@Component
public class Before_Session_Destroyed implements ApplicationListener<SessionDestroyedEvent> {

	/*
	 * I am working on one task on which I need to update the attribute
	 * lastLoggedOut in Dbo_User table. I have configure automatic session
	 * expire time. In case of logout its updating Database but in case of
	 * automatic logout by session expired then in this task i'used the
	 * annotation component which this class will be auto detected or scanning
	 * ==> when session (JSESSIONID destroyed and Cookies removed from LOCALHOST
	 * domain) ==> logged out -----> the method onApplicationEvent is auto
	 * handling causes it is overriding using parameter SessionDestroyedEvent
	 * which mean a static variable return session has ended
	 */
	@Autowired
	private User_Repository Jpa_User_Repository;
	@Autowired
	User_Service userService;
	private org.jboss.logging.Logger logger = LoggerFactory.logger(Before_Session_Destroyed.class);

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

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		// this method is called before session destroyed

		HttpSession session = (HttpSession) event.getSource();
		String SessionId = session.getId();
		String email = getCurrentUserLogin();
		Long idUserToSet = userService.FindIDUserByEmail(email);
		
		try {

			logger.info("Email :" + email);

			logger.info("id : " + idUserToSet);
			Date logOutTime = new Date();
			// Block 1 : Update time_Logged_out of Current_User
			Jpa_User_Repository.updateUserTimeLoggedOut(idUserToSet, logOutTime);
			logger.info("attribute 'Time_Logged_Out' for user with Email :" + email + " with SessionId :" + SessionId
					+ " has been approved successelly");
			// End Block Update
			// Block 2 : Delete the old session_Id of current_User then adding new one 
			logger.info("Old session reference : "+Jpa_User_Repository.getOldSessionId(idUserToSet));
			Jpa_User_Repository.updateUserSessionId(SessionId, idUserToSet);
			logger.info("new Session reference has been updated successfully: "+SessionId);
		} catch (Exception exception1) {
			logger.error("attribute 'Time_Logged_Out' for user with Email :" + getCurrentUserLogin()
					+ " with SessionId :" + SessionId + "with id :" + idUserToSet
					+ " is not setted correctly the EXCEPTION is " + exception1.getMessage());
		}
		
		 
 

	}

}
