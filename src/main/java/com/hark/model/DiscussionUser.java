/**
 * 
 */
package com.hark.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shkhan
 *
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="discussion_users")
@Entity
public class DiscussionUser implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@NotEmpty
	private String username;
	
	@OneToOne(mappedBy ="discussionUser",cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private DiscussionFeedback feedBack;
	
	public DiscussionUser(String username) {
		this.username = username;
	}
	
}
