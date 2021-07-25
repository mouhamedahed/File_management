package tn.esprit.spring.Entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("ALL")

@Entity
@Table(name = "T_User_Role")
public class Dbo_Role {

	/*-----------------------****Bean_Attributes****-------------------------------------*/

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Role_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "User_Role_Name", length = 20)
	private ERole name;
	

	/*-----------------------****Getters_Setters_Methods()****-------------------------------------*/

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public ERole getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(ERole name) {
		this.name = name;
	}

	/* Constructor */

	public Dbo_Role(ERole name) {
		super();
		this.name = name;
	}

	public Dbo_Role() {
		super();
	}

}
