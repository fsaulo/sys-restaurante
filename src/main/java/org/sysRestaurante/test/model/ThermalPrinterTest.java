import org.junit.jupiter.api.Test;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.applet.AppSettings;
import org.sysRestaurante.dao.*;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Receipt;
import org.sysRestaurante.util.ThermalPrinter;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ThermalPrinterTest {

    @Test
    void shouldPrintNonFiscalReceipt() {
        try {
            ThermalPrinter printer = new ThermalPrinter("POS-80C_COZINHA");
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (PrintService ps : services) {
                System.out.println(ps.getName());
            }
            Receipt receiptObj = new Receipt();
            ComandaDao orderDao = new ComandaDao();

            orderDao.setIdOrder(344);
            orderDao.setTotal(100);
            orderDao.setDiscount(0);
            orderDao.setTaxes(0);
            orderDao.setOrderDate(LocalDate.now());
            orderDao.setOrderTime(LocalTime.now());
            orderDao.setOrderDateTime(LocalDateTime.now());
            orderDao.setIdTable(2);

            ArrayList<ProductDao> products = new ArrayList<>();

            ProductDao product1 = new ProductDao();
            product1.setDescription("Produto1");
            product1.setQuantity(1);
            product1.setIdProduct(12);
            product1.setSellPrice(40);

            ProductDao product2 = new ProductDao();
            product2.setDescription("Produto2");
            product2.setIdProduct(13);
            product2.setQuantity(2);
            product2.setSellPrice(30);

            products.add(product1);
            products.add(product2);

            byte[] receipt = receiptObj.buildReceiptForPrint(orderDao, products);

            printer.print(receipt);

            assertTrue(true);

        } catch (Exception e) {
            fail("Falha ao imprimir cupom: " + e.getMessage());
        }
    }

    @Test
    void shouldPrintKitchenTicket() {
        try {
            ThermalPrinter printer = new ThermalPrinter("POS-80C_COZINHA");

            ProductDao product = new ProductDao();
            product.setQuantity(1);
            product.setDescription("Sopa de camarão");
            product.setCategoryDao(new ProductDao.CategoryDao(ProductDao.CategoryDao.Type.TASTE));

            Receipt receiptObj = new Receipt();
            KitchenOrderDao ticket = new KitchenOrderDao();
            ticket.setIdKitchenOrder(1);
            ticket.setKitchenOrderDetails("Sem batata");
            ticket.setIdComanda(99);
            ticket.setIdTable(12);
            ticket.setIdEmployee(1);
            ticket.setIdOrder(345);


            byte[] ticketBuilder = receiptObj.buildKitchenTicketForPrint(
                    ticket,
                    product
            );

            printer.print(ticketBuilder);
            assertTrue(true);

        } catch (Exception e) {
            fail("Falha ao imprimir cupom: " + e.getMessage());
        }
    }

    @Test
    void shouldPrintSangriaReceipt() {
        CashierDao cashier = new CashierDao();
        cashier.setByCard(100);
        cashier.setInCash(999);
        cashier.setIdCashier(1);
        cashier.setDateOpening(LocalDate.now());
        cashier.setTimeOpening(LocalTime.now());
        cashier.setDateClosing(LocalDate.now());
        cashier.setTimeClosing(LocalTime.now());
        cashier.setRevenue(309);
        cashier.setInitialAmount(10);
        cashier.setWithdrawal(0);
        cashier.setIdUser(1);

        ComandaDao comanda = new ComandaDao();
        comanda.setTotal(999);
        comanda.setDateClosing(LocalDate.now());
        comanda.setTimeClosing(LocalTime.now());
        comanda.setIdEmployee(1);
        comanda.setCustomerName("Cliente1");
        comanda.setIdTable(13);
        comanda.setIdComanda(4);
        comanda.setIdOrder(3);

        ArrayList<ComandaDao> comandas = new ArrayList<>();
        comandas.add(comanda);

        UserDao user = new UserDao();
        user.setAdmin(true);
        user.setIdUser(1);
        user.setUsername("admin");

        OrderDao order = new OrderDao();
        order.setTotal(455);
        order.setOrderDate(LocalDate.now());
        order.setOrderTime(LocalTime.now());
        order.setIdOrder(9);

        ArrayList<OrderDao> orders = new ArrayList<>();
        orders.add(order);

        try {
            Receipt receipt = new Receipt();
            ThermalPrinter printer = AppSettings.getInstance().getPOSPrinter();

            byte[] ticketBuilder = receipt.buildSangriaForPrint(cashier, user, orders, comandas);
            printer.print(ticketBuilder);
        } catch (Exception e) {
            fail("Falha ao imprimir cupom: " + e.getMessage());
        }
    }
}