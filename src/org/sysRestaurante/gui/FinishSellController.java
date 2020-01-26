package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private VBox seeReceiptBox;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Label changeLabel;
    @FXML
    private Label subtotalLabel;

    private CurrencyField payInCash;
    private CurrencyField payByCard;
    private PercentageField percentageField;

    @FXML
    public void initialize() {
        payInCash = new CurrencyField(new Locale("pt",  "BR"));
        payInCash.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        payInCash.setPrefWidth(200);
        payInCash.setAmount(AppFactory.getCashierPOSController().getTotal());
        payByCard = new CurrencyField(new Locale("pt",  "BR"));
        payByCard.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        payByCard.setPrefWidth(200);
        percentageField = new PercentageField();
        percentageField.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20));
        percentageField.setPrefWidth(200);
        confirmBox.setDisable(false);

        box1.getChildren().add(payInCash);
        box2.getChildren().add(payByCard);
        box3.getChildren().add(percentageField);
        confirmBox.setOnMouseClicked(event -> confirm());

        Format format = CurrencyField.getBRLCurrencyFormat();
        percentageField.textProperty().addListener((observable, oldValue, newValue) -> {
            subtotalLabel.setText(format.format(getSubtotal()));
            changeLabel.setText(format.format(getChange()));
        });

        changeLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (getChange() < 0) {
                changeLabel.setTextFill(Color.RED);
            } else {
                changeLabel.setTextFill(Color.valueOf("#78d34e"));
            }
        });

        Platform.runLater(this::handleKeyEvent);
        seeReceiptBox.setOnMouseClicked(event -> viewReceipt());
        subtotalLabel.setText(format.format(getSubtotal()));
        changeLabel.setText(format.format(getChange()));
        payInCash.textProperty().addListener((observable, oldValue, newValue) ->
                changeLabel.setText(format.format(getChange())));
        payByCard.textProperty().addListener((observable, oldValue, newValue) ->
                changeLabel.setText(format.format(getChange())));
}

    @FXML
    public void confirm() {
        Cashier cashier = new Cashier();
        ObservableList<ProductDao> items = AppFactory.getCashierPOSController().getItems();

        double discount = this.percentageField.getAmount();
        double change = getChange();
        String note = noteTextArea.getText();

        if (note == null || note.isEmpty()) {
            note = (discount > 0) ? "Descontos aplicados: " + (int) (100*discount) + "%" : "Sem observações";
        }

        if (change < 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Não foi possível completar transação.");
            alert.setContentText("Valor pago inferior ao valor do pedido");
            alert.setTitle("Alerta do sistema");
            alert.showAndWait();
        } else {
            double payByCard = this.payByCard.getAmount();
            double payInCash = getSubtotal() - payByCard;
            OrderDao orderDao = cashier.newOrder(AppFactory.getCashierDao().getIdCashier(),
                    payInCash,
                    payByCard,
                    1,
                    discount,
                    note);
            cashier.setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
            cashier.addProductsToOrder(orderDao.getIdOrder(), items);
            AppFactory.getCashierController().updateCashierElements();
            AppFactory.getCashierController().setSellConfirmed(true);
            AppFactory.setOrderDao(orderDao);
//            box1.getScene().getWindow().hide();
        }
    }

    public void viewReceipt() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação do sistema");
        alert.setHeaderText("Pedido não confirmado.");
        alert.setContentText("Para ver o recibo você deve confirmar o pedido. Deseja continuar?");
        alert.showAndWait();

        if (alert.getResult().equals(ButtonType.OK)) {
            confirm();
            if (AppFactory.getCashierController().isSellConfirmed()) {
                AppController.showDialog(SceneNavigator.RECEIPT_VIEW);
                confirmBox.setDisable(true);
            }
        }
    }

    public void handleKeyEvent() {
        subtotalLabel.getScene().getAccelerators().clear();
        subtotalLabel.getScene().getAccelerators().put(SceneNavigator.F4_CANCEL, () ->
                box1.getScene().getWindow().hide());
        subtotalLabel.getScene().getAccelerators().put(SceneNavigator.F2_CONFIRMATION, this::confirm);
    }

    public double getChange() {
        double total = AppFactory.getCashierPOSController().getTotal();
        double discount = total * percentageField.getAmount();
        return (payInCash.getAmount() + payByCard.getAmount()) - (total - discount);
    }

    public double getSubtotal() {
        double total = AppFactory.getCashierPOSController().getTotal();
        double discount = total * percentageField.getAmount();
        return total - discount;
    }
}
