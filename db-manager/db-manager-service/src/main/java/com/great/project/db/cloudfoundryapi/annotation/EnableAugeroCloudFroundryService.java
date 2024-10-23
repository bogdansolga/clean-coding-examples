package com.great.project.db.cloudfoundryapi.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.great.project.db.cloudfoundryapi.config.CloudFoundryConfig;
import com.great.project.db.cloudfoundryapi.service.CloudFoundryService;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Import({ CloudFoundryService.class, CloudFoundryConfig.class })
public @interface EnableAugeroCloudFroundryService {

}
