package com.rshinna.taskboardapi.auth.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
  @Value("${app.rate-limit.enabled:true}")
  private boolean rateLimitEnabled;

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.builder().capacity(5).refillGreedy(5, Duration.ofMinutes(1)).build())
        .build();
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if (!rateLimitEnabled) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!request.getMethod().equals("POST") ||
            !request.getRequestURI().equals("/auth/login")) {
      filterChain.doFilter(request, response);
      return;
    }

    String clientIp = request.getRemoteAddr();
    Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

    if (bucket.tryConsume(1)) {
      filterChain.doFilter(request, response);
    } else {
      log.warn("Rate limit exceeded for IP: {}", clientIp);
      response.setStatus(429);
      response.setContentType("application/json");
      response.getWriter().write("{\"error\":\"Too Many Requests\",\"message\":\"Você excedeu o limite de 5 requisições por minuto.\"}");
    }

  }
}
