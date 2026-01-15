package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class OrderDetailsController {

    @FXML
    private Label tableLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label customerLabel;
    @FXML
    private Label codComandaLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label codOrderLabel;
    @FXML
    private ComboBox<String> employeeComboBox;
    @FXML
    private Button cancelOrderButton;
    @FXML
    private TableView<ProductDao> selectedProductsTableView;
    @FXML
    private TableColumn<ProductDao, String> descriptionColumn;
    @FXML
    private TableColumn<ProductDao, Integer> qtdColumn;
    @FXML
    private TableColumn<ProductDao, Double> priceColumn;
    @FXML
    private TableColumn<ProductDao, Double> totalColumn;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label employeeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Button exitButton;
    @FXML
    private Button receiptButton;

    private final OrderDao order = AppFactory.getOrderDao();

    public void initialize() {
        List<ProductDao> products = Order.getItemsByOrderId(order.getIdOrder());
        if (products != null) {
            AppFactory.setSelectedProducts(new ArrayList<>(products));
        }

        ObservableList<ProductDao> items = FXCollections
                .observableArrayList(products);
        updateTables();

        selectedProductsTableView.setItems(items);
        codOrderLabel.setText(String.valueOf(order.getIdOrder()));
        totalLabel.setText(CurrencyField.getBRLCurrencyFormat().format(order.getTotal()));
        subtotalLabel.setText(CurrencyField.getBRLCurrencyFormat().format(order.getTotal()));
        customerLabel.setText("Sem registro");
        employeeLabel.setText("Sem registro");
        timeLabel.setText("Sem registro");
        exitButton.setOnAction(actionEvent -> exit());
        codComandaLabel.setText("CAIXA #" + AppFactory.getCashierDao().getIdCashier());
        dateLabel.setText(DateFormatter.DATE_TIME_FORMAT.format(order.getOrderDate().atTime(order.getOrderTime())));
        cancelOrderButton.setOnAction(actionEvent -> onCancelOrder());

        receiptButton.setOnMouseClicked(event -> {
            try {
                setOrderDetails();
                AppController.printPOSReceipt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        setOrderDetails();
        setCanceledLabel();
    }

    private void setOrderDetails() {
        if (order.getDetails().equals("Pedido em comanda")) {
            ComandaDao comanda = Order.getComandaByOrderId(order.getIdOrder());
            String employee = Personnel.getEmployeeNameById(Objects.requireNonNull(comanda).getIdEmployee());
            String customer = Order.getCustomerName(order.getIdOrder());
            AppFactory.setOrderDao(comanda);
            AppFactory.setComandaDao(comanda);
            tableLabel.setText("MESA #" + comanda.getIdTable());
            codComandaLabel.setText(String.valueOf(comanda.getIdComanda()));
            employeeLabel.setText(employee);
            employeeComboBox.getItems().add("Atendente");

            if (!Objects.requireNonNull(employee).equals("Nenhum")) {
                employeeComboBox.getItems().add(employee);
                employeeComboBox.getSelectionModel().select(employee);
            } else {
                employeeComboBox.getSelectionModel().selectFirst();
                employeeLabel.setText("Sem registro");
            }

            if (comanda.getDateClosing() != null) {
                calculateTimePeriod(comanda);
            }

            if (customer != null) {
                customerLabel.setText(customer);
            }
        } else {
            ComandaDao comanda = new ComandaDao();
            comanda.setIdOrder(order.getIdOrder());
            comanda.setTotal(order.getTotal());
            comanda.setTaxes(order.getTaxes());
            comanda.setDiscount(order.getDiscount());
            comanda.setIdCashier(order.getIdCategory());
            comanda.setOrderDateTime(order.getOrderDateTime());
            AppFactory.setOrderDao(order);
            AppFactory.setComandaDao(comanda);
        }
    }

    private void setCanceledLabel() {
        if (order.getStatus().equals("Cancelado")) {
            cancelOrderButton.setText("CANCELADO");
            cancelOrderButton.setTextFill(Color.DARKRED);
            cancelOrderButton.setDisable(true);
        }
    }

    public void updateTables() {
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        qtdColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("sellPrice"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        priceColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        totalColumn.setCellFactory((CellFormatter<ProductDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
    }

    public void calculateTimePeriod(ComandaDao comanda) {
        LocalDateTime dateTimeOpenned = comanda.getDateOpening().atTime(comanda.getTimeOpening());
        LocalDateTime dateTimeClosed = comanda.getDateClosing().atTime(comanda.getTimeClosing());
        if (ChronoUnit.DAYS.between(dateTimeOpenned, dateTimeClosed) > 1) {
            timeLabel.setText(ChronoUnit.DAYS.between(dateTimeOpenned, dateTimeClosed) + " dias");
        } else if (ChronoUnit.HOURS.between(dateTimeOpenned, dateTimeClosed) > 1) {
            timeLabel.setText(ChronoUnit.HOURS.between(dateTimeOpenned, dateTimeClosed) + " horas");
        } else if (ChronoUnit.MINUTES.between(dateTimeOpenned, dateTimeClosed) > 60) {
            timeLabel.setText("Mais de uma hora");
        } else {
            timeLabel.setText(ChronoUnit.MINUTES.between(dateTimeOpenned, dateTimeClosed) + " minutos");
        }
    }

    public void onCancelOrder() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar o pedido?");
        alert.setContentText("Essa operação não poderá ser desfeita.");
        alert.initOwner(cancelOrderButton.getScene().getWindow());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            final int CANCELED = 3;
            int idOrder = order.getIdOrder();
            int idCashier = AppFactory.getCashierDao().getIdCashier();
            double total = order.getTotal();

            if (order.getDetails().equals("Pedido em comanda")) {
                ComandaDao comanda = Order.getComandaByOrderId(order.getIdOrder());
                AppFactory.setComandaDao(comanda);
                int idComanda = Objects.requireNonNull(comanda).getIdComanda();
                int idTable = comanda.getIdTable();
                Order.closeComanda(idComanda, total);
                Order.updateOrderStatus(idComanda, CANCELED);
                Order.updateOrderAmount(idComanda, total, 0, 0);
                Management.closeTable(idTable);
            }

            if (order.getStatus().equals("Concluído")) {
                double inCash = order.getInCash();
                double byCard = order.getByCard();
                Cashier.setRevenue(idCashier, -inCash, -byCard, 0);
            }

            Order.cancel(idOrder);
            order.setStatus(CANCELED);
            NotificationHandler.showInfo("Pedido cancelado com sucesso!");
            initialize();
        }
    }

    public PopOver viewReceipt() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.RECEIPT_VIEW));
        List<ProductDao> products = FXCollections.observableArrayList(Order.getItemsByOrderId(order.getIdOrder()));
        AppFactory.setSelectedProducts(new ArrayList<>(products));
        VBox node = null;

        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PopOver(node);
    }

    public void exit() {
        try {
            AppFactory.getCashierController().updateOrderTableList();
            AppFactory.getCashierController().updateCashierElements();
        } catch (Exception ex) {
            ExceptionHandler.doNothing();
        }

        totalLabel.getParent().getScene().getWindow().hide();
    }
}
