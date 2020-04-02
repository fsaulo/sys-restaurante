package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
    @FXML
    private Label messageLabel;
    @FXML
    private Label byCashLabel;
    @FXML
    private Label initialAmount;
    @FXML
    private Label comandasCountLabel;
    @FXML
    private ListView tableListView;

    private boolean customerPresent = false;
    private List<ComandaDao> comandas;
    CashierDao cashierDao;
    private ObservableList<TableDao> tables = FXCollections.observableArrayList(Order.getBusyTables());
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(
            CloseCashierDialogController.class.getName());

    public void initialize() {
        Platform.runLater(() -> vboxWrapper.requestFocus());

        cashierDao = AppFactory.getCashierDao();
        comandas = Order.getComandasByIdCashier(AppFactory.getCashierDao().getIdCashier());
        continueButton.setOnMouseClicked(event -> closeCashier());
        comandasCountLabel.setText(String.valueOf(getClosedComandasCount()));

        updateDetails();
        checkBusyTable();

        cancelButton.setMnemonicParsing(true);
        cancelButton.setOnMouseClicked(event -> ((Node) event.getSource()).getScene().getWindow().hide());

        if (!isCloseble()) {
            messageLabel.setText("Há comanda abertas, finalize-as para continuar");
            tableListView.setCellFactory(tlv -> new TableListViewCell());
            tableListView.setDisable(false);
            tableListView.setVisible(true);
            tableListView.setItems(tables);
            tableListView.setMinHeight(200);
        } else {
            messageLabel.setText("");
            tableListView.setDisable(true);
            tableListView.setVisible(false);
            tableListView.setPrefHeight(0);
        }
    }

    public void closeCashier() {
        LOGGER.info("Trying to close cashier...");

        if (isCloseble()) {
            Cashier.close(cashierDao.getIdCashier());
            vboxWrapper.getScene().getWindow().hide();
            AppFactory.setCashierDao(null);
            AppFactory.getCashierController().updateCashierElements();
            LOGGER.info("Cashier was closed with no problems.");
        } else {
            LOGGER.warning("Cashier can't be closed due to orders that hasn't been closed.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Não é possível encerrar o caixa pois existem comandas abertas!");
            alert.setContentText("Para fechar o caixa, finalize as comandas abertas");
            alert.initOwner(vboxWrapper.getScene().getWindow());
            alert.showAndWait();
        }
    }

    public void updateDetails() {
        cashierDao = AppFactory.getCashierDao();
        NumberFormat format = CurrencyField.getBRLCurrencyFormat();
        LocalDateTime dateTimeOpenned = cashierDao.getDateOpening().atTime(cashierDao.getTimeOpening());
        dateOpennedLabel.setText(DateFormatter.TIME_DETAILS_FORMAT.format(dateTimeOpenned));
        withdrawalsLabel.setText(format.format(cashierDao.getWithdrawal()));
        revenueLabel.setText(format.format(cashierDao.getRevenue()));
        initialAmount.setText(format.format(cashierDao.getInitialAmount()));
        byCashLabel.setText(format.format(cashierDao.getInCash() + cashierDao.getInitialAmount()));

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

    public boolean isCloseble() {
        return !customerPresent;
    }

    public void checkBusyTable() {
        for (TableDao table : tables) {
            table.setStatus(3);
            table.setIdTable(table.getIdTable());
        }

        for (ComandaDao item : comandas) {
            if (item.getIdCategory() != 6) {
                customerPresent = true;
                break;
            }
            customerPresent = false;
        }
    }

    public int getClosedComandasCount() {
        int total = 0;
        for (ComandaDao comanda : comandas) {
            if (!comanda.isOpen()) {
                total++;
            }
        }
        return total;
    }
}
