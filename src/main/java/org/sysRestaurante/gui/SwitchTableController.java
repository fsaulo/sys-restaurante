package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.TableDao;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.model.Order;

public class SwitchTableController {
    @FXML
    private ListView<TableDao> tableListView;
    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Label selectedTableLabel;
    @FXML
    private TextField searchBox;

    private final ObservableList<TableDao> tables = FXCollections.observableArrayList(Management.getTables());

    @FXML
    public void initialize() {
        selectedTableLabel.setText("Nenhuma mesa selecionada");
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
        searchBox.textProperty().addListener((observable -> refreshTables()));
        searchBox.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !tableListView.getItems().isEmpty()) {
                tableListView.getSelectionModel().selectFirst();
                onConfirmClicked(event);
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                searchBox.clear();
                selectedTableLabel.requestFocus();
            }
        });

        Platform.runLater(() -> selectedTableLabel.requestFocus());
    }

    public void refreshTables() {
        FilteredList<TableDao> filteredData = new FilteredList<>(tables, null);
        String filter = searchBox.getText().toUpperCase();
        String AVAILABLE = "DISPONIVEL;DISPONÍVEL";

        if (filter.length() == 0) {
            filteredData.setPredicate(null);
        }
        else if (AVAILABLE.contains(filter.toUpperCase())) {
            filteredData.setPredicate(s -> s.getIdStatus() == 1);
        } else {
            filteredData.setPredicate(s -> String.valueOf(s.getIdTable()).contains(filter) ||
                                           AVAILABLE.contains(filter.toUpperCase()));
        }

        tableListView.setItems(filteredData);
    }

    private void onCancelClicked(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).getScene().getWindow().hide();
    }

    private void onConfirmClicked(Event event) {
        ComandaDao order = AppFactory.getComandaDao();
        int idTable;

        boolean empty = false;
        boolean available = false;
        TableDao selectedTable = null;

        try {
            selectedTable = tableListView.getSelectionModel().getSelectedItem();
            available = selectedTable.getIdStatus() == 1;
        } catch (NullPointerException ex) {
            empty = true;
        }


        if (available) {
            idTable = tableListView.getSelectionModel().getSelectedItem().getIdTable();
        } else  {
            if (empty) {
                String empySelectionMessage = "Selecione uma mesa!";
                selectedTableLabel.setText(empySelectionMessage);
                selectedTableLabel.setStyle("-fx-text-fill: red");

            } else {
                String tableNotAvailableMessage = "Mesa #" + selectedTable.getIdTable() + " não está disponível.";
                selectedTableLabel.setText(tableNotAvailableMessage);
                selectedTableLabel.setStyle("-fx-text-fill: red");
            }
            event.consume();
            return;
        }

        Management.changeTableStatus(order.getIdTable(), 1);
        Order.changeTable(order.getIdComanda(), idTable);
        Management.changeTableStatus(idTable, Management.UNAVAILABLE);
        order.setIdTable(idTable);
        AppFactory.setComandaDao(order);
        ((Node) event.getSource()).getScene().getWindow().hide();
        AppFactory.getComandaPOSController().updateTableCod();
        AppFactory.getComandaPOSController().handleChangeTable();
    }
}
