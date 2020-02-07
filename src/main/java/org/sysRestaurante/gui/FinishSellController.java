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
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Receipt;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.util.PercentageField;

import java.net.MalformedURLException;
import java.text.Format;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private VBox goBackButton;
    @FXML
    private VBox seeReceiptBox;
    @FXML
    private VBox saveReceipt;
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
        Font font = Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20);
        payInCash = new CurrencyField(new Locale("pt", "BR"));
        payInCash.setFont(font);
        payInCash.setPrefWidth(200);
        payInCash.setAmount(AppFactory.getCashierPOSController().getTotal());
        payByCard = new CurrencyField(new Locale("pt", "BR"));
        payByCard.setFont(font);
        payByCard.setPrefWidth(200);
        percentageField = new PercentageField();
        percentageField.setFont(font);
        percentageField.setPrefWidth(200);
        confirmBox.setDisable(false);
        confirmBox.setOnMouseClicked(event -> confirm());
        box1.getChildren().add(payInCash);
        box2.getChildren().add(payByCard);
        box3.getChildren().add(percentageField);

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

        seeReceiptBox.setOnMouseClicked(event -> viewReceipt());
        subtotalLabel.setText(format.format(getSubtotal()));
        changeLabel.setText(format.format(getChange()));
        payInCash.textProperty().addListener((observable, oldValue, newValue) ->
                changeLabel.setText(format.format(getChange())));
        payByCard.textProperty().addListener((observable, oldValue, newValue) ->
                changeLabel.setText(format.format(getChange())));
        Platform.runLater(this::handleKeyEvent);
    }

    @FXML
    public void confirm() {
        Order order = new Order();
        ObservableList<ProductDao> items = AppFactory.getCashierPOSController().getItems();

        double discount = this.percentageField.getAmount();
        double change = getChange();
        StringBuilder note = new StringBuilder();
        note.append((discount > 0) ? "Descontos aplicados: " + (int) (100 * discount) + "%" : "");

        if (note.toString().equals("") && noteTextArea.getText().isBlank()) {
            note.append("Sem observações");
        } else if (!noteTextArea.getText().isBlank() && !noteTextArea.getText().isEmpty()){
            note.append(" ; ");
            note.append(noteTextArea.getText());
        }

        if (change < 0.0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Não foi possível completar transação.");
            alert.setContentText("Valor pago inferior ao valor do pedido");
            alert.setTitle("Alerta do sistema");
            alert.initOwner(confirmBox.getScene().getWindow());
            alert.showAndWait();
        } else {
            double payByCard = this.payByCard.getAmount();
            double payInCash = getSubtotal() - payByCard;
            OrderDao orderDao = order.newOrder(AppFactory.getCashierDao().getIdCashier(),
                    payInCash,
                    payByCard,
                    1,
                    discount,
                    note.toString());
            new Cashier().setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
            order.addProductsToOrder(orderDao.getIdOrder(), items);
            AppFactory.getCashierController().updateCashierElements();
            AppFactory.getCashierController().setSellConfirmed(true);
            AppFactory.setOrderDao(orderDao);
            box1.getScene().getWindow().hide();
        }
    }

    @FXML
    public void cancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar venda?");
        alert.setContentText("Todos os registros salvos serão perdidos.");
        alert.initOwner(confirmBox.getScene().getWindow());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            changeLabel.getScene().getWindow().hide();
            AppFactory.setOrderDao(new OrderDao());
            AppFactory.getCashierPOSController().getPOSWindow().hide();
        }
    }

    @FXML
    public void back() {
        goBackButton.getScene().getWindow().hide();
    }

    @FXML
    public void saveReceipt() {
        try {
            receiptContentConstructor();
            Receipt receipt = new Receipt(
                    AppFactory.getSelectedProducts(),
                    AppFactory.getOrderDao());
            receipt.saveReceiptAsPng(saveReceipt.getScene().getWindow());
        } catch (MalformedURLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Informações de erro");
            alert.setHeaderText("Arquivo não contém extensão válida.");
            alert.setContentText("Extensões válidas: *.png");
            alert.initOwner(confirmBox.getScene().getWindow());
            alert.showAndWait();
        }
    }

    public void viewReceipt() {
        receiptContentConstructor();
        AppController.showDialog(SceneNavigator.RECEIPT_VIEW, changeLabel.getScene().getWindow());
    }

    public void receiptContentConstructor() {
        OrderDao orderDao = new OrderDao();
        orderDao.setOrderDate(LocalDate.now());
        orderDao.setOrderTime(LocalTime.now());
        orderDao.setTotal(AppFactory.getCashierPOSController().getTotal());
        orderDao.setDiscount(percentageField.getAmount() * 100);
        orderDao.setIdOrder(new Order().getLastOrderId() + 1);
        AppFactory.setOrderDao(orderDao);
        ArrayList<ProductDao> products = AppFactory.getSelectedProducts();
        AppFactory.setSelectedProducts(products);
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
        double change = Math.round(((payInCash.getAmount() + payByCard.getAmount()) - (total - discount))*100)/100.0;
        return change;
    }

    public double getSubtotal() {
        double total = AppFactory.getCashierPOSController().getTotal();
        double discount = total * percentageField.getAmount();
        return total - discount;
    }
}
