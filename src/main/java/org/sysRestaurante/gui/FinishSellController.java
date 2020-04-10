package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Receipt;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.util.PercentageField;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.Format;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

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
    private HBox hBoxControl;
    @FXML
    private VBox confirmBox;
    @FXML
    private VBox seeReceiptBox;
    @FXML
    private VBox saveReceipt;
    @FXML
    private VBox cancelButton;
    @FXML
    private VBox wrapperVBox;
    @FXML
    private TextArea noteTextArea;
    @FXML
    private Label changeLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label discountLabel;
    @FXML
    private Label taxLabel;
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
    @FXML
    private Button cc1;
    @FXML
    private Button cc2;

    private ArrayList<ProductDao> products;
    private CurrencyField payInCash;
    private CurrencyField payByCard;
    private PercentageField percentageField1;
    private PercentageField percentageField2;
    private static String GREEN = "#4a8d2c";
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(FinishSellController.class.getName());

    @FXML
    public void initialize() {
        setInputFilds();
        triggerActions();

        Format format = CurrencyField.getBRLCurrencyFormat();
        AppController.setSellConfirmed(false);
        percentageField1.textProperty().addListener(observable -> {
            totalLabel.setText(format.format(getTotal()));
            changeLabel.setText(format.format(getChange()));
            discountLabel.setText(format.format(getDiscount()));
        });

        percentageField2.textProperty().addListener(observable -> {
            totalLabel.setText(format.format(getTotal()));
            changeLabel.setText(format.format(getChange()));
            taxLabel.setText(format.format(getTax()));
        });

        changeLabel.textProperty().addListener(observable -> {
            if (getChange() < 0) {
                changeLabel.setTextFill(Color.RED);
            } else {
                changeLabel.setTextFill(Color.valueOf(GREEN));
            }
        });

        if (AppFactory.getOrderDao() instanceof ComandaDao) cancelButton.setDisable(true);
        PopOver popOver = viewReceipt();

        seeReceiptBox.setOnMouseClicked(event -> {
            popOver.show(seeReceiptBox);
            hBoxControl.setDisable(true);
        });

        popOver.setOnHidden(e1 -> hBoxControl.setDisable(false));
        totalLabel.setText(format.format(getTotal()));
        subtotalLabel.setText(format.format(getSubtotal()));
        discountLabel.setText(format.format(getDiscount()));
        changeLabel.setText(format.format(getChange()));
        taxLabel.setText(format.format(getTax()));
        payInCash.textProperty().addListener(observable -> changeLabel.setText(format.format(getChange())));
        payByCard.textProperty().addListener(observable -> changeLabel.setText(format.format(getChange())));
        Platform.runLater(this::handleKeyEvent);
    }

    @FXML
    public void cancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar venda?");
        alert.setContentText("Todos os registros salvos serão perdidos.");
        alert.initOwner(wrapperVBox.getScene().getWindow());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            wrapperVBox.getScene().getWindow().hide();
            AppFactory.setOrderDao(new OrderDao());
            if (AppFactory.getPos() != null) AppFactory.getPos().getPOSWindow().hide();
        }
    }

    @FXML
    public void back() {
        box1.getScene().getWindow().hide();
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
            alert.initOwner(wrapperVBox.getScene().getWindow());
            alert.showAndWait();
        }
    }

    public void confirm() {
        Order order = new Order();
        ArrayList<ProductDao> items = AppFactory.getSelectedProducts();

        double discount = this.percentageField1.getAmount();
        double change = getChange();
        StringBuilder note = new StringBuilder();
        note.append((discount > 0) ? "Descontos aplicados: " + (int) (100 * discount) + "%" : "");

        if (note.toString().equals("") && noteTextArea.getText().isBlank()) {
            note.append("Sem observações");
        } else if (!noteTextArea.getText().isBlank() && !noteTextArea.getText().isEmpty()) {
            note.append(" ; ");
            note.append(noteTextArea.getText());
        }

        if (change < 0.0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Não foi possível completar transação.");
            alert.setContentText("Valor pago inferior ao valor do pedido");
            alert.setTitle("Alerta do sistema");
            alert.initOwner(wrapperVBox.getScene().getWindow());
            alert.showAndWait();
        } else {
            double payByCard = this.payByCard.getAmount();
            double payInCash = getTotal() - payByCard;
            OrderDao orderDao = AppFactory.getOrderDao();

            if (orderDao instanceof ComandaDao || orderDao == null) {
                LOGGER.info("Instance of 'Comanda' Data Access Object");
                order.closeComanda((ComandaDao) orderDao, payByCard + payInCash);
                order.addProductsToOrder(orderDao.getIdOrder(), items);
                order.updateOrderStatus(((ComandaDao) orderDao).getIdComanda(), 1);
                order.updateOrderAmount(((ComandaDao) orderDao).getIdComanda(), payInCash, payByCard, discount);
                Management.closeTable(((ComandaDao) orderDao).getIdTable());
                new Cashier().setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
                AppFactory.getManageComandaController().refreshTileList();
                AppController.setSellConfirmed(true);
            } else {
                LOGGER.info("Instance of 'Order' Data Access Object");
                orderDao = order.newOrder(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 1,
                        discount, note.toString());
                new Cashier().setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
                order.addProductsToOrder(orderDao.getIdOrder(), items);
                AppFactory.getCashierController().updateCashierElements();
                AppController.setSellConfirmed(true);
            }

            AppFactory.setOrderDao(null);
            wrapperVBox.getScene().getWindow().hide();
        }
    }

    public void receiptContentConstructor() {
        OrderDao orderDao = AppFactory.getOrderDao();
        orderDao.setOrderDate(LocalDate.now());
        orderDao.setOrderTime(LocalTime.now());
        orderDao.setTotal(getSubtotal());
        orderDao.setDiscount(percentageField1.getAmount() * 100);
        orderDao.setIdOrder(new Order().getLastOrderId() + 1);
        AppFactory.setOrderDao(orderDao);
        products = AppFactory.getSelectedProducts();
        AppFactory.setSelectedProducts(products);
    }

    public PopOver viewReceipt() {
        receiptContentConstructor();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.RECEIPT_VIEW));
        VBox node = null;

        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PopOver popOver = new PopOver(node);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);

        if (products.size() >= 15) {
            popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
        }

        return popOver;
    }

    public void setInputFilds() {
        Font font = Font.font("consolas", FontWeight.BOLD, FontPosture.REGULAR, 20);
        double width = 170.0;
        payInCash = new CurrencyField(new Locale("pt", "BR"));
        payInCash.setFont(font);
        payInCash.setPrefWidth(width);
        payInCash.setAmount(getSubtotal());
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
            payInCash.setAmount(getTotal());
            payByCard.setAmount(0.0);
        });

        tc2.setOnMouseClicked(e4 -> {
            payByCard.setAmount(getTotal());
            payInCash.setAmount(0.0);
        });

        cc1.setOnMouseClicked(e5 -> payInCash.setAmount(getTotal() - payByCard.getAmount()));
        cc2.setOnMouseClicked(e5 -> payByCard.setAmount(getTotal() - payInCash.getAmount()));
    }

    public void handleKeyEvent() {
        totalLabel.getParent().requestFocus();
        totalLabel.getScene().getAccelerators().clear();
        totalLabel.getScene().getAccelerators().put(SceneNavigator.F2_CONFIRMATION, this::confirm);
        totalLabel.getScene().getAccelerators().put(SceneNavigator.F4_CANCEL, () -> box1.getScene().getWindow().hide());
    }

    public double getSubtotal() {
        double total = 0;
        for (ProductDao item : AppFactory.getSelectedProducts()) {
            total += item.getSellPrice() * item.getQuantity();
        }
        return total;
    }

    public double getDiscount() {
        return getSubtotal() * percentageField1.getAmount();
    }

    public double getTax() {
        return getSubtotal() * percentageField2.getAmount();
    }

    public double getTotal() {
        double subtotal = getSubtotal();
        double discount = getDiscount();
        double tax = getTax();
        return subtotal + tax - discount;
    }

    public double getChange() {
        double total = getTotal();
        return Math.round(((payInCash.getAmount() + payByCard.getAmount()) - (total))*100)/100.0;
    }
}
