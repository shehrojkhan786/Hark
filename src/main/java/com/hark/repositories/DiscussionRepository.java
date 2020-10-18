package com.hark.repositories;

import org.springframework.data.repository.CrudRepository;

import com.hark.model.Discussion;

public interface DiscussionRepository extends CrudRepository<Discussion, String> {

}
