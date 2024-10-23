package com.great.project.db.db.manager.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.cerner.augero.security.CustomWebSecurityConfig;
import com.cerner.augero.security.annotation.AugeroWebSecurityConfig;

@AugeroWebSecurityConfig
public class DbManagerSecurityConfig implements CustomWebSecurityConfig {

    /**
     * Method to configure authorization on endpoint. <br>
     * To run locally, when setting credentials, change ".authenticated()" to ".permitAll()".
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/v1/**").authenticated();
        http.csrf().disable();
    }

}
