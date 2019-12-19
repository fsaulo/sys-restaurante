package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import util.LoggerHandler;

import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(LoginController.class.getName());

    @FXML
    private Label textLabel;

    private boolean textShowed;

    public void initialize() {
        textLabel.setText("Click OK");
        setTextShowed(false);

        LOGGER.info("Login pane initialized.");
    }

    public void onButtonClicked() {
        if (isTextShowed()) {
            textLabel.setText("Oi, linda!");
            setTextShowed(false);
        }
        else {
            textLabel.setText("Hello, world!");
            setTextShowed(true);
        }

        LOGGER.info("Button was clicked");
    }

    public boolean isTextShowed() {
        return textShowed;
    }

    public void setTextShowed(boolean flag) {
        this.textShowed = flag;
    }
}
