package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
        ObservableList items = FXCollections.observableArrayList(Order.getItemsByOrderId(order.getIdOrder()));
        updateTables();

        selectedProductsTableView.setItems(items);
        codOrderLabel.setText(String.valueOf(order.getIdOrder()));
        totalLabel.setText(CurrencyField.getBRLCurrencyFormat().format(order.getTotal()));
        subtotalLabel.setText(CurrencyField.getBRLCurrencyFormat().format(order.getTotal()));
        customerLabel.setText("Sem registro");
        employeeLabel.setText("Sem registro");
        timeLabel.setText("Sem registro");
        exitButton.setOnAction(actionEvent -> exitButton.getParent().getScene().getWindow().hide());
        codComandaLabel.setText("CAIXA #" + AppFactory.getCashierDao().getIdCashier());
        dateLabel.setText(DateFormatter.TIME_DETAILS_FORMAT.format(order.getOrderDate().atTime(order.getOrderTime())));

        if (order.getDetails().equals("Pedido em comanda")) {
            ComandaDao comanda = Order.getComandaByOrderId(order.getIdOrder());
            String employee = Personnel.getEmployeeNameById(comanda.getIdEmployee());
            String customer = Order.getCustomerName(order.getIdOrder());
            tableLabel.setText("MESA #" + comanda.getIdTable());
            codComandaLabel.setText(String.valueOf(comanda.getIdComanda()));
            employeeLabel.setText(employee);
            employeeComboBox.getItems().add("Atendente");

            if (!employee.equals("Nenhum")) {
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
        }

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
}
