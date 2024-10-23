package com.great.project.patientadmin.core.domain.services;

import java.util.Locale;

import com.great.project.patientadmin.core.application.port.secondary.BusinessConfigurationPort;
import org.springframework.context.i18n.LocaleContextHolder;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to set the global language of the current thread given the tenant specific language.
 */
@Slf4j
public class BusinessConfigUtility {

    private BusinessConfigurationPort businessConfigAdapter;

    public BusinessConfigUtility(BusinessConfigurationPort businessConfigAdapter) {
        this.businessConfigAdapter = businessConfigAdapter;
    }

    /**
     * Associate the tenant specific language, of type {@link Locale}, with the current thread.
     */
    public void setContextLanguage() {
        Locale tenantSpecificLanguage = businessConfigAdapter.getTenantSpecificLanguage();
        LocaleContextHolder.setLocale(tenantSpecificLanguage);
        log.info("Tenant specific language: " + LocaleContextHolder.getLocale().getLanguage());
    }
}
