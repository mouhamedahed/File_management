package tn.esprit.spring.Service;

import java.util.Collection;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.esprit.spring.Entity.Dbo_Role;
import tn.esprit.spring.Repository.Role_Repository;
 
 

@Service
public class User_Role_Service implements Interface_User_Role_Service{
	
	
	@Autowired
	 Role_Repository role_Repository;

	@Override
	public Dbo_Role addRole(Dbo_Role roleToAdd) {
		 
		return role_Repository.save(roleToAdd);
	}

	@Override
	public Collection<Dbo_Role> retrieveAllRole() {
		 
		return role_Repository.findAll();
	}

	@Override
	public Dbo_Role saveOrUpdate(Dbo_Role role) {
		
		return role_Repository.saveAndFlush(role);
	}

 

	@Override
	public String deleteById(Long id) {
		JSONObject jsonObject = new JSONObject();
		try {
			role_Repository.deleteById(id);
			jsonObject.put("message", "User deleted successfully ");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	@Override
	public Optional<Dbo_Role> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}
