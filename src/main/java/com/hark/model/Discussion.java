/**
 * 
 */
package com.hark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
@Table(name = "discussions")
//@RedisHash("chatrooms")
public class Discussion implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Column(name="discussion_id")
	private String discussionId;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(
			name = "user_discussions",
			joinColumns = { @JoinColumn(name = "discussion_id") },
			inverseJoinColumns = { @JoinColumn(name = "user_id") }
	)
	private Set<User> users = new HashSet<>(0);

	@OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="discussion_id")
	private Set<DiscussionUser> discussionUsers =  new HashSet<>(0);

	private boolean isActive=true;
	
	public void addUser(DiscussionUser user) {
		this.discussionUsers.add(user);
	}
	public void removeUser(DiscussionUser user) {
		this.discussionUsers.remove(user);
	}
	public int getNumberOfConnectedUsers(){
		return this.discussionUsers.size();
	}

	public Discussion(){
		UUID uuid = UUID.randomUUID();
		this.discussionId = uuid.toString();
	}

	public Discussion(String discussionId){
		this.discussionId = discussionId;
	}

}
