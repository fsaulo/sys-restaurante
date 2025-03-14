package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Personnel;

import java.util.ArrayList;
import java.util.Objects;

public class ComandaViewController {

    @FXML
    private Label tableLabel;
    @FXML
    private Label comandaLabel;
    @FXML
    private Button closeComandaButton;
    @FXML
    private Button addOrder;
    @FXML
    private ComboBox<EmployeeDao> employeeComboBox;
    @FXML
    private ListView<ProductDao> productsListView;

    private final ComandaDao comanda;
    private ArrayList<ProductDao> list;

    private boolean isEmployeeListViewHover = false;

    public ComandaViewController(ComandaDao comanda) {
        this.comanda = comanda;
    }

    public void initialize() {

        handleEmployeesComboBox();
        tableLabel.setText("MESA " + comanda.getIdTable());
        comandaLabel.setText("#" + comanda.getIdComanda());
        list = new ArrayList<>(Objects.requireNonNull(Order.getItemsByOrderId(comanda.getIdOrder())));

        productsListView.setItems(FXCollections.observableList(list));
        productsListView.setCellFactory(plv -> new ProductListViewCell(false));
        productsListView.setFocusTraversable(true);
        closeComandaButton.setOnMouseClicked(event -> handleCloseComanda());
        employeeComboBox.setOnAction(event -> {
            updateEmployee();
            isEmployeeListViewHover = true;
        });

        addOrder.setOnMouseClicked(event -> {
            AppFactory.setComandaDao(comanda);
            AppController.showDialog(SceneNavigator.ADD_PRODUCTS_TO_COMANDA,
                    AppFactory.getMainController().getScene().getWindow());
        });
        Platform.runLater(() -> tableLabel.requestFocus());
    }

    public boolean isEmployeeListViewHover() { return isEmployeeListViewHover; }

    public void handleCloseComanda() {
        if (list.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Alerta do sistema");
            alert.setHeaderText("Atenção!");
            alert.setContentText("Não foi possível finalizar pedido, não há nenhum produto na lista.");
            alert.initOwner(AppFactory.getManageComandaController().getWindow());
            alert.showAndWait();
        } else {
            AppFactory.setOrderDao(comanda);
            AppFactory.setSelectedProducts(list);
            AppController.showPaymentDialog();
            AppFactory.getManageComandaController().update();
        }
    }

    public void updateEmployee() {
        int idEmployee;

        try {
            EmployeeDao employee = employeeComboBox.selectionModelProperty().get().getSelectedItem();
            idEmployee = employee.getIdEmployee();
        } catch (NullPointerException ex) {
            idEmployee = -1;
        }

        comanda.setIdEmployee(idEmployee);
        Order.updateEmployee(comanda.getIdOrder(), idEmployee);
    }

    public void handleEmployeesComboBox() {
        ArrayList<EmployeeDao> employees = new Personnel().list();
        Callback<ListView<EmployeeDao>, ListCell<EmployeeDao>> cellFactory = new Callback<>() {
            @Override
            public ListCell<EmployeeDao> call(ListView<EmployeeDao> l) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(EmployeeDao item, boolean empty) {
                        super.updateItem(item, empty);
                        setOnMouseExited(e2 -> isEmployeeListViewHover = false);
                        setOnMouseEntered(e1 -> isEmployeeListViewHover = true);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText("ID: " + item.getIdEmployee() + " " + item.getName());
                        }
                    }
                };
            }
        };

        // TODO: fix glitch that makes this button unusable
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
