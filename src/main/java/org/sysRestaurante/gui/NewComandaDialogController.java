package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

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
    private ComboBox<EmployeeDao> employeeList;
    @FXML
    private Label selectedTableLabel;
    @FXML
    private TextField searchBox;

    private final ObservableList<TableDao> tables = FXCollections.observableArrayList(new Order().getTables());

    @FXML
    public void initialize() {
        Platform.runLater(() -> selectedTableLabel.requestFocus());
        handleComboBox();
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
    }

    public void refreshTables() {
        FilteredList<TableDao> filteredData = new FilteredList<>(tables, null);
        String filter = searchBox.getText().toUpperCase();
        String AVAILABLE = "DISPONIVEL;DISPONÍVEL";

        if (filter == null || filter.length() == 0) {
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

    public void onConfirmClicked(Event event) {
        Order cashier = new Order();
        OrderDao order = new OrderDao();
        int idTable = 0;

        boolean empty = false;
        boolean available = false;
        TableDao selectedTable = null;

        try {
            selectedTable = tableListView.getSelectionModel().getSelectedItem();
            available = selectedTable.getIdStatus() == 1;
        } catch (NullPointerException ex) {
            empty = true;
        }


        if (available && !empty) {
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

        int idCashier = AppFactory.getCashierDao().getIdCashier();
        int idEmployee = 0;

        try {
            EmployeeDao employee = employeeList.getSelectionModel().getSelectedItem();
            idEmployee = employee.getIdEmployee();
        } catch (NullPointerException ex) {
            idEmployee = -1;
        }

        String defaultMessage = "Cliente na mesa #" + selectedTable.getIdTable();
        order = cashier.newOrder(idCashier, 0, 0, 2, 0,
                defaultMessage);
        cashier.newComanda(idTable, order.getIdOrder(), idCashier, idEmployee,2);
        Order.changeTableStatus(idTable, 2);
        AppFactory.getManageComandaController().refreshTileList();
        ((Node) event.getSource()).getScene().getWindow().hide();

        NotificationHandler.showInfo("Comanda aberta com sucesso!");
    }

    public void handleComboBox() {
        ArrayList<EmployeeDao> employees = new Personnel().list();
        Callback<ListView<EmployeeDao>, ListCell<EmployeeDao>> cellFactory = new Callback<>() {
            @Override
            public ListCell<EmployeeDao> call(ListView<EmployeeDao> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(EmployeeDao item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText("ID: " + item.getIdEmployee() + "    " + item.getName());
                        }
                    }
                };
            }
        };
        employeeList.setButtonCell(cellFactory.call(null));
        employeeList.setCellFactory(cellFactory);

        for (EmployeeDao employee : employees) {
            employeeList.getItems().add(employee);
        }
    }

    public void onCancelClicked(Event event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
