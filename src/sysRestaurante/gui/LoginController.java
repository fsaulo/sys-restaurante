package sysRestaurante.gui;

import javafx.scene.paint.Color;
import sysRestaurante.model.Authentication;
import sysRestaurante.util.Encryption;
import sysRestaurante.util.LoggerHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(LoginController.class.getName());

    @FXML
    private Label dbStatusLabel;

    public void initialize() {
        Authentication certs = new Authentication();

        if (certs.isDatabaseConnected()) {
            dbStatusLabel.setTextFill(Color.web("Green"));
            dbStatusLabel.setText("Database connected");

        } else {
            dbStatusLabel.setTextFill(Color.web("Red"));
            dbStatusLabel.setText("Database disconnected");
        }
        LOGGER.info("Login pane initialized.");
    }
}





















