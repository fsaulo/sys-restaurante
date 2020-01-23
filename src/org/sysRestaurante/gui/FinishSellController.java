package org.sysRestaurante.gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.CurrencyField;
import org.sysRestaurante.util.PercentageField;

import java.util.Locale;

public class FinishSellController {

    @FXML
    private HBox box1;
    @FXML
    private HBox box2;
    @FXML
    private HBox box3;
    @FXML
    private VBox confirm;

    @FXML
    public void initialize() {
        CurrencyField payInCash = new CurrencyField(new Locale("pt",  "BR"));
        CurrencyField payByCard = new CurrencyField(new Locale("pt",  "BR"));
        PercentageField percentageField = new PercentageField();
        payInCash.setPrefWidth(200);
        payByCard.setPrefWidth(200);
        percentageField.setPrefWidth(200);
        payByCard.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        payInCash.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        percentageField.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        box1.getChildren().add(payInCash);
        box2.getChildren().add(payByCard);
        box3.getChildren().add(percentageField);
        confirm.setOnMouseClicked(event -> {
            Cashier cashier = new Cashier();
            ObservableList items = AppFactory.getCashierPOSController().getItems();
            double total = AppFactory.getCashierPOSController().getTotal();
            OrderDao orderDao = cashier.newOrder(AppFactory.getCashierDao().getIdCashier(), total, 0, "");
            cashier.setRevenue(AppFactory.getCashierDao().getIdCashier(), total, 0, 0);
            cashier.addProductsToOrder(orderDao.getIdOrder(), items);
            AppFactory.getCashierController().updateCashierElements();
            AppFactory.getCashierController().setSellConfirmed(true);
            box1.getScene().getWindow().hide();
        });
    }
}
