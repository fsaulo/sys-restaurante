package org.sysRestaurante.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class BRLFormat {

    private static final Locale PT_BR = new Locale("pt", "BR");

    private static final NumberFormat VALUE_FORMAT;

    static {
        VALUE_FORMAT = NumberFormat.getNumberInstance(PT_BR);
        VALUE_FORMAT.setMinimumFractionDigits(2);
        VALUE_FORMAT.setMaximumFractionDigits(2);
        VALUE_FORMAT.setGroupingUsed(false);
    }

    private BRLFormat() {
        // util class
    }

    public static String value(double amount) {
        return VALUE_FORMAT.format(amount);
    }
}