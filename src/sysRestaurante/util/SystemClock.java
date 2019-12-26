package sysRestaurante.util;

import javafx.scene.control.Label;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SystemClock {

    private static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public SystemClock (Label timeLabel) {
        updateTime(timeLabel);
    }

    public static void updateTime(Label timeLabel) {
        Thread clock = new Thread() {
            public int hour;
            public int minute;
            public int second;

            public void run() {
                while (true) {
                    DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    Calendar cal = Calendar.getInstance();

                    second = cal.get(Calendar.SECOND);
                    minute = cal.get(Calendar.MINUTE);
                    hour = cal.get(Calendar.HOUR);
                    timeLabel.setText(hour + ":" + (minute) + ":" + second);

                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };
        clock.start();
    }
}
