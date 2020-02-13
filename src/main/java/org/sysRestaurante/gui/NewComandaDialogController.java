package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Order;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

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
            selectedTableLabel.setStyle("-fx-text-fill: black");
            selectedTableLabel.setText("Mesa selecionada: #" + idTable);
        });

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

        TrayNotification success = new TrayNotification();
        success.setAnimationType(AnimationType.POPUP);
        success.setTitle("Informações do Sistema");
        success.setMessage("Comanda aberta com sucesso!");
        success.setNotificationType(NotificationType.SUCCESS);
        success.showAndDismiss(Duration.seconds(1));

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void onCancelClicked(Event event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
