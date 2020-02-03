package org.sysRestaurante.gui.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public interface DateFormatter {
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateFormat CLOCK_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    DateTimeFormatter TIME_DETAILS_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
}
