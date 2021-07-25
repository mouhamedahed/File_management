package tn.esprit.spring.Service;

import java.util.List;
import java.util.Optional;

import tn.esprit.spring.Entity.Dbo_User;

public interface Interface_User_Service {

	Dbo_User addUser(Dbo_User userToAdd);

	List<Dbo_User> retrieveAllUsers();

	Dbo_User saveOrUpdate(Dbo_User user);

	Optional<Dbo_User> findById(Long id);

	String deleteById(Long id);

	Long FindIDUserByEmail(String email);

	public void increaseFailedAttempts(Dbo_User userAttempt);

	public void resetFailedAttempts(String email);

	public void lock(Dbo_User userAttempt);

	public boolean unlockWhenTimeExpired(Dbo_User userToUnlock);
}
