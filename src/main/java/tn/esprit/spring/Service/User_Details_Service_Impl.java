package tn.esprit.spring.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Repository.User_Repository;

@Service
public class User_Details_Service_Impl implements UserDetailsService {

	/*
	 * We use the @Service annotation to be able to inject User Details Service
	 * Impl_ with @Autowired in Security_Configuration
	 */
	@Autowired
	private User_Repository userRepository;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Dbo_User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Could not find user with this Email " + email);
		}
		return Session_UserDetails.build(user);
	}

	/*
	 * 
	 * It is a class_User_Details_Service_Impl which implements the
	 * UserDetailsService interface: it therefore implements a method verifying
	 * the existence of a user according to the value of email and returning an
	 * object of the UserDetails interface
	 */

}
