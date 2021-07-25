package tn.esprit.spring.Entity;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Confirmation_Token_User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "token_id")
	private long tokenid;

	@Column(name = "confirmation_token")
	private String confirmationToken;

	public Confirmation_Token_User() {
		super();
	}

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@OneToOne(targetEntity = Dbo_User.class, fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private Dbo_User user;

	public Confirmation_Token_User(Dbo_User user) {
		 

		this.user = user;
		createdDate = new Date();
		confirmationToken = UUID.randomUUID().toString();
	}
	
	

	public long getTokenid() {
		return tokenid;
	}

	public void setTokenid(long tokenid) {
		this.tokenid = tokenid;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Dbo_User getUser() {
		return user;
	}

	public void setUser(Dbo_User user) {
		this.user = user;
	}
	
}
