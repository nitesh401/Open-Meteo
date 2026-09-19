package com.example.learningdev.repository;

import com.example.learningdev.model.EnrichedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrichedPostRepository extends JpaRepository<EnrichedPost, Long> {
    // Return all records ordered by temperature descending (highest first)
    List<EnrichedPost> findAllByOrderByTemperatureDesc();
}
