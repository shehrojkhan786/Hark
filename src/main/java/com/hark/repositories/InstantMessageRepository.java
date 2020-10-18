package com.hark.repositories;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import com.hark.model.InstantMessage;


@Repository
public interface InstantMessageRepository extends CassandraRepository<InstantMessage, Long> {
	
	List<InstantMessage> findInstantMessagesByUsernameAndChatRoomId(String username, String chatRoomId);
}
