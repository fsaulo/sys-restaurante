package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.CurrencyField;
import org.sysRestaurante.util.DateFormatter;
import org.sysRestaurante.util.LoggerHandler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class CloseCashierDialogController {

    @FXML
    private Button continueButton;
    @FXML
    private Button cancelButton;
    @FXML
    private VBox vboxWrapper;
    @FXML
    private Label withdrawalsLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private Label dateOpennedLabel;
    @FXML
    private Label durationLabel;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(
            CloseCashierDialogController.class.getName());

    public void initialize() {
        Cashier cashier = new Cashier();
        CashierDao cashierDao = AppFactory.getCashierDao();
        continueButton.setOnMouseClicked(event ->  {
            LOGGER.info("Trying to close cashier...");
            cashier.close(cashierDao.getIdCashier());
            ((Node) event.getSource()).getScene().getWindow().hide();
            AppFactory.setCashierDao(null);
            AppFactory.getCashierController().updateCashierElements();
        });

        updateDetails();
        cancelButton.setMnemonicParsing(true);
        cancelButton.setOnMouseClicked(event -> ((Node) event.getSource()).getScene().getWindow().hide());
        Platform.runLater(() -> vboxWrapper.requestFocus());
    }

    public void updateDetails() {
        CashierDao cashierDao = AppFactory.getCashierDao();
        LocalDateTime dateTimeOpenned = cashierDao.getDateOpening().atTime(cashierDao.getTimeOpening());
        dateOpennedLabel.setText(DateFormatter.TIME_DETAILS_FORMAT.format(dateTimeOpenned));
        withdrawalsLabel.setText(CurrencyField.getBRLCurrencyFormat().format(cashierDao.getWithdrawal()));
        revenueLabel.setText(CurrencyField.getBRLCurrencyFormat().format(cashierDao.getRevenue()));

        if (ChronoUnit.DAYS.between(dateTimeOpenned, LocalDateTime.now()) > 1) {
            durationLabel.setText(ChronoUnit.DAYS.between(dateTimeOpenned, LocalDateTime.now()) + " dias");
        } else if (ChronoUnit.HOURS.between(dateTimeOpenned, LocalDateTime.now()) > 1) {
            durationLabel.setText(ChronoUnit.HOURS.between(dateTimeOpenned, LocalDateTime.now()) + " horas");
        } else if (ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) > 60) {
            durationLabel.setText("Mais de uma hora");
        } else {
            durationLabel.setText(ChronoUnit.MINUTES.between(dateTimeOpenned, LocalDateTime.now()) + " minutos");
        }
    }
}
