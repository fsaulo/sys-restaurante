package org.sysRestaurante.applet;

public class AppSettings {
    private static final AppSettings instance = new AppSettings();
    private final boolean isProduction;

    private AppSettings() {
        this.isProduction = Boolean.parseBoolean(System.getProperty("app.env.production", "false"));
    }

    public static AppSettings getInstance() {
        return instance;
    }

    public boolean isProduction() { return isProduction; }
}
