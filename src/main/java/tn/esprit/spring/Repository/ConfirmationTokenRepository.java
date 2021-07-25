package tn.esprit.spring.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.spring.Entity.Confirmation_Token_User;
@Repository
public interface ConfirmationTokenRepository extends CrudRepository<Confirmation_Token_User, String>{

	 Confirmation_Token_User findByconfirmationToken(String confirmationToken);
	  
}
