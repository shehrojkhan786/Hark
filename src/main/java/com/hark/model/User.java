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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

	@Email
	private String email;

	private Long phone;

	private String politicalParty;

	private String country;
	
	private String deviceId;
	
	private boolean isProfileCompleted = false;

	private boolean isActive = true;
	
	private boolean isSearching = false;

	public User(String username, String email, String password,Long phone,String politicalParty,String country,String name,String deviceId) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.phone = phone;
		this.politicalParty = politicalParty;
		this.country = country;
		this.name = name;
		this.deviceId = deviceId;
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

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name = "role_id")	
	private Role role;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,mappedBy = "user")
	private Set<Discussion> chatRooms = new HashSet<>(0);
}
