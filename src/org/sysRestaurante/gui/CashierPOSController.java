package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CashierPOSController {

    @FXML
    public VBox cancelButton;
    @FXML
    private Label label;

    public void initialize() {
        Platform.runLater(() -> label.requestFocus());
        cancelButton.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Tem certeza que deseja cancelar venda?");
            alert.setContentText("Todos os registros salvos serão perdidos.");
            alert.showAndWait();

            if (alert.getResult() != ButtonType.CANCEL)
                ((Node) e.getSource()).getScene().getWindow().hide();
        });
    }
}
