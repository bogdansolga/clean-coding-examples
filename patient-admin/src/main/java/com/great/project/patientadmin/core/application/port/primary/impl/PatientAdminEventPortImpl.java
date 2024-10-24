package com.great.project.patientadmin.core.application.port.primary.impl;

import com.great.project.common.events.domain.DomainEvent;
import com.great.project.patientadmin.core.application.eventhandler.EventDispatcher;
import com.great.project.patientadmin.core.application.port.primary.PatientAdminEventPort;

/**
 * This class acts as dispatcher. Based on Domain Event code it delegates to a specific event handler. Dispatching is
 * done through {@link EventDispatcher}, in order to find all event handler implementations within the same core module.
 */
public class PatientAdminEventPortImpl implements PatientAdminEventPort {

    private EventDispatcher eventDispatcher;

    public PatientAdminEventPortImpl(EventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Method to handle all events coming from external messages. Based on the event code, it delegates to the
     * appropriate EventHandler implementation.
     * 
     * @param event
     *            the DomainEvent object that contains event code and payload.
     */
    @Override
    public void handleEvent(DomainEvent<?> event) {
        eventDispatcher.dispatch(event);
    }
}