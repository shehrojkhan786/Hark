package com.hark.repositories;

import com.hark.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {

    //public List<Discussion> findByUsername(String username);
    Optional<Discussion> findByDiscussionId(String discussionId);
    void deleteByDiscussionId(String discussionId);
}
