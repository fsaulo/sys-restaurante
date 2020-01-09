package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Annotation;
import org.sysRestaurante.util.ExceptionHandler;

import java.sql.SQLException;

public class ClearNotesController {
    @FXML
    private Button yesButton;
    @FXML
    private Button noButton;

    public void initialize() {
        yesButton.setOnMouseClicked(e -> {
            try {
                new Annotation().removeAll();
                AppFactory.getDashbordController().removeNotesFromList();
                AppFactory.getDashbordController().reloadNotes();
                ((Node) e.getSource()).getScene().getWindow().hide();
            } catch (SQLException ex) {
                ExceptionHandler.incrementGlobalExceptionsCount();
                ex.printStackTrace();
            }
        });

        noButton.setOnMouseClicked(e -> {
            AppFactory.getDashbordController().removeNotesFromList();
            AppFactory.getDashbordController().reloadNotes();
            ((Node) e.getSource()).getScene().getWindow().hide();
        });
    }
}
