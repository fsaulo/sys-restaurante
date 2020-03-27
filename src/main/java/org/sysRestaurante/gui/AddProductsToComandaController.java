package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;

import java.util.ArrayList;

public class AddProductsToComandaController {

    @FXML
    private Button exitButton;
    @FXML
    private Button finalizeOrderButton;
    @FXML
    private VBox wrapperVBox;
    @FXML
    private Label tableLabel;
    @FXML
    private Label codOrderLabel;
    @FXML
    private ComboBox<EmployeeDao> employeeComboBox;
    @FXML
    private TextField customerBox;
    @FXML
    private Label customerLabel;
    @FXML
    private Label subtotalLabel;

    private ComandaDao comanda = AppFactory.getComandaDao();

    @FXML
    public void initialize() {
        handleEmployeesComboBox();
        writeCustomer();

        exitButton.setOnAction(e1 -> {
            wrapperVBox.getScene().getWindow().hide();
            AppFactory.getManageComandaController().refreshTileList();
            setCustomer();
        });

        employeeComboBox.setOnAction(event -> updateEmployee());
        finalizeOrderButton.setOnAction(e2 -> finalizeOrder());
        tableLabel.setText("MESA #" + this.comanda.getIdTable());
        codOrderLabel.setText(String.valueOf(comanda.getIdOrder()));
        customerLabel.setText(Order.getCustomerName(comanda.getIdOrder()));
        customerBox.setOnKeyTyped(e-> customerLabel.setText(customerBox.getText()));
        subtotalLabel.setText("R$ " + comanda.getTotal());
        Platform.runLater(this::handleKeyEvent);
    }

    public void handleKeyEvent() {
        wrapperVBox.requestFocus();
        wrapperVBox.getScene().getWindow().setOnCloseRequest(e1 -> {
            setCustomer();
            AppFactory.getManageComandaController().refreshTileList();
        });
    }

    public void setCustomer() {
        Order.insertCustomerName(comanda.getIdOrder(), customerBox.getText());
    }

    public void writeCustomer() {
        customerBox.setText(Order.getCustomerName(comanda.getIdOrder()));
    }

    public void updateEmployee() {
        int idEmployee;

        try {
            EmployeeDao employee = employeeComboBox.selectionModelProperty().get().getSelectedItem();
            idEmployee = employee.getIdEmployee();
        } catch (NullPointerException ex) {
            idEmployee = -1;
        }

        Order.updateEmployee(comanda.getIdOrder(), idEmployee);
    }

    public void finalizeOrder() { }

    public void handleEmployeesComboBox() {
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
                            setText("ID: " + item.getIdEmployee() + " " + item.getName());
                        }
                    }
                };
            }
        };

        employeeComboBox.setButtonCell(cellFactory.call(null));
        employeeComboBox.setCellFactory(cellFactory);

        for (EmployeeDao employee : employees) {
            employeeComboBox.getItems().add(employee);
        }

        EmployeeDao selectedEmployee = null;
        for (EmployeeDao emp : employees) {
            if (emp.getIdEmployee() == comanda.getIdEmployee()) {
                selectedEmployee = emp;
                break;
            }
        }

        if (selectedEmployee != null ) {
            employeeComboBox.getSelectionModel().select(selectedEmployee);
        }
    }
}
