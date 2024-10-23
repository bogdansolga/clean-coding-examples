package com.great.project.patientadmin.core.application.port.secondary;

import com.cerner.augero.common.events.domain.AugeroDomainEvent;

/**
 * Secondary port that specifies operations regarding events.
 *
 * @author Rares Nistor
 */
public interface EventPublisherPort {

    /**
     * Publishes the given event.
     *
     * @param event the event to be published.
     */
    void publishEvent(AugeroDomainEvent<?> event);
}
