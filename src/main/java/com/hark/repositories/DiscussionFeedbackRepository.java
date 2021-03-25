package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hark.model.DiscussionFeedback;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionFeedbackRepository extends JpaRepository<DiscussionFeedback, Long> {

}
