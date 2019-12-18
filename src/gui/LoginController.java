package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class LoginController {

    @FXML
    private Label textLabel;

    private boolean textShowed;

    public void initialize() {
        textLabel.setText("Click OK");
        setTextShowed(false);
    }

    public void onButtonClicked() {
        if (isTextShowed()) {
            textLabel.setText("Welcome!");
            setTextShowed(false);
        }
        else {
            textLabel.setText("Hello, world!");
            setTextShowed(true);
        }
    }

    public boolean isTextShowed() {
        return textShowed;
    }

    public void setTextShowed(boolean flag) {
        this.textShowed = flag;
    }
}
