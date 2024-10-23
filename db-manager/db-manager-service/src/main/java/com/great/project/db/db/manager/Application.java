package com.great.project.db.db.manager;

import com.great.project.db.cloudfoundryapi.annotation.EnableAugeroCloudFroundryService;
import com.great.project.db.credentialstoreserviceapi.annotation.EnableAugeroCredentialStoreService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.cerner.augero.core.annotation.EnableAugeroCoreUtil;

/**
 * DONE
 * 
 * @author George Nistor <george.nistor@cerner.com>
 */
@EnableAugeroCoreUtil
@EnableAugeroCredentialStoreService
@EnableAugeroCloudFroundryService
@SpringBootApplication
@ComponentScan(basePackages = { "com.cerner.augero.resiliency", "com.cerner.augero.db.manager" })
public class Application {
    public static void main(String[] args) {
        final SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.run();
    }

}
