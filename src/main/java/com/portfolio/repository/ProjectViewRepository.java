package com.portfolio.repository;

import com.portfolio.entity.ProjectView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectViewRepository extends JpaRepository<ProjectView, UUID> {

    Optional<ProjectView> findByProjectIdAndViewDateAndIpAddress(
            UUID projectId, LocalDate viewDate, String ipAddress);

    @Query("SELECT SUM(pv.count) FROM ProjectView pv WHERE pv.projectId = :projectId")
    Long getTotalViewsByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT SUM(pv.count) FROM ProjectView pv WHERE pv.viewDate = :date")
    Long getTotalViewsByDate(@Param("date") LocalDate date);
}
