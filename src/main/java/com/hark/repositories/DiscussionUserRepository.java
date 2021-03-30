package com.hark.repositories;

import com.hark.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hark.model.DiscussionUser;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionUserRepository extends JpaRepository<DiscussionUser, String> {

    List<Discussion> findByUsername(String username);

}
