package com.portfolio.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        Instant start = Instant.now();

        try {
            log.info("[{}] {} {} - Started",
                    requestId,
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI());

            chain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            Duration duration = Duration.between(start, Instant.now());

            log.info("[{}] {} {} - Completed in {}ms - Status {}",
                    requestId,
                    wrappedRequest.getMethod(),
                    wrappedRequest.getRequestURI(),
                    duration.toMillis(),
                    wrappedResponse.getStatus());

            wrappedResponse.copyBodyToResponse();
        }
    }
}
