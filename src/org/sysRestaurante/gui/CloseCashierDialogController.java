package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
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
        CashierDao cashierDao = AppFactory.getCashierDao();
        continueButton.setOnMouseClicked(event ->  {
            cashier.close(cashierDao.getIdCashier(), cashierDao.getRevenue());
            ((Node) event.getSource()).getScene().getWindow().hide();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação do sistema");
            alert.setHeaderText("Caixa fechado com sucesso!");
            alert.setContentText("Para realizar novas operações de caixa, será necessário abri-lo novamente.");
            alert.showAndWait();

            AppFactory.setCashierDao(null);
        });

        cancelButton.setMnemonicParsing(true);
        cancelButton.setOnMouseClicked(event -> ((Node) event.getSource()).getScene().getWindow().hide());
        Platform.runLater(() -> vboxWrapper.requestFocus());
    }
}
