package org.sysRestaurante.test.model;

import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.model.Order;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {

    @Test
    public void shouldUpdateKitchenOrderStatus() {
        KitchenOrderDao.KitchenOrderStatus status = KitchenOrderDao.KitchenOrderStatus.WAITING;
        int resultId = Order.newKitchenOrder(1, status.getValue(), "");

        assertTrue(resultId > 0);

        KitchenOrderDao.KitchenOrderStatus newStatus = KitchenOrderDao.KitchenOrderStatus.DELIVERED;
        boolean success = Order.updateKitchenOrderStatus(resultId, newStatus.getValue());
        KitchenOrderDao order = Order.getKitchenOrder(resultId);

        assertTrue(success);
        assertNotNull(order);
        assertEquals(newStatus, order.getKitchenOrderStatus());
    }

    @Test
    public void shouldUpdateKitchenOrderStatusWithDateTime() {
        KitchenOrderDao.KitchenOrderStatus status = KitchenOrderDao.KitchenOrderStatus.WAITING;
        int resultId = Order.newKitchenOrder(1, status.getValue(), "");

        assertTrue(resultId > 0);

        KitchenOrderDao.KitchenOrderStatus newStatus = KitchenOrderDao.KitchenOrderStatus.CANCELLED;
        LocalDateTime now = LocalDateTime.now();
        boolean success = Order.updateKitchenOrderStatusWithDateTime(resultId, newStatus.getValue(), now);
        KitchenOrderDao order = Order.getKitchenOrder(resultId);

        assertTrue(success);
        assertNotNull(order);
        assertEquals(newStatus, order.getKitchenOrderStatus());
        assertEquals(order.getFinalKitchenOrderDateTime(), now);
    }
}
