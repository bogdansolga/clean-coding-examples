package com.great.project.db.cloudfoundryapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
@Getter
@Setter
public class CloudFoundryConfig {
    @Value("${cloudfoundry.loginUrl:}")
    private String loginUrl;
    @Value("${cloudfoundry.apiUrl:}")
    private String apiUrl;
}
