package com.hark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hark.model.DiscussionFeedback;

public interface DiscussionFeedbackRepository extends JpaRepository<DiscussionFeedback, Long> {

}
