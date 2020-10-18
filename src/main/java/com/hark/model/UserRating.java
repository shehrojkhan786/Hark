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
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "user_ratings")
public class UserRating {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	private int stars;
	
	@OneToOne(fetch=FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name = "discussion_id",nullable=false)
	private Discussion chatRoom;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
	private User user;
	
}
