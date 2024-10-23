package com.great.project.db.credentialstoreserviceapi.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.great.project.db.credentialstoreserviceapi.config.CredentialStoreConfig;
import com.great.project.db.credentialstoreserviceapi.service.CredentialStoreService;
import com.great.project.db.credentialstoreserviceapi.util.CryptoUtil;
import com.great.project.db.credentialstoreserviceapi.util.RequestUtil;

@Documented
@Retention(RUNTIME)
@Target({ TYPE, ANNOTATION_TYPE })
@Import({ CredentialStoreConfig.class, CredentialStoreService.class, CryptoUtil.class, RequestUtil.class })
public @interface EnableAugeroCredentialStoreService {

}
