/**
 * 
 */
package com.hark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
/**
 * @author shkhan
 *
 */
@AllArgsConstructor
@Data
@Entity
@Table(name = "discussions")
//@RedisHash("chatrooms")
public class Discussion implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Column(name="discussion_id")
	private String discussionId;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	@OneToOne(mappedBy ="chatRoom",cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private UserRating userRating;
	
	@OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="discussion_id")
	private Set<DiscussionUser> users =  new HashSet<>(0);

	private boolean isActive=true;
	
	public void addUser(DiscussionUser user) {
		this.users.add(user);
	}
	public void removeUser(DiscussionUser user) {
		this.users.remove(user);
	}
	public int getNumberOfConnectedUsers(){
		return this.users.size();
	}

	public Discussion(){
		UUID uuid = UUID.randomUUID();
		this.discussionId = uuid.toString();
	}

	public Discussion(String discussionId){
		this.discussionId = discussionId;
	}

}
