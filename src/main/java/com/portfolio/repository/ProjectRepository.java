package com.portfolio.repository;

import com.portfolio.entity.Project;
import com.portfolio.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByStatusOrderByDisplayOrderAsc(ProjectStatus status);
    List<Project> findByIsFeaturedTrueOrderByDisplayOrderAsc();
    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE " +
           "LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Project> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}