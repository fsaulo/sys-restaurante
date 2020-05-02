package org.sysRestaurante.gui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import javafx.util.Callback;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.gui.formatter.StatusCellFormatter;
import org.sysRestaurante.util.NotificationHandler;

import java.lang.management.MemoryUsage;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private TableColumn<OrderDao, String> notes;
    @FXML
    private TableColumn<OrderDao, LocalDate> date;
    @FXML
    private TableColumn<OrderDao, Double> total;
    @FXML
    private TableView<OrderDao> orderListTableView;

    @FXML
    public void initialize() {
        AppFactory.setCashierController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());

        updateOrderTableList();
        updateCashierElements();
        handleKeyEvent();

        orderListTableView.setRowFactory(orderDaoTableView -> {
            final TableRow<OrderDao> row = new TableRow<>();
            final ContextMenu contextMenu = new ContextMenu();

            SeparatorMenuItem separator = new SeparatorMenuItem();
            MenuItem optionDeleteOrder = new MenuItem("Cancelar pedido");
            MenuItem optionDetailsOrder = new MenuItem("Detalhes");
            MenuItem optionSeeReceipt = new MenuItem("Recibo");

            optionDetailsOrder.setOnAction(actionEvent -> {
                AppFactory.setOrderDao(row.getItem());
                AppController.showDialog(SceneNavigator.ORDER_DETAILS_DIALOG, true);
            });

            contextMenu.getItems().addAll(optionDetailsOrder, optionSeeReceipt, separator, optionDeleteOrder);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty().not())
            .then(contextMenu)
            .otherwise((ContextMenu) null));

            return row;
        });
    }

    @FXML
    public void onOpenOrCloseCashier() {
        boolean isCashierOpen = Cashier.isOpen();

        if (isCashierOpen) {
            AppController.showDialog(SceneNavigator.CLOSE_CASHIER_DIALOG, true);
            if (!Cashier.isOpen()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informações do sistema");
                alert.setHeaderText("Caixa fechado com sucesso!");
                alert.setContentText("Para realizar novas operações de caixa, será necessário abri-lo novamente.");
                alert.initOwner(borderPaneHolder.getScene().getWindow());
                alert.showAndWait();
            }
        } else {
            AppController.showDialog(SceneNavigator.OPEN_CASHIER_DIALOG, true);
            updateCashierElements();
            if (Cashier.isOpen()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informações do sistema");
                alert.setHeaderText(null);
                alert.setContentText("Caixa aberto com sucesso!");
                alert.initOwner(borderPaneHolder.getScene().getWindow());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void onNewOrder() {
        newOrderBox.setDisable(true);
        AppFactory.getAppController().showPOS();

        if (AppController.isSellConfirmed()) {
            AppController.setSellConfirmed(false);
            NotificationHandler.showInfo("Pedido realizado com sucesso");
        }

        newOrderBox.setDisable(false);
    }

    public void handleKeyEvent() {
        AppFactory.getMainController().getScene().getAccelerators().clear();
        AppFactory.getAppController().setFullScreenShortcut();
        AppFactory.getMainController().getScene().getAccelerators()
                .put(SceneNavigator.F10_OPEN_OR_CLOSE_CASHIER, this::onOpenOrCloseCashier);
        AppFactory.getMainController().getScene().getAccelerators()
                .put(SceneNavigator.F2_CONFIRMATION, this::onNewOrder);
    }

    public void updateCashierElements() {
        updateCashierStatus();
        updateOrderTableList();
    }

    public void updateCashierStatus() {
        AppFactory.setCashierController(this);
        boolean isCashierOpenned = Cashier.isOpen();
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
        orderListTableView.setItems(Order.getOrderByIdCashier(AppFactory.getCashierDao().getIdCashier()));
        codOrder.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        notes.setCellValueFactory(new PropertyValueFactory<>("note"));
        status.setCellFactory(tc -> new StatusCellFormatter());
        date.setCellFactory((CellFormatter<OrderDao, LocalDate>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy")
                .format(value));
        total.setCellFactory((CellFormatter<OrderDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        orderListTableView.refresh();
    }

    public void setDisableCashierOptions(boolean status) {
        searchOrderBox.setDisable(status);
        cancelOrderBox.setDisable(status);
        newOrderBox.setDisable(status);
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
