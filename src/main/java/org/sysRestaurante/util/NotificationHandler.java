package org.sysRestaurante.util;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.sysRestaurante.applet.AppFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NotificationHandler {

    public static void showInfo(String message) {
        Notifications notification = Notifications.create()
                .graphic(null)
                .title("Informações do Sistema")
                .position(Pos.TOP_RIGHT)
                .text(message)
                .hideAfter(Duration.seconds(5));

        Window window = AppFactory.getMainController().getScene().getWindow();
        if (!((Stage) window).isFullScreen()) {
            notification.owner(window);
        }

        notification.showInformation();
    }

    public static void errorDialog(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro do sistema");
        alert.setHeaderText("Eita! Ocorreu um erro gravíssimo!");
        alert.setContentText("Os dados da última operação foram perdidos, por favor, feche o sistema e abra novamente. "
        + "Lamentamos o ocorrido. Se possível, encaminhe os logs de erro para o desenvolvedor.");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        Label label = new Label("Segue em anexo os logs de erros: ");
        TextArea textArea = new TextArea(exceptionText);

        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();

        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
}
