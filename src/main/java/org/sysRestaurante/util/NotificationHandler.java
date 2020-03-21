package org.sysRestaurante.util;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.sysRestaurante.applet.AppFactory;

public class NotificationHandler {

    public static void showInfo(String message) {
        Notifications notification = Notifications.create()
                .graphic(null)
                .title("Informações do Sistema")
                .position(Pos.TOP_RIGHT)
                .text(message)
                .owner(AppFactory.getMainController().getScene().getWindow())
                .hideAfter(Duration.seconds(3));
        notification.showInformation();
    }
}
