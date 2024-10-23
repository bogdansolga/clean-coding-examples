package com.great.project.patientadmin.core.application.port.primary;

import com.cerner.augero.common.events.domain.DomainEvent;

/**
 * Primary port for handling incoming events
 * 
 * @author Gabriela Maciac, Catalin Matache
 */
public interface PatientAdminEventPort {

    /**
     * Method to handle all events coming from external messages. It receives event data and handles the event based on
     * its event code.
     * 
     * @param event
     *            - the event to be handled based on its event code.
     */
    void handleEvent(DomainEvent<?> event);

}
