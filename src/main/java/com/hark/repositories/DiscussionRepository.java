package com.hark.repositories;

import com.hark.model.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscussionRepository extends JpaRepository<Discussion, String> {

    public List<Discussion> findByUsername(String username);

}
