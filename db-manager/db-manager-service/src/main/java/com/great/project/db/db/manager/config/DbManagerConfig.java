package com.great.project.db.db.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.pivotal.cfenv.core.CfEnv;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
@Getter
@Setter
public class DbManagerConfig {

    CfEnv cfEnv = new CfEnv();
    String appSpace = cfEnv.getApp().getSpaceId();

    private String spaceGuid = appSpace;

    @Value("${dbManager.schemaServicePlanGuid:}")
    private String schemaServicePlanGuid;

    @Value("${dbManager.hanaCloudServicePlanGuid:}")
    public String hanaCloudServicePlanGuid;

    @Value("${resilience.bulkhead.max_duration:}")
    public int maxWaitDurationInSeconds;

    @Value("${resilience.bulkhead.max_concurrent_calls:}")
    public int maxConcurrentCalls;
}
