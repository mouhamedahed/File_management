package tn.esprit.spring.Contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.esprit.spring.Entity.Claim;
import tn.esprit.spring.Repository.Claim_Repository;
import tn.esprit.spring.Repository.Role_Repository;
import tn.esprit.spring.Repository.User_Repository;
import tn.esprit.spring.Service.Claim_Service;
import tn.esprit.spring.Service.Interface_User_Service;
import tn.esprit.spring.Service.Session_UserDetails;
import tn.esprit.spring.Service.User_Role_Service;
import tn.esprit.spring.Service.User_Service;



@RestController
@RequestMapping("/Claim")
public class ClaimRestController {

	
	
	@Autowired
	Claim_Repository cl_rep;
	@Autowired
	Claim_Service clm_serv;
	@Autowired
	User_Repository user_Repository;

	@Autowired
	User_Repository ur1;
	@Autowired
	User_Service us;
	@Autowired
	User_Role_Service ur;

	@Autowired
	Role_Repository role_Repository;

	@Autowired
	Interface_User_Service userService;


	@PostMapping("/addclaim")
	public ResponseEntity<?> addClaim(Authentication auth, @RequestBody Claim claim) {

		SecurityContextHolder.getContext().setAuthentication(auth);
		Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
		return clm_serv.Add_Claim(userDetails.getId(), claim);
	}
	
	@PostMapping("/affectation_Claim_2")
	public ResponseEntity<?> affectation_Claim_2( Authentication auth, @RequestBody Claim claim) {

		SecurityContextHolder.getContext().setAuthentication(auth);
		Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
		return clm_serv.affectation_Claim_2(userDetails.getId(), claim);
	}
	@GetMapping("/Retrieve_All_Claims")
	public ResponseEntity<?> retrieve_all_claims(Authentication auth, @RequestBody Claim claim)
  {
		SecurityContextHolder.getContext().setAuthentication(auth);
		Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
		
		return clm_serv.Retrieve_All_Claims(userDetails.getId());

		
  }
	@DeleteMapping("/DeleteClaim/{claim_id}")
	public ResponseEntity<?> DeleteClaim(Authentication auth, @PathVariable int claim_id) {

		SecurityContextHolder.getContext().setAuthentication(auth);
		Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
		return clm_serv.Delete_Claim(userDetails.getId(), claim_id);
	}
	@PutMapping("/modify-Claim/{claim_id}")
	public ResponseEntity<?> UpdateContract(Authentication auth, @PathVariable("claim_id") int claim_id, @RequestBody Claim claim) {

		SecurityContextHolder.getContext().setAuthentication(auth);
		Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
		return clm_serv.Validate_Claim(userDetails.getId(), claim_id, claim); 
	}
 
	
	@GetMapping("/investisment")
	public ResponseEntity<?>investisment (Authentication auth)
	{

		SecurityContextHolder.getContext().setAuthentication(auth);
		Session_UserDetails userDetails = (Session_UserDetails) auth.getPrincipal();
	return 	clm_serv.investisment(userDetails.getId());
	}
	
}
