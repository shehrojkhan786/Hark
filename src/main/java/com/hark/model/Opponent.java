/**
 * 
 */
package com.hark.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shkhan
 *
 */
@Entity
@Table(name = "opponents")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Opponent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@NotNull	
	private Long opponentId1;

	@NotNull
	private Long opponentId2;
	
	@NotNull
	private String discussionRoomId;
	
	public Opponent(Long opponentId1, Long opponentId2, String discussionRoomId) {
		this.discussionRoomId=discussionRoomId;
		this.opponentId1=opponentId1;
		this.opponentId2=opponentId2;
	}
	
}
