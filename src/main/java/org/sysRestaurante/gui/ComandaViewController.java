package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.ProductDao;

import java.util.ArrayList;

public class ComandaViewController {

    @FXML
    private Label tableLabel;
    @FXML
    private Label comandaLabel;
    @FXML
    private Button closeComandaButton;
    private ComandaDao comanda;

    public ComandaViewController(ComandaDao comanda) {
        this.comanda = comanda;
    }

    public void initialize() {
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
}
