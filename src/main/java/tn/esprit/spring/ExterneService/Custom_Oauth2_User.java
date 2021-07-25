package tn.esprit.spring.ExterneService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Custom_Oauth2_User implements OAuth2User , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OAuth2User oAuth2User;
	
	
	public Custom_Oauth2_User(OAuth2User oAuth2User) {
		 
		this.oAuth2User = oAuth2User;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return oAuth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return oAuth2User.getAuthorities();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return oAuth2User.getAttribute("name");
	}
	
	public String getFullName(){
		return oAuth2User.getAttribute("name");
	}
	
	public String getEmail(){
		return oAuth2User.getAttribute("email");
	}

	public Date getBirthdate() {
		// TODO Auto-generated method stub
		return oAuth2User.getAttribute("birthdate");
	}
	
	public String getPassword() {
		// TODO Auto-generated method stub
		return oAuth2User.getAttribute("password");
	}
	

}
