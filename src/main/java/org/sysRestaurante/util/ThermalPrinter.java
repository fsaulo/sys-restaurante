package org.sysRestaurante.util;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import java.io.*;

public class ThermalPrinter {

    private final String printerName;

    public ThermalPrinter(String printerName) {
        this.printerName = printerName;
    }

    public void print(byte[] data) throws PrintException {
        PrintService service = findPrinter();
        if (service == null) {
            throw new PrintException("Impressora não encontrada: " + printerName);
        }

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(data, flavor, null);

        PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
        attrs.add(new Copies(1));

        DocPrintJob job = service.createPrintJob();
        job.print(doc, attrs);
    }

    private PrintService findPrinter() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService service : services) {
            if (service.getName().toLowerCase().contains(printerName.toLowerCase())) {
                return service;
            }
        }
        return null;
    }
}
