package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hark.model.UserRating;

@Repository
public interface RatingRepository extends JpaRepository<UserRating, Long> {
	//List<Badge> findAll();
}
