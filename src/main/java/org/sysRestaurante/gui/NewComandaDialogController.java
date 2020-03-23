package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;
import org.sysRestaurante.util.NotificationHandler;

import java.util.ArrayList;

public class NewComandaDialogController {

    @FXML
    private ListView<TableDao> tableListView;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private ComboBox<String> employeeList;
    @FXML
    private Label selectedTableLabel;

    private final ObservableList<TableDao> tables = FXCollections.observableArrayList(new Order().getTables());

    @FXML
    public void initialize() {
        selectedTableLabel.setText("");
        tableListView.setItems(tables);
        tableListView.setCellFactory(tlv -> new TableListViewCell());
        tableListView.focusModelProperty().addListener(observable -> {
            int idTable = tableListView.getSelectionModel().getSelectedItem().getIdTable();
            selectedTableLabel.setText("Mesa selecionada: #" + idTable);
        });
        tableListView.setOnMouseClicked(event -> {
            int idTable = tableListView.getSelectionModel().getSelectedItem().getIdTable();
            selectedTableLabel.setStyle("-fx-text-fill: black");
            selectedTableLabel.setText("Mesa selecionada: #" + idTable);
        });

        ArrayList<EmployeeDao> employees = new Personnel().list();
        for (EmployeeDao employee : employees) {
            String func = "Id: " + employee.getIdEmployee() + "; atendente: " + employee.getName();
            employeeList.getItems().add(func);
        }

        cancelButton.setOnMouseClicked(this::onCancelClicked);
        confirmButton.setOnMouseClicked(this::onConfirmClicked);
    }

    public void onConfirmClicked(Event event) {
        Order cashier = new Order();
        OrderDao order = new OrderDao();
        int idTable = 0;

        TableDao selectedTable = tableListView.getSelectionModel().getSelectedItem();
        boolean available = selectedTable.getIdStatus() == 1;
        if (selectedTable != null && available) {
            idTable = tableListView.getSelectionModel().getSelectedItem().getIdTable();
        } else {
            String tableNotAvailableMessage = "Mesa #" + selectedTable.getIdTable() + " não está disponível.";
            selectedTableLabel.setText(tableNotAvailableMessage);
            selectedTableLabel.setStyle("-fx-text-fill: red");
            event.consume();
            return;
        }

        int idCashier = AppFactory.getCashierDao().getIdCashier();
        String defaultMessage = "Cliente na mesa #" + selectedTable.getIdTable();
        order = cashier.newOrder(idCashier, 0, 0, 2, 0,
                defaultMessage);
        cashier.newComanda(idTable, order.getIdOrder(), idCashier, 2);
        Order.changeTableStatus(idTable, 2);
        AppFactory.getManageComandaController().refreshTileList();
        ((Node) event.getSource()).getScene().getWindow().hide();

        NotificationHandler.showInfo("Comanda aberta com sucesso!");
    }

    public void onCancelClicked(Event event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
