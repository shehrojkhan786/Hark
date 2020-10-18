/**
 * 
 */
package com.hark.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@Entity
@Table(name = "users",
		uniqueConstraints = { 
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email"),
		@UniqueConstraint(columnNames = "phone")
	})
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotEmpty
	@Size(min = 5, max = 15)
	private String username;

	@NotEmpty
	@Size(min = 5)
	private String password;

	@NotEmpty
	private String name;

	@NotEmpty
	private String aliasName;

	@Email
	private String email;

	@NotEmpty
	private Long phone;

	@NotEmpty
	private String politicalParty;

	@NotEmpty
	private String country;

	private boolean isActive = true;

	public User(String username, String email, String password,Long phone) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.phone = phone;
	}

	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL,mappedBy="user")
	private Set<UserRating> ratings = new HashSet<>(0);
	
	@ManyToMany(cascade=CascadeType.ALL)
	@JoinTable(
	        name = "user_badges", 
	        joinColumns = { @JoinColumn(name = "user_id") }, 
	        inverseJoinColumns = { @JoinColumn(name = "badge_id") }
	    )	
	private Set<Badge> badges = new HashSet<>(0);

	@OneToOne(mappedBy ="user",cascade = CascadeType.ALL,fetch=FetchType.EAGER)	
	private Role role;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Discussion> chatRooms = new HashSet<>(0);
}
