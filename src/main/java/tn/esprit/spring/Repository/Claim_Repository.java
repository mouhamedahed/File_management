package tn.esprit.spring.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import tn.esprit.spring.Entity.Claim;

@Repository
public interface Claim_Repository extends CrudRepository<Claim, Long> {


}