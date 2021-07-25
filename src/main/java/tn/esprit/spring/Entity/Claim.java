package tn.esprit.spring.Entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "T_Claim")

public class Claim  implements Serializable{
private static final long serialVersionUID = 1L;
	
	/*-----------------------****Bean_Attributes****   *********-------------------------------------*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Claim_id")
	private Long id;
	@Temporal(TemporalType.DATE)
	private Date Date;
	private String state;
	@Column(columnDefinition="varchar(1000)",name="discription")
	private String Discription;
	private  String claimname;

	@ManyToOne
	private Dbo_User user;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getDate() {
		return Date;
	}
	public void setDate(Date date) {
		Date = date;
	}

	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getDiscription() {
		return Discription;
	}
	public void setDiscription(String discription) {
		Discription = discription;
	}

	public String getClaimname() {
		return claimname;
	}
	public void setClaimname(String claimname) {
		this.claimname = claimname;
	}

	public Dbo_User getUser() {
		return user;
	}
	public void setUser(Dbo_User user) {
		this.user = user;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Claim(Long id, java.util.Date date, String state, String discription, String claimname, Dbo_User user) {
		super();
		this.id = id;
		Date = date;
		this.state = state;
		Discription = discription;
		this.claimname = claimname;
		this.user = user;
	}
	public Claim() {
		super();
	}

	
	
	
}