package tn.esprit.spring.ExterneService;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Entity.Dbo_User_Provider;
import tn.esprit.spring.Repository.User_Repository;
import tn.esprit.spring.Service.User_Service;

@Component
public class OAuth2_Login_Success_Handler extends SimpleUrlAuthenticationSuccessHandler    {
	@Autowired
	User_Service CustomerService;
	@Autowired
	User_Repository CustomerRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Custom_Oauth2_User OauthUser = (Custom_Oauth2_User) authentication.getPrincipal();
		 
		String email = OauthUser.getEmail();
		String name = OauthUser.getName();
		Date birthDate = OauthUser.getBirthdate();
		String session = request.getSession().getId();
		
		Date now = new Date();
		Dbo_User Customer = CustomerRepository.findByEmail(email);

		if (Customer == null) {
			// Create a new user using OAuth2 Google
			CustomerService.create_NewUser_After_OAuth2_Login_Success(email, name, birthDate, Dbo_User_Provider.GOOGLE,
					now, session);
		} else {
			// Update existing User
			CustomerService.update_the_Existing_User_After_OAuth2_Login_Success(Customer, name,
					Dbo_User_Provider.GOOGLE, now , session);

		}

		logger.info("Customer's google account email : " + email);
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
}
