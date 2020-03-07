package org.sysRestaurante.util;

import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationHandler {

    public static void showInfo(String message) {
        Notifications notification = Notifications.create()
                .graphic(null)
                .title("Informações do Sistema")
                .text(message)
                .hideAfter(Duration.seconds(3));
        notification.showInformation();
    }
}
