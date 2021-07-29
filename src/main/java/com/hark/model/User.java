/**
 * 
 */
package com.hark.model;

import java.io.Serializable;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@Entity
@Table(name = "users",
		uniqueConstraints = { 
		@UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email")
	})
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotEmpty
	@Size(min = 5, max = 15)
	private String username;

	@NotEmpty
	@Size(min = 5)
	@JsonIgnore
	private String password;

	@Email
	@NotEmpty
	@Column(unique = true)
	private String email;

	private Long phone;

	private String politicalParty;

	private String country;
	
	private String deviceId;

	@Column(name="user_rating")
	private float stars = 0.0f;
	
	private boolean isProfileCompleted = false;

	private boolean isActive = true;
	
	private boolean isSearching = false;

	@Column(name = "verification_code", length = 64)
	private String verificationCode;
	private boolean enabled;

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	@OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL,mappedBy="toUser")
	private Set<UserRating> ratings = new HashSet<>(0);
	
	@ManyToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinTable(
	        name = "user_badges", 
	        joinColumns = { @JoinColumn(name = "user_id") }, 
	        inverseJoinColumns = { @JoinColumn(name = "badge_id") }
	    )	
	private Set<Badge> badges = new HashSet<>(0);

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "role_id")	
	private Role role;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_discussions",
			joinColumns = { @JoinColumn(name = "user_id") },
			inverseJoinColumns = { @JoinColumn(name = "discussion_id") }
	)
	private Set<Discussion> discussions = new HashSet<>(0);
}
