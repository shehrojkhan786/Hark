package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hark.model.DiscussionUser;

public interface DiscussionUserRepository extends JpaRepository<DiscussionUser, String> {

}
