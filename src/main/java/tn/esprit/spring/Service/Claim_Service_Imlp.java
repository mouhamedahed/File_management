package tn.esprit.spring.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import tn.esprit.spring.Entity.Claim;
import tn.esprit.spring.Entity.Dbo_User;
import tn.esprit.spring.Entity.ERole;
import tn.esprit.spring.Entity.RequestApiForm.MessageResponse;
import tn.esprit.spring.Repository.Claim_Repository;
import tn.esprit.spring.Repository.Role_Repository;
import tn.esprit.spring.Repository.User_Repository;
@Service
public  class Claim_Service_Imlp  implements Claim_Service{
   
	
	@Autowired
	Claim_Repository Cl_rep;
	@Autowired
	User_Repository ur1;
	@Autowired
	User_Service us;
	@Autowired
	User_Role_Service ur;
	@Autowired
	private Email_Sender_Service emailSenderService;
	@Autowired
	Role_Repository role_Repository;

	@Autowired
	Interface_User_Service userService;
	public ResponseEntity<?>  Add_Claim(long user_id, Claim claim) {
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);
		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))) {
			/*Claim claimm = Cl_rep.findById(claim.getId()).orElse(null);
			if (claimm != null) {
				return ResponseEntity.ok(new MessageResponse("garden existe déja"));

			}

			else {*/
				Cl_rep.save(claim);
				return ResponseEntity.ok(new MessageResponse("Claim est bien enregistrée"));

			//}

		}

		else {

			return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));

		}
		
	}
	
	
	public ResponseEntity<?>  affectation_Claim_2(long user_id, Claim claim) {
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);
		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))) {
			claim.setState("in_progress");
			claim.setUser(dbo_User);
			Cl_rep.save(claim);
				return ResponseEntity.ok(new MessageResponse("claim est bien enregistrée"));

			//}

		}

		else {

			return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));

		}
		
	}
	
	
	public ResponseEntity<?>  affectation_Claim(long user_id, long claim_id) {
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);
		Claim claim = Cl_rep.findById(claim_id).orElse(null);


		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))) {
		
			claim.setState("in_progress");
				Cl_rep.save(claim);
				return ResponseEntity.ok(new MessageResponse("claim est bien affecter"));

			//}

		}

		else {

			return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));

		}
		
	}


	
	public ResponseEntity<?> Delete_Claim(long user_id, int Claim_id) {
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);
		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))) {
			 Claim claim =Cl_rep.findById((long) Claim_id).orElse(null);
			 if ( claim != null) {
				 Cl_rep.deleteById((long) Claim_id);
					return ResponseEntity.ok(new MessageResponse("claim est supprimé "));
				} else {
					return ResponseEntity.ok(new MessageResponse("claim n'existe pas"));
				}
			}

			else {

				return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));

			}

		}


	@Override
	public ResponseEntity<?> Retrieve_All_Claims(long user_id) {
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);

		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))){
		//List <Claim> claims= (List<Claim>) garden.getClaims();
			List<Claim> claims = new ArrayList<>();
			for (Claim claim : Cl_rep.findAll()) {
				System.out.println(claim.toString());
				claims.add(claim);
				
			}
		return ResponseEntity.ok(new MessageResponse(" "+claims));
		}
		return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));
	}
		

	@Override
	public ResponseEntity<?> Validate_Claim(long user_id, int Claim_id, Claim claim) {
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);
		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))) {
			 Claim Claim_To_Update =Cl_rep.findById((long) Claim_id).orElse(null);
			if ( Claim_To_Update != null) {
				if(Claim_To_Update.getState().equals("in_progress"))
				
				Claim_To_Update.setState("valid");
				else {
					return ResponseEntity.ok(new MessageResponse("deja valider "));

				}
				
				Cl_rep.save(Claim_To_Update);
				return ResponseEntity.ok(new MessageResponse("Claim est bien modifiée "));
			} else {
				return ResponseEntity.ok(new MessageResponse("Claim n'existe pas"));
			}
		}

		else {

			return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));

		}

	}

	@Override
	public ResponseEntity<?>investisment (long user_id){
		Dbo_User dbo_User = ur1.findById(user_id).orElse(null);
		System.out.println("***************************************");

		System.out.println("***************************************");

		if (dbo_User.getRole().stream().anyMatch(e -> e.getName().equals(ERole.ROLE_ADMIN))){
		int nbr=0;
		List<Claim> claims = new ArrayList<>();
		List<String> claims1 = new ArrayList<>();


		for (Claim claim : claims) {
			if(claim.getState().equals("valid"))
			{
				nbr++;
			}
		//	System.out.println("rani nbr rrrrr "+nbr);
		}
			if(nbr >= 2)
			{
				SimpleMailMessage mailMessage = new SimpleMailMessage();
				mailMessage.setSubject("investisment !");
				mailMessage.setFrom("mouhamed.ahed@esprit.tn");
				mailMessage.setText("  vous passer le nombre maximum de reclamtions vous devez contacter l'adminstrateur voiçi tes listes des reclamations " +claims1);

				emailSenderService.sendEmail(mailMessage);
				return ResponseEntity.ok(new MessageResponse("voir mail!"));

			}
			else
			{
				return ResponseEntity.ok(new MessageResponse("*************"));

			}
			
		}
		else {

			return ResponseEntity.ok(new MessageResponse("user n'est pas un admin"));

		}
	
		}
}
	
	
	
	


	
	
	

	


