package com.portfolio.service.impl;

import com.portfolio.entity.ProjectView;
import com.portfolio.repository.ProjectViewRepository;
import com.portfolio.service.ProjectViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectViewServiceImpl implements ProjectViewService {

    private final ProjectViewRepository projectViewRepository;

    @Override
    @Async
    @Transactional
    public void trackView(UUID projectId, String ipAddress, String userAgent) {
        try {
            LocalDate today = LocalDate.now();

            Optional<ProjectView> existing = projectViewRepository
                    .findByProjectIdAndViewDateAndIpAddress(projectId, today, ipAddress);

            if (existing.isPresent()) {
                ProjectView view = existing.get();
                view.setCount(view.getCount() + 1);
                projectViewRepository.save(view);
            } else {
                ProjectView newView = ProjectView.builder()
                        .projectId(projectId)
                        .viewDate(today)
                        .ipAddress(ipAddress)
                        .userAgent(truncateUserAgent(userAgent))
                        .count(1L)
                        .build();
                projectViewRepository.save(newView);
            }
        } catch (Exception e) {
            log.warn("Failed to track view for project {}: {}", projectId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalViews(UUID projectId) {
        Long views = projectViewRepository.getTotalViewsByProjectId(projectId);
        return views != null ? views : 0L;
    }

    private String truncateUserAgent(String userAgent) {
        if (userAgent == null) return null;
        return userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent;
    }
}
