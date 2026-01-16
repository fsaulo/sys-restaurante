import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    public static int idCashier;
    public static OrderDao orderDao;
    public static ComandaDao comandaDao;

    @BeforeAll
    public static void shouldOpenCashierAndCreateOrder() {
        Cashier cashier = new Cashier();
        idCashier = cashier.open(1, 0, "");
        assertNotEquals(-1, idCashier);

        orderDao = Order.newOrder(1, idCashier, 0, 0, 0, 0, 0, "");
        assertNotNull(orderDao);

        comandaDao = Order.newComanda(1, orderDao.getIdOrder(), idCashier, 1, 1);
        assertNotNull(comandaDao);
    }


    @Test
    public void shouldUpdateKitchenOrderStatus() {
        KitchenOrderDao.KitchenOrderStatus status = KitchenOrderDao.KitchenOrderStatus.WAITING;
        int resultId = Order.newKitchenOrder(1, status.getValue(), "");

        assertTrue(resultId > 0);

        KitchenOrderDao.KitchenOrderStatus newStatus = KitchenOrderDao.KitchenOrderStatus.DELIVERED;
        boolean success = Order.updateKitchenOrderStatus(resultId, newStatus.getValue());
        KitchenOrderDao order = Order.getKitchenOrderById(resultId);

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
        KitchenOrderDao order = Order.getKitchenOrderById(resultId);

        assertTrue(success);
        assertNotNull(order);
        assertEquals(newStatus, order.getKitchenOrderStatus());
        assertEquals(order.getFinalKitchenOrderDateTime(), now);
    }

    @Test
    public void shouldGetKitchenOrderById() {
        int idComanda = 1;
        KitchenOrderDao.KitchenOrderStatus status = KitchenOrderDao.KitchenOrderStatus.WAITING;
        int resultId = Order.newKitchenOrder(idComanda, status.getValue(), "");
        assertTrue( resultId > 0);

        ArrayList<KitchenOrderDao> resultArrayList = Order.getKitchenTicketsByComandaId(idComanda);

        assertNotNull(resultArrayList);
        assertFalse(resultArrayList.isEmpty());
    }

    @Test
    public void shouldGetKitchenOrderByCashierId() {
        KitchenOrderDao.KitchenOrderStatus status = KitchenOrderDao.KitchenOrderStatus.WAITING;
        List<ComandaDao> comandaDaoList = Order.getComandasByIdCashier(idCashier);

        assertNotNull(comandaDaoList);
        assertFalse(comandaDaoList.isEmpty());

        int idComanda = comandaDaoList.get(0).getIdComanda();
        int resultId1 = Order.newKitchenOrder(idComanda, status.getValue(), "TESTE1");
        int resultId2 = Order.newKitchenOrder(idComanda, status.getValue(), "TESTE2");

        assertTrue(resultId1 > 0);
        assertTrue(resultId2 > 0);

        List<KitchenOrderDao> resultArrayList = Order.getKitchenTicketsByCashierId(idCashier);

        assertNotNull(resultArrayList);
        assertFalse(resultArrayList.isEmpty());
    }
}
