package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Cashier;

public class CloseCashierDialogController {

    @FXML
    private Button continueButton;
    @FXML
    private Button cancelButton;
    @FXML
    private VBox vboxWrapper;

    public void initialize() {
        Cashier cashier = new Cashier();
        continueButton.setOnMouseClicked(event ->  {
            cashier.close(AppFactory.getCashierDao().getIdCashier(), 0);
            ((Node) event.getSource()).getScene().getWindow().hide();
        });
        cancelButton.setOnMouseClicked(event -> ((Node) event.getSource()).getScene().getWindow().hide());
        Platform.runLater(() -> vboxWrapper.requestFocus());
    }
}
