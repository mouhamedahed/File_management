package tn.esprit.spring.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.spring.Entity.Confirmation_Token_User;
import tn.esprit.spring.Entity.Dbo_Role;
import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Entity.Dbo_User_Provider;
import tn.esprit.spring.Entity.ERole;
import tn.esprit.spring.Repository.ConfirmationTokenRepository;
import tn.esprit.spring.Repository.Role_Repository;
import tn.esprit.spring.Repository.User_Repository;

@Service("I_User_Service")
@Transactional
public class User_Service implements Interface_User_Service {

	Logger logger_Service = Logger.getLogger(this.getClass().getName());
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private User_Repository Jpa_User_Repository;
	@Autowired
	Role_Repository Jpa_Role_Repository;
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;
	@Autowired
	private Email_Sender_Service emailSenderService;
	/* First, we declare the maximum number of failed login attempts allowed */
	public static final int MAX_FAILED_ATTEMPTS = 3;
	/* Second we declare duration of the lock time in milliseconds */
	private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24hours

	/*
	 * this method updates the number of failed attempts of a user. It is called
	 * each time the user fails to login (CallBack Exception Bad credentials or
	 * another exception generated has injected in method Authenticate).
	 */
	public void increaseFailedAttempts(Dbo_User userAttempt) {
		int newFailAttempts = userAttempt.getFailedAttempt() + 1;
		Jpa_User_Repository.updateFailedAttempts(newFailAttempts, userAttempt.getEmail());
		logger_Service.info("Failed attempts has been affected by 1 **successefully**");
	}
	/* sets the number of failed attempts to zero. This method will be called when the user has logged in successfully. */
	public void resetFailedAttempts(String email) {
		Jpa_User_Repository.updateFailedAttempts(0, email);
		logger_Service.info("Failed attempts has been all reseted **successefully**");
	}

	/*  locks the user’s account if the number of failed logins reach the maximum allowed times */
	public void lock(Dbo_User userAttempt) {
		userAttempt.setAccountNonLocked(false);
		userAttempt.setLockTime(new Date());

		Jpa_User_Repository.save(userAttempt);
		logger_Service.info("Account of "+userAttempt.getFullName()+" has been locked **successefully** ");
	}

	/*  unlocks the user’s account when lock duration expires, allowing the user to login as usual */
	public boolean unlockWhenTimeExpired(Dbo_User userToUnlock) {
		long lockTimeInMillis = userToUnlock.getLockTime().getTime();
		long currentTimeInMillis = System.currentTimeMillis(); // SYSTEM.DATETIME NOW

		if (lockTimeInMillis + LOCK_TIME_DURATION < currentTimeInMillis) {
			userToUnlock.setAccountNonLocked(true);
			userToUnlock.setLockTime(null);
			userToUnlock.setFailedAttempt(0);

			Jpa_User_Repository.save(userToUnlock);
			logger_Service.info("Account of "+userToUnlock.getFullName()+" has been unlocked **successefully**");
			return true;
		}

		return false;
	}

	@Override
	public Dbo_User addUser(Dbo_User userToAdd) {

		return Jpa_User_Repository.save(userToAdd);
	}

	@Override
	public List<Dbo_User> retrieveAllUsers() {
		return (List<Dbo_User>) Jpa_User_Repository.findAll();
	}

	@Override
	public Optional<Dbo_User> findById(Long id) {
		return Jpa_User_Repository.findById(id);
	}

	@Override
	public Dbo_User saveOrUpdate(Dbo_User user) {
		Dbo_User userToSave = new Dbo_User();

		userToSave.setFirstName(user.getFirstName());
		userToSave.setLastName(user.getLastName());
		userToSave.setPassword(passwordEncoder.encode(user.getPassword()));
		userToSave.setEmail(user.getEmail());
		userToSave.setActif(false);
		userToSave.setDate(user.getDate());

		return Jpa_User_Repository.save(userToSave);
	}

	@Override
	public String deleteById(Long id) {

		JSONObject jsonObject = new JSONObject();
		try {
			Jpa_User_Repository.deleteById(id);
			jsonObject.put("message", "User deleted successfully ");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	public void create_NewUser_After_OAuth2_Login_Success(String email, String name, Date birthdate,
			Dbo_User_Provider provider, Date now, String sessionId) {
		// TODO Auto-generated method stub
		Dbo_User UserOAuth2ToSave = new Dbo_User();
		UserOAuth2ToSave.setEmail(email);
		UserOAuth2ToSave.setFirstName(name);
		UserOAuth2ToSave.setLastName(name);
		UserOAuth2ToSave.setActif(true);
		UserOAuth2ToSave.setDate(birthdate);
		UserOAuth2ToSave.setCreatedTime(new Date());
		UserOAuth2ToSave.setLastLoggedIn(now);
		UserOAuth2ToSave.setPassword(passwordEncoder.encode(name));

		Set<Dbo_Role> roles = new HashSet<>();
		Dbo_Role userRole = Jpa_Role_Repository.findByName(ERole.ROLE_USER)
				.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		roles.add(userRole);

		UserOAuth2ToSave.setRole(roles);
		UserOAuth2ToSave.setDbo_User_Provider(provider);
		UserOAuth2ToSave.setSession_Id(sessionId);

		Jpa_User_Repository.save(UserOAuth2ToSave);
		Confirmation_Token_User confirmationToken = new Confirmation_Token_User(UserOAuth2ToSave);

		confirmationTokenRepository.save(confirmationToken);

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email);
		mailMessage.setSubject("!! Secret Information !!");
		mailMessage.setFrom("ayoub.benzahra@esprit.tn");
		mailMessage.setText(" Dear Mr " + name + " "
				+ "following an agreement created with the Google OAuth Client service, your password will be generated as follows [FirstName + LastName]");

		emailSenderService.sendEmail(mailMessage);

	}

	public void update_the_Existing_User_After_OAuth2_Login_Success(Dbo_User customer, String name,
			Dbo_User_Provider providerSession, Date now, String sessionId) {
		// TODO Auto-generated method stub

		// customer.setDbo_User_Provider(providerSession);
		customer.setLastLoggedIn(now);
		customer.setSession_Id(sessionId);
		Jpa_User_Repository.save(customer);
	}

	@Override
	public Long FindIDUserByEmail(String email) {
		return Jpa_User_Repository.FindIDUserByEmail(email);
	}

}
