package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.model.Receipt;

import java.util.ArrayList;

public class ReceiptViewController {

    @FXML
    TextFlow receiptText;

    public void initialize() {
        UserDao userDao = AppFactory.getUserDao();
        OrderDao orderDao = AppFactory.getOrderDao();
        ArrayList<ProductDao> products = AppFactory.getSelectedProducts();
        String receipt = new Receipt(products, userDao, orderDao).getReceipt();

        Text rpp = new Text(receipt);
        rpp.setFont(new Font("monospaced", 12));
        receiptText.getChildren().add(rpp);
    }
}
