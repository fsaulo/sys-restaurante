package org.sysRestaurante.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<EventType<?>, List<EventHandler<?>>> listenersMap = new HashMap<>();

    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        listenersMap.computeIfAbsent(eventType, k -> new ArrayList<>()).add(eventHandler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        List<EventHandler<?>> eventHandlers = listenersMap.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove(eventHandler);
        }
    }

    public <T extends Event> void fireEvent(T event) {
        List<EventHandler<?>> eventHandlers = listenersMap.get(event.getEventType());
        if (eventHandlers != null) {
            for (EventHandler<?> handler : eventHandlers) {
                //noinspection unchecked
                ((EventHandler<T>) handler).handle(event);
            }
        }
    }
}
