package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Receipt;

import java.util.ArrayList;

public class ReceiptViewController {

    @FXML
    TextFlow receiptText;

    public void initialize() {
        OrderDao orderDao = AppFactory.getOrderDao();
        ArrayList<ProductDao> products = AppFactory.getSelectedProducts();
        Receipt receipt = new Receipt(orderDao, products);
        receipt.buildReceipt();
        Text rpp = new Text(receipt.getReceipt());
        rpp.setFont(new Font("DejaVu Sans Mono", 13));
        receiptText.getChildren().add(rpp);
    }
}
