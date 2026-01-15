package org.sysRestaurante.applet;

import org.sysRestaurante.util.ThermalPrinter;

public class AppSettings {
    private static final AppSettings instance = new AppSettings();
    private final boolean isProduction;
    private String kitchenPrinterName = "POS-80C_COZINHA";
    private String posPrinterName = "POS-80C_COZINHA";
    private boolean shouldPrintPOS = true;
    private boolean shouldPrintKitchenTicket = true;

    public boolean isShouldPrintKitchenTicket() {
        return shouldPrintKitchenTicket;
    }
    public void setShouldPrintKitchenTicket(boolean shouldPrintKitchenTicket) {
        this.shouldPrintKitchenTicket = shouldPrintKitchenTicket;
    }


    public boolean isShouldPrintPOS() {
        return shouldPrintPOS;
    }

    public void setShouldPrintPOS(boolean shouldPrintPOS) {
        this.shouldPrintPOS = shouldPrintPOS;
    }

    private AppSettings() {
        this.isProduction = Boolean.parseBoolean(System.getProperty("sys.production", "true"));
    }

    public static AppSettings getInstance() {
        return instance;
    }

    public boolean isProduction() { return isProduction; }

    public ThermalPrinter getKitchenPrinter() { return new ThermalPrinter(kitchenPrinterName); }

    public ThermalPrinter getPOSPrinter() { return new ThermalPrinter(posPrinterName); }
}
