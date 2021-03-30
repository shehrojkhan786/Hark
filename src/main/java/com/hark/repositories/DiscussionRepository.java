package com.hark.repositories;

import com.hark.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    //public List<Discussion> findByUsername(String username);
    List<Discussion> findByDiscussionId(String discussionId);
    void deleteByDiscussionId(String discussionId);
}
