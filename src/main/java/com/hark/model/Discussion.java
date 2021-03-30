/**
 * 
 */
package com.hark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
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
public class Discussion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name="discussion_id")
	private String discussionId;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	@OneToOne(mappedBy ="chatRoom",cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	private UserRating userRating;
	
	@OneToMany(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="discussion_id",referencedColumnName="discussion_id")
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
