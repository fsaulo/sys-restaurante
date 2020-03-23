package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;
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
    private ComboBox<String> employeeList;

    private ComandaDao comanda;

    public ComandaViewController(ComandaDao comanda) {
        this.comanda = comanda;
    }

    public void initialize() {
        Platform.runLater(() -> tableLabel.requestFocus());
        tableLabel.setText("MESA " + comanda.getIdTable());
        comandaLabel.setText("#" + comanda.getIdComanda());
        ArrayList<ProductDao> list = new ArrayList<>();
        ArrayList<EmployeeDao> employees = new Personnel().list();

        for (EmployeeDao employee : employees) {
            String func = "Id: " + employee.getIdEmployee() + "; atendente: " + employee.getName();
            employeeList.getItems().add(func);
        }

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
}
