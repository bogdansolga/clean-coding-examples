package com.great.project.patientadmin.core.application.port.secondary;

import java.util.Locale;

/**
 * Secondary port for consuming the business configuration operation to determine the tenant specific language.
 * 
 * @author Gabriela Maciac
 */
public interface BusinessConfigurationPort {

    /**
     * Operation to determine the tenant specific language.
     *
     * @return the language in which the text will be translated
     */
    Locale getTenantSpecificLanguage();
}
