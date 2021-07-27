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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author shkhan
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode
@Table(name = "user_ratings")
public class UserRating implements Serializable {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Column(name="user_rating")
	private float stars=0.0f;

	@OneToOne(fetch=FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinColumn(name = "discussion_id",nullable=false)
	private Discussion chatRoom;
	
	@ManyToOne
    @JoinColumn(name="from_user_id", nullable=false)
	private User fromUser;

	@ManyToOne
	@JoinColumn(name="to_user_id", nullable=false)
	private User toUser;
	
}
