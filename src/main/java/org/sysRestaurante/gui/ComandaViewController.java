package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Personnel;

import java.util.ArrayList;

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
    private VBox popOverVbox;
    @FXML
    private ComboBox<EmployeeDao> employeeList;

    private ComandaDao comanda;

    public ComandaViewController(ComandaDao comanda) {
        this.comanda = comanda;
    }

    public void initialize() {
        Platform.runLater(() -> tableLabel.requestFocus());
        handleEmployeesComboBox();
        tableLabel.setText("MESA " + comanda.getIdTable());
        comandaLabel.setText("#" + comanda.getIdComanda());
        ArrayList<ProductDao> list = new ArrayList<>();

        ProductDao productDao = new ProductDao();
        productDao.setIdProduct(1);
        productDao.setQuantity(1);
        productDao.setSellPrice(5.0);
        productDao.setTotal(5.0);
        productDao.setDescription("Dummy product");
        list.add(productDao);

        closeComandaButton.setOnMouseClicked(event -> {
            AppFactory.setSelectedProducts(list);
            AppFactory.setOrderDao(comanda);
            AppController.openFinishSell();
        });
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
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText("ID: " + item.getIdEmployee() + " " + item.getName());
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

        EmployeeDao selectedEmployee = null;
        for (EmployeeDao emp : employees) {
            if (emp.getIdEmployee() == comanda.getIdEmployee()) {
                selectedEmployee = emp;
                break;
            }
        }

        if (selectedEmployee != null ) {
            employeeList.getSelectionModel().select(selectedEmployee);
        }
    }
}
