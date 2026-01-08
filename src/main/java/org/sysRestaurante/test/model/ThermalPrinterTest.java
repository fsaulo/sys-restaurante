package org.sysRestaurante.test.model;


import org.junit.jupiter.api.Test;
import org.sysRestaurante.util.ReceiptBuilder;
import org.sysRestaurante.util.ThermalPrinter;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import static org.junit.jupiter.api.Assertions.*;

class ThermalPrinterTest {

    @Test
    void shouldPrintNonFiscalReceipt() {
        try {
            ThermalPrinter printer = new ThermalPrinter("pos");
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            for (PrintService ps : services) {
                System.out.println(ps.getName());
            }
            byte[] receipt = ReceiptBuilder.buildSampleReceipt();

            printer.print(receipt);

            assertTrue(true);

        } catch (Exception e) {
            fail("Falha ao imprimir cupom: " + e.getMessage());
        }
    }
}