package org.sysRestaurante.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.sysRestaurante.dao.KitchenOrderDao;

public class TicketStatusChangedEvent extends Event {
    private static KitchenOrderDao.KitchenOrderStatus ticketStatus;
    public static final EventType<TicketStatusChangedEvent> TICKET_STATUS_CHANGED_EVENT_TYPE =
            new EventType<>(Event.ANY, "TICKET_STATUS_CHANGED");

    public TicketStatusChangedEvent() {
        super(TICKET_STATUS_CHANGED_EVENT_TYPE);
    }

    public TicketStatusChangedEvent(KitchenOrderDao.KitchenOrderStatus status) {
        super(TICKET_STATUS_CHANGED_EVENT_TYPE);
        ticketStatus = status;
    }

    public KitchenOrderDao.KitchenOrderStatus getTicketStatus() {
        return ticketStatus;
    }
}
