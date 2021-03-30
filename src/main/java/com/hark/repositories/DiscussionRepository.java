package com.hark.repositories;

import com.hark.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, String> {

    //public List<Discussion> findByUsername(String username);

}
