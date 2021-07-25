package tn.esprit.spring.Service;

import org.springframework.http.ResponseEntity;

import tn.esprit.spring.Entity.Claim;

public interface Claim_Service {

	public ResponseEntity<?> Add_Claim(long user_id, Claim C);

	public ResponseEntity<?> Delete_Claim(long user_id, int Claim_id);

	public ResponseEntity<?> Retrieve_All_Claims(long user_id);

	public ResponseEntity<?> affectation_Claim_2(long user_id, Claim claim);

	public ResponseEntity<?> Validate_Claim(long user_id, int claim_id, Claim claim);

	public ResponseEntity<?> investisment(long user_id);

}
