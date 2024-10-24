package com.great.project.patientadmin.core.application.eventhandler;

import com.great.project.common.events.domain.DomainEvent;

/**
 * This is a marker interface to identify an event handler. It provides
 * supported event code and method to process event
 */
public interface EventHandler<E extends DomainEvent<?>> {

    /**
     * Method that has responsibility for the business event. It receives the Domain
     * Event and does basic validation and covers resource delegation or
     * transactions. It will make use of another application service providing the
     * business logic.
     * 
     * @param event the {@link DomainEvent} handled.
     */
    void processEvent(E event);

    /**
     * Method to return the event code supported by the {@link EventHandler}
     * implementation.
     *  
     * @return the according event code.
     */
    String eventCodeSupported();
}
