package com.hark.repositories;

import com.hark.model.DiscussionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionUserRepository extends JpaRepository<DiscussionUser, String> {

    List<DiscussionUser> findByUsername(String username);

}
