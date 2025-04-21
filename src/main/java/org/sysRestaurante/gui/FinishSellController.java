package org.sysRestaurante.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.model.Receipt;
import org.sysRestaurante.util.PercentageField;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.Format;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class FinishSellController {

    public ToggleGroup discountGroup;
    public ToggleGroup taxGroup;
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
    private Label codOrderLabel;
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

    private CurrencyField payInCash;
    private CurrencyField payByCard;
    private PercentageField percentageField1;
    private PercentageField percentageField2;
    private final OrderDao order = AppFactory.getOrderDao();
    private static final String GREEN = "#4a8d2c";

    @FXML
    public void initialize() {
        assert(order != null);

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
        buildReceiptContent();

        PopOver popOver = viewReceipt();
        final Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200)));
        timeline.setOnFinished(finishEvent -> {
            if (seeReceiptBox.isHover() || popOver.getContentNode().isHover()) {
                timeline.play();
            } else popOver.hide();
        });

        seeReceiptBox.setOnMouseEntered(event -> {
            if (!popOver.isShowing()) popOver.show(seeReceiptBox, -5);
        });
        seeReceiptBox.setOnMouseExited(event -> timeline.play());

        codOrderLabel.setText(Integer.toString(AppFactory.getOrderDao().getIdOrder()));
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
            buildReceiptContent();
            Receipt receipt = new Receipt(AppFactory.getOrderDao(), AppFactory.getSelectedProducts());
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

    public void buildReceiptContent() {
        OrderDao orderDao = AppFactory.getOrderDao();
        assert (orderDao != null);
        if (!(orderDao instanceof ComandaDao)) {
            orderDao.setIdOrder(Order.getLastOrderId() + 1);
        }
        order.setOrderDate(LocalDate.now());
        order.setOrderTime(LocalTime.now());
        order.setTotal(getSubtotal());
        order.setDiscount(percentageField1.getAmount() * getSubtotal());
        order.setTaxes(percentageField2.getAmount() * getSubtotal());
        AppFactory.setOrderDao(order);
    }

    public PopOver viewReceipt() {
        buildReceiptContent();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.RECEIPT_VIEW));
        VBox node = null;

        try {
            node = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PopOver popOver = new PopOver(node);
        popOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_RIGHT);
        popOver.setHideOnEscape(true);
        ArrayList<ProductDao> products = AppFactory.getSelectedProducts();

        if (products.size() >= 15) {
            popOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_CENTER);
        }

        return popOver;
    }

    public void confirm() {
        ArrayList<ProductDao> items = AppFactory.getSelectedProducts();

        double discount = this.percentageField1.getAmount() * getSubtotal();
        double taxes = this.percentageField2.getAmount() * getSubtotal();
        double change = getChange();

        StringBuilder note = new StringBuilder();
        note.append((discount > 0) ? "Descontos aplicados: " + (int) (100 * percentageField1.getAmount()) + "%" : "");

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
                int idOrder = Objects.requireNonNull(orderDao).getIdOrder();
                int idComanda = ((ComandaDao) orderDao).getIdComanda();
                int idTable = ((ComandaDao) orderDao).getIdTable();

                Order.closeComanda(idComanda, payByCard + payInCash);
                Order.addProductsToOrder(orderDao.getIdOrder(), items);
                Order.updateOrderStatus(idComanda, 1);
                Order.updateOrderAmount(idComanda, payInCash, payByCard, discount);
                Order.setDiscounts(idOrder, discount);
                Order.setTaxes(idOrder, taxes);

                tryToClosePendingTickets(idComanda);

                Management.closeTable(idTable);
                Cashier.setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);
                AppFactory.getManageComandaController().update();
            } else {
                Cashier.setRevenue(AppFactory.getCashierDao().getIdCashier(), payInCash, payByCard, 0);

                orderDao = Order.newOrder(
                        AppFactory.getCashierDao().getIdUser(),
                        AppFactory.getCashierDao().getIdCashier(),
                        payInCash,
                        payByCard,
                        1,
                        discount,
                        taxes,
                        note.toString());

                Order.addProductsToOrder(Objects.requireNonNull(orderDao).getIdOrder(), items);
                AppFactory.getCashierController().updateCashierElements();
            }

            AppController.setSellConfirmed(true);
            AppFactory.setOrderDao(null);
            wrapperVBox.getScene().getWindow().hide();
        }
    }

    public void tryToClosePendingTickets(int idComanda) {
        ArrayList<KitchenOrderDao> tickets = Order.getKitchenTicketsByComandaId(idComanda);
        assert tickets != null;
        for (var ticket : tickets) {
            if (ticket.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.CANCELLED) ||
                    ticket.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.DELIVERED) ||
                    ticket.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.RETURNED)) {
                return;
            }

            Order.updateKitchenOrderStatusWithDateTime(
                    ticket.getIdKitchenOrder(),
                    KitchenOrderDao.KitchenOrderStatus.DELIVERED.getValue(),
                    LocalDateTime.now()
            );
        }
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
		return getSubtotal() + getTax() - getDiscount();
    }

    public double getChange() {
        return Math.round(((payInCash.getAmount() + payByCard.getAmount()) - (getTotal()))*100)/100.0;
    }
}
