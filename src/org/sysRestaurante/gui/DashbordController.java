package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.sysRestaurante.util.LoggerHandler;

import java.util.logging.Logger;

public class DashbordController {
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DashbordController.class.getName());
    private static long timerInMillies;

    @FXML
    private BorderPane borderPane;

    private Label sessionTimer = new Label();

    public void initialize() {
        HBox footer = new AppController().getFooter();
        borderPane.setBottom(footer);
    }




    public static long getElapsedSessionTime() {
        return timerInMillies;
    }
}
