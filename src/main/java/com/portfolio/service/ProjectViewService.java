package com.portfolio.service;

import java.util.UUID;

public interface ProjectViewService {
    void trackView(UUID projectId, String ipAddress, String userAgent);
    Long getTotalViews(UUID projectId);
}
