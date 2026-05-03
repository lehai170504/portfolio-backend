package com.portfolio.repository;

import com.portfolio.entity.Experience;
import com.portfolio.enums.ExperienceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, UUID> {
    List<Experience> findByTypeOrderByStartDateDesc(ExperienceType type);
    List<Experience> findAllByOrderByStartDateDesc();
}