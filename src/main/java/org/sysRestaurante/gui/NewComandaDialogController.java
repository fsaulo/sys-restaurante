package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.HBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Order;

public class NewComandaDialogController {

    @FXML
    private ListView<TableDao> tableListView;
    @FXML
    private HBox cancelButton;
    @FXML
    private HBox confirmButton;
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
            selectedTableLabel.setText("Mesa selecionada: #" + idTable);
        });

        cancelButton.setOnMouseClicked(this::onCancelClicked);
        confirmButton.setOnMouseClicked(this::onConfirmClicked);
    }

    public void onConfirmClicked(Event event) {
        Order cashier = new Order();
        OrderDao order = new OrderDao();
        int idTable = 0;

        if (tableListView.getSelectionModel().getSelectedItem() != null) {
            idTable = tableListView.getSelectionModel().getSelectedItem().getIdTable();
        } else {
            return;
        }

        int idCashier = AppFactory.getCashierDao().getIdCashier();
        String defaultMessage = "Cliente na mesa #";
        order = cashier.newOrder(idCashier, 0, 0, 2, 0,
                defaultMessage);
        cashier.newComanda(idTable, order.getIdOrder(), idCashier, 2);
        AppFactory.getManageComandaController().refreshTileList();
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void onCancelClicked(Event event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
