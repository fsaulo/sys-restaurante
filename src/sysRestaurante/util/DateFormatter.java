package sysRestaurante.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public static final java.text.DateFormat CLOCK_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
}
