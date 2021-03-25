package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hark.model.DiscussionUser;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionUserRepository extends JpaRepository<DiscussionUser, String> {

}
