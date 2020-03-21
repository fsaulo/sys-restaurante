package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
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
    private HBox box4;
    @FXML
    private VBox confirmBox;
    @FXML
    private VBox goBackButton;
    @FXML
    private VBox seeReceiptBox;
    @FXML
    private VBox saveReceipt;
    @FXML
    private VBox cancelButton;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Label changeLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private ToggleButton ba0;
    @FXML
    private ToggleButton ba5;
    @FXML
    private ToggleButton ba10;
    @FXML
    private ToggleButton ba15;
    @FXML
    private ToggleButton ba20;
    @FXML
    private ToggleButton bs0;
    @FXML
    private ToggleButton bs5;
    @FXML
    private ToggleButton bs10;
    @FXML
    private ToggleButton bs15;
    @FXML
    private ToggleButton bs20;
    @FXML
    private Button tc1;
    @FXML
    private Button tc2;
    @FXML
    private Button c1;
    @FXML
    private Button c2;

    private CurrencyField payInCash;
    private CurrencyField payByCard;
    private PercentageField percentageField1;
    private PercentageField percentageField2;

    @FXML
    public void initialize() {
        setInputFilds();
        triggerActions();

        Format format = CurrencyField.getBRLCurrencyFormat();
        percentageField1.textProperty().addListener(observable -> {
            subtotalLabel.setText(format.format(getSubtotal()));
            changeLabel.setText(format.format(getChange()));
        });

        percentageField2.textProperty().addListener(observable -> {
            subtotalLabel.setText(format.format(getSubtotal()));
            changeLabel.setText(format.format(getChange()));
        });

        changeLabel.textProperty().addListener(observable -> {
            if (getChange() < 0) {
                changeLabel.setTextFill(Color.RED);
            } else {
                changeLabel.setTextFill(Color.valueOf("#78d34e"));
            }
        });

        if (AppFactory.getOrderDao() instanceof ComandaDao) cancelButton.setDisable(true);
        seeReceiptBox.setOnMouseClicked(event -> viewReceipt());
        subtotalLabel.setText(format.format(getSubtotal()));
        changeLabel.setText(format.format(getChange()));
        payInCash.textProperty().addListener(observable -> changeLabel.setText(format.format(getChange())));
        payByCard.textProperty().addListener(observable -> changeLabel.setText(format.format(getChange())));
        Platform.runLater(this::handleKeyEvent);
    }

    @FXML
    public void confirm() {
        Order order = new Order();
        ArrayList<ProductDao> items = AppFactory.getSelectedProducts();

        double discount = this.percentageField1.getAmount();
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
            OrderDao orderDao = AppFactory.getOrderDao();

            if (orderDao instanceof ComandaDao || orderDao == null) {
                int idComanda = ((ComandaDao) orderDao).getIdComanda();
                order.closeComanda(idComanda, payByCard + payInCash);
                order.addProductsToOrder(orderDao.getIdOrder(), items);
                order.updateOrderStatus(orderDao.getIdOrder(), 1);
                order.updateOrderAmount(orderDao.getIdOrder(), payInCash, payByCard, discount);
                order.closeTable(((ComandaDao) orderDao).getIdTable());
                new Cashier().setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
                AppFactory.getManageComandaController().refreshTileList();
            } else {
                orderDao = order.newOrder(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 1,
                        discount, note.toString());
                new Cashier().setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
                order.addProductsToOrder(orderDao.getIdOrder(), items);
                AppFactory.getCashierController().updateCashierElements();
                AppFactory.getCashierController().setSellConfirmed(true);
            }

            AppFactory.setOrderDao(null);
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
            confirmBox.getScene().getWindow().hide();
            AppFactory.setOrderDao(new OrderDao());
            if (AppFactory.getPosController() != null) AppFactory.getPosController().getPOSWindow().hide();
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
            Receipt receipt = new Receipt(AppFactory.getSelectedProducts(), AppFactory.getOrderDao());
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

    public void setInputFilds() {
        Font font = Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 20);
        double width = 170.0;
        payInCash = new CurrencyField(new Locale("pt", "BR"));
        payInCash.setFont(font);
        payInCash.setPrefWidth(width);
        payInCash.setAmount(getTotal());
        payByCard = new CurrencyField(new Locale("pt", "BR"));
        payByCard.setFont(font);
        payByCard.setPrefWidth(width);
        percentageField1 = new PercentageField();
        percentageField1.setFont(font);
        percentageField1.setPrefWidth(width);
        percentageField2 = new PercentageField();
        percentageField2.setFont(font);
        percentageField2.setPrefWidth(width);

        confirmBox.setDisable(false);
        confirmBox.setOnMouseClicked(event -> confirm());
        box1.getChildren().add(payInCash);
        box2.getChildren().add(payByCard);
        box3.getChildren().add(percentageField1);
        box4.getChildren().add(percentageField2);
    }

    public void triggerActions() {
        ba0.setOnAction(e1 -> percentageField1.setAmount(0.0));
        ba5.setOnAction(e1 -> percentageField1.setAmount(.05));
        ba10.setOnAction(e1 -> percentageField1.setAmount(.10));
        ba15.setOnAction(e1 -> percentageField1.setAmount(.15));
        ba20.setOnAction(e1 -> percentageField1.setAmount(.20));

        bs0.setOnAction(e2 -> percentageField2.setAmount(0.0));
        bs5.setOnAction(e2 -> percentageField2.setAmount(.05));
        bs10.setOnAction(e2 -> percentageField2.setAmount(.10));
        bs15.setOnAction(e2 -> percentageField2.setAmount(.15));
        bs20.setOnAction(e2 -> percentageField2.setAmount(.20));

        c1.setOnMouseClicked(e3 -> payInCash.setAmount(0.0));
        c2.setOnMouseClicked(e3 -> payByCard.setAmount(0.0));

        tc1.setOnMouseClicked(e4 -> {
            payInCash.setAmount(getSubtotal());
            payByCard.setAmount(0.0);
        });

        tc2.setOnMouseClicked(e4 -> {
            payByCard.setAmount(getSubtotal());
            payInCash.setAmount(0.0);
        });
    }

    public void viewReceipt() {
        receiptContentConstructor();
        AppController.showDialog(SceneNavigator.RECEIPT_VIEW, changeLabel.getScene().getWindow());
    }

    public void receiptContentConstructor() {
        OrderDao orderDao = new OrderDao();
        orderDao.setOrderDate(LocalDate.now());
        orderDao.setOrderTime(LocalTime.now());
        orderDao.setTotal(getTotal());
        orderDao.setDiscount(percentageField1.getAmount() * 100);
        orderDao.setIdOrder(new Order().getLastOrderId() + 1);
        AppFactory.setOrderDao(orderDao);
        ArrayList<ProductDao> products = AppFactory.getSelectedProducts();
        AppFactory.setSelectedProducts(products);
    }

    public void handleKeyEvent() {
        subtotalLabel.getParent().requestFocus();
        subtotalLabel.getScene().getAccelerators().clear();
        subtotalLabel.getScene().getAccelerators().put(SceneNavigator.F4_CANCEL, () ->
                box1.getScene().getWindow().hide());
        subtotalLabel.getScene().getAccelerators().put(SceneNavigator.F2_CONFIRMATION, this::confirm);
    }

    public double getTotal() {
        double total = 0;
        for (ProductDao item : AppFactory.getSelectedProducts()) {
            total += item.getTotal();
        }
        return total;
    }

    public double getSubtotal() {
        double total = getTotal();
        double discount = total * percentageField1.getAmount();
        double serviceTax = total * percentageField2.getAmount();
        return total + serviceTax - discount;
    }

    public double getChange() {
        double subtotal = getSubtotal();
        double change = Math.round(((payInCash.getAmount() + payByCard.getAmount()) - (subtotal))*100)/100.0;
        return change;
    }
}
