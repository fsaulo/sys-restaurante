package org.sysRestaurante.gui.formatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public interface DateFormatter {
    DateFormat CLOCK_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
    DateTimeFormatter TIME_DETAILS_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd-MM HH:mm:ss");
    DateTimeFormatter DETAILED_TIME = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss");

    /**
     * Translate given minutes to human readable format.
     * Returns a formated string.
     */

    static String translateTimeFromMinutes(long minutes) {
        String humanReadableDateTime;

        // Less than an hour
        if (minutes < 60) {
            humanReadableDateTime = minutes + " minutos";
        }

        // More than an hour & less then a day
        else if (minutes <= 1440) {
            humanReadableDateTime = Duration.ofMinutes(minutes).toHours() + " horas";
        }

        // More than a day
        else {
            humanReadableDateTime = Duration.ofMinutes(minutes).toHours() + " dias";
        }

        return humanReadableDateTime;
    }
}
