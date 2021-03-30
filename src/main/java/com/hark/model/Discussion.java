/**
 * 
 */
package com.hark.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.springframework.data.redis.core.RedisHash;

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
@Table(name = "discussions")
//@RedisHash("chatrooms")
public class Discussion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private String id;

	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@OneToOne(mappedBy ="chatRoom",cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private UserRating userRating;
	
	@OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="discussion_id")
	private Set<DiscussionUser> users =  new HashSet<>(0);
	
	public void addUser(DiscussionUser user) {
		this.users.add(user);
	}
	public void removeUser(DiscussionUser user) {
		this.users.remove(user);
	}
	public int getNumberOfConnectedUsers(){
		return this.users.size();
	}

	
}
