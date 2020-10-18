/**
 * 
 */
package com.hark.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "badges")
public class Badge {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="badge_id")
	private Long id;
	
	
	@NotEmpty
	private String name;
	
	@NotEmpty
	private String type;
	
	private boolean isActive = true;
	
	@ManyToMany(mappedBy="badges")
	private Set<User> users = new HashSet<>(0);
	
}
