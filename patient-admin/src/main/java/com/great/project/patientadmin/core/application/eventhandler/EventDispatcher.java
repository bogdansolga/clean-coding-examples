package com.great.project.patientadmin.core.application.eventhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.great.project.common.events.domain.DomainEvent;

/**
 * Class for serving generic events.
 */
public class EventDispatcher {

    private final Map<String, List<EventHandler<DomainEvent<?>>>> eventHandlerMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void setHandlers(List<EventHandler<? extends DomainEvent<?>>> handlers) {
        for (EventHandler<?> handler : handlers) {
            if (eventHandlerMap.containsKey(handler.eventCodeSupported())) {
                eventHandlerMap.get(handler.eventCodeSupported()).add((EventHandler<DomainEvent<?>>) handler);
            } else {
                List<EventHandler<DomainEvent<?>>> handlersList = new ArrayList<>(
                        Collections.singletonList((EventHandler<DomainEvent<?>>) handler));
                eventHandlerMap.put(handler.eventCodeSupported(), handlersList);
            }
        }
    }

    public void dispatch(DomainEvent<?> event) {
        eventHandlerMap.get(event.getEvent().getCode()).forEach(handler -> handler.processEvent(event));
    }
}
