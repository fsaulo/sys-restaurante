package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Reminder;
import org.sysRestaurante.util.ExceptionHandler;

import java.sql.SQLException;

public class ClearNotesAlertController {
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;
    @FXML
    private VBox clearNoteDialog;

    public void initialize() {
        Platform.runLater(() -> clearNoteDialog.requestFocus());
        yesButton.setOnMouseClicked(e -> {
            try {
                new Reminder().removeChecked();
                AppFactory.getDashbordController().removeNotesFromList();
                AppFactory.getDashbordController().reloadNotes();
                ((Node) e.getSource()).getScene().getWindow().hide();
            } catch (SQLException ex) {
                ExceptionHandler.incrementGlobalExceptionsCount();
                ex.printStackTrace();
            }
        });

        noButton.setOnMouseClicked(e -> {
            ((Node) e.getSource()).getScene().getWindow().hide();
        });
    }
}
