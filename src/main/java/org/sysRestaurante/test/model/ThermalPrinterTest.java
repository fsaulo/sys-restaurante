package org.sysRestaurante.test.model;


import org.junit.jupiter.api.Test;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Receipt;
import org.sysRestaurante.util.ThermalPrinter;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

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
}