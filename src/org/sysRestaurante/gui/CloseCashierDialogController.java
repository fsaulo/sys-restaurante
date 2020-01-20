package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.LoggerHandler;

import java.util.logging.Logger;

public class CloseCashierDialogController {

    @FXML
    private Button continueButton;
    @FXML
    private Button cancelButton;
    @FXML
    private VBox vboxWrapper;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(
            CloseCashierDialogController.class.getName());

    public void initialize() {
        Cashier cashier = new Cashier();
        CashierDao cashierDao = AppFactory.getCashierDao();
        continueButton.setOnMouseClicked(event ->  {
            LOGGER.info("Trying to close cashier...");
            cashier.close(cashierDao.getIdCashier(), cashierDao.getRevenue());
            ((Node) event.getSource()).getScene().getWindow().hide();
            AppFactory.setCashierDao(null);
        });

        cancelButton.setMnemonicParsing(true);
        cancelButton.setOnMouseClicked(event -> ((Node) event.getSource()).getScene().getWindow().hide());
        Platform.runLater(() -> vboxWrapper.requestFocus());
    }
}
