package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hark.model.Discussion;

public interface DiscussionRepository extends JpaRepository<Discussion, String> {

}
