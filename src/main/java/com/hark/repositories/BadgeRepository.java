package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hark.model.Badge;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
	//List<Badge> findAll();
}
