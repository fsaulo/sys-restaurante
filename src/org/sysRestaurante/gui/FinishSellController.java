package org.sysRestaurante.gui;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.CurrencyField;
import org.sysRestaurante.util.PercentageField;

import java.text.Format;
import java.util.Locale;

public class FinishSellController {

    @FXML
    private HBox box1;
    @FXML
    private HBox box2;
    @FXML
    private HBox box3;
    @FXML
    private VBox confirmBox;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Label changeLabel;

    private CurrencyField payInCash;
    private CurrencyField payByCard;

    @FXML
    public void initialize() {
        payInCash = new CurrencyField(new Locale("pt",  "BR"));
        payByCard = new CurrencyField(new Locale("pt",  "BR"));
        PercentageField percentageField = new PercentageField();
        payInCash.setPrefWidth(200);
        payByCard.setPrefWidth(200);
        double total = AppFactory.getCashierPOSController().getTotal();
        double change = (payInCash.getAmount() + payByCard.getAmount()) - total;
        Format format = CurrencyField.getBRLCurrencyFormat();
        changeLabel.setText(format.format((-1) * change));
        changeLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if ((payInCash.getAmount() + payByCard.getAmount() - total) < 0) {
                changeLabel.setTextFill(Color.RED);
            } else {
                changeLabel.setTextFill(Color.valueOf("#78d34e"));
            }
        });
        payInCash.textProperty().addListener((observable, oldValue, newValue) ->
                changeLabel.setText(format.format((payInCash.getAmount() + payByCard.getAmount()) - total)));
        payByCard.textProperty().addListener((observable, oldValue, newValue) ->
                changeLabel.setText(format.format((payInCash.getAmount() + payByCard.getAmount()) - total)));

        percentageField.setPrefWidth(200);
        payByCard.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        payInCash.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        percentageField.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        box1.getChildren().add(payInCash);
        box2.getChildren().add(payByCard);
        box3.getChildren().add(percentageField);
        confirmBox.setOnMouseClicked(this::confirm);
    }

    @FXML
    public void confirm(Event event) {
        Cashier cashier = new Cashier();
        ObservableList<ProductDao> items = AppFactory.getCashierPOSController().getItems();
        double total = AppFactory.getCashierPOSController().getTotal();
        double payByCard = this.payByCard.getAmount();
        double payInCash = this.payInCash.getAmount();
        double change = (payByCard + payInCash) - total;
        String note = noteTextArea.getText() != null ? noteTextArea.getText() : "Sem observações";

        if (change < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Não foi possível completar transação.");
            alert.setContentText("Valor pago inferior ao valor do pedido");
            alert.setTitle("Alerta do sistema");
            alert.showAndWait();
        } else {
            OrderDao orderDao = cashier.newOrder(
                    AppFactory.getCashierDao().getIdCashier(),
                    payInCash,
                    payByCard,
                    note);
            cashier.setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
            cashier.addProductsToOrder(orderDao.getIdOrder(), items);
            AppFactory.getCashierController().updateCashierElements();
            AppFactory.getCashierController().setSellConfirmed(true);
            box1.getScene().getWindow().hide();
        }
    }
}
