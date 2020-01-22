package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.CurrencyCellFormatter;
import org.sysRestaurante.util.CurrencyField;
import org.sysRestaurante.util.DateCellFormatter;
import org.sysRestaurante.util.DateFormatter;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.StatusCellFormatter;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.logging.Logger;

public class CashierController {

    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private Label openOrCloseCashierLabel;
    @FXML
    private VBox searchOrderBox;
    @FXML
    private VBox cancelOrderBox;
    @FXML
    private VBox newOrderBox;
    @FXML
    private VBox statusCashierBox;
    @FXML
    private Label statusCashierLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private VBox cashierDateDetailsBox;
    @FXML
    private Label inCashLabel;
    @FXML
    private Label byCardLabel;
    @FXML
    private Label withdrawalLabel;
    @FXML
    private TableColumn<OrderDao, Integer> codOrder;
    @FXML
    private TableColumn<OrderDao, String> details;
    @FXML
    private TableColumn<OrderDao, String> status;
    @FXML
    private TableColumn<OrderDao, LocalDate> date;
    @FXML
    private TableColumn<OrderDao, Double> total;
    @FXML
    private TableView<OrderDao> orderListTableView;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(CashierController.class.getName());

    @FXML
    public void initialize() {
        AppFactory.setCashierController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());

        updateOrderTableList();
        updateCashierElements();
        handleKeyEvent();
        LOGGER.info("At cashier page");
    }

    @FXML
    public void onOpenOrCloseCashier() {
        boolean isCashierOpenned = Cashier.getLastCashierStatus();

        if (isCashierOpenned) {
            AppController.showDialog(SceneNavigator.CLOSE_CASHIER_DIALOG);
            updateCashierElements();
            if (!Cashier.getLastCashierStatus()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informação do sistema");
                alert.setHeaderText("Caixa fechado com sucesso!");
                alert.setContentText("Para realizar novas operações de caixa, será necessário abri-lo novamente.");
                alert.showAndWait();
            }
        } else {
            AppController.showDialog(SceneNavigator.OPEN_CASHIER_DIALOG);
            updateCashierElements();
            if (Cashier.getLastCashierStatus()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informação do sistema");
                alert.setHeaderText(null);
                alert.setContentText("Caixa aberto com sucesso!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void onNewOrder() {
        newOrderBox.setDisable(true);
        AppFactory.getAppController().openPOS();
        newOrderBox.setDisable(false);
    }

    public void handleKeyEvent() {
        AppFactory.getMainController().getScene().getAccelerators().clear();
        AppFactory.getMainController().getScene().getAccelerators()
                .put(SceneNavigator.F10_OPEN_OR_CLOSE_CASHIER, this::onOpenOrCloseCashier);
    }

    public void updateCashierElements() {
        updateCashierStatus();
        updateOrderTableList();
    }

    public void updateCashierStatus() {
        boolean isCashierOpenned = Cashier.getLastCashierStatus();
        CashierDao cashierDao;

        if (isCashierOpenned) {
            cashierDao = new Cashier().getCashierDataAccessObject(AppFactory.getCashierDao().getIdCashier());
            setDisableCashierOptions(false);
            openOrCloseCashierLabel.setText("Fechar caixa");
            statusCashierLabel.setText("CAIXA LIVRE");
            statusCashierBox.setStyle("-fx-background-color: #58996A; -fx-background-radius: 5");
            statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
            statusCashierBox.getChildren().add(statusCashierLabel);
            changeCashierDetails(true);
        } else {
            cashierDao = new CashierDao();
            setDisableCashierOptions(true);
            openOrCloseCashierLabel.setText("Abrir caixa");
            Label statusMessage = new Label("Use o atalho F10 para abrir o caixa");
            statusCashierLabel.setText("CAIXA FECHADO");
            statusCashierBox.setStyle("-fx-background-color: #bababa; -fx-background-radius: 5");
            statusCashierBox.getChildren().add(statusMessage);
            statusMessage.setStyle("-fx-font-family: carlito; " +
                    "-fx-font-size: 15; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-style: italic");
            statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
            statusCashierBox.getChildren().addAll(statusCashierLabel, statusMessage);
            changeCashierDetails(false);
        }

        NumberFormat brlCurrencyFormat = CurrencyField.getBRLCurrencyFormat();
        revenueLabel.setText(brlCurrencyFormat.format(cashierDao.getRevenue()));
        inCashLabel.setText(brlCurrencyFormat.format(cashierDao.getInCash()));
        byCardLabel.setText(brlCurrencyFormat.format(cashierDao.getByCard()));
        withdrawalLabel.setText(brlCurrencyFormat.format(cashierDao.getWithdrawal()));
    }

    public void updateOrderTableList() {
        orderListTableView.setItems(new Cashier().getOrderByIdCashier(AppFactory.getCashierDao().getIdCashier()));
        codOrder.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        total.setCellValueFactory(new PropertyValueFactory<>("inCash"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setCellFactory(tc -> new StatusCellFormatter());
        date.setCellFactory(tc -> new DateCellFormatter());
        orderListTableView.refresh();
    }

    public void setDisableCashierOptions(boolean status) {
        searchOrderBox.setDisable(status);
        newOrderBox.setDisable(status);
        cancelOrderBox.setDisable(status);
    }

    public void changeCashierDetails(boolean isCashierOpenned) {
        cashierDateDetailsBox.getChildren().removeAll(cashierDateDetailsBox.getChildren());

        if (isCashierOpenned) {
            Label message = new Label("Caixa aberto em");
            Label date = new Label();
            message.setStyle("-fx-font-family: carlito; -fx-font-size: 15; -fx-font-weight: bold");
            date.setStyle("-fx-font-family: carlito; -fx-font-size: 15; -fx-font-weight: bold");
            date.setText(DateFormatter
                    .TIME_DETAILS_FORMAT
                    .format(Cashier.getCashierDateTimeDetailsById(AppFactory.getCashierDao().getIdCashier())));
            cashierDateDetailsBox.getChildren().addAll(message, date);
        } else {
            Label message = new Label("Caixa está fechado");
            message.setStyle("-fx-font-family: carlito; -fx-font-size: 15; -fx-font-weight: bold");
            cashierDateDetailsBox.getChildren().add(message);
        }
    }
}
