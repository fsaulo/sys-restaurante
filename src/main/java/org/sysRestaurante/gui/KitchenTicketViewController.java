package org.sysRestaurante.gui;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.KitchenOrderDao;

import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.NotificationHandler;

import java.time.LocalDateTime;
import java.util.List;

public class KitchenTicketViewController {

    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Text detailsText;
    @FXML
    private Label productsListLabel;
    @FXML
    private Label orderIdLabel;
    @FXML
    private VBox popOverVbox;
    @FXML
    private Label statusLabel;
    @FXML
    private Label tableLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private HBox statusBox;

    private KitchenOrderDao kitchenOrderDao;
    private LocalDateTime ticketInitialTime;
    private boolean lastStatusRed;
    private FadeTransition blinkStatusAnimationTransition;
    private FadeTransition blinkConfirmButtonAnimationTransition;

    @FXML
    void initialize() {
        kitchenOrderDao = AppFactory.getKitchenOrderDao();
        ticketInitialTime = kitchenOrderDao.getKitchenOrderDateTime();
        confirmButton.setOnAction(mouseEvent -> updateTicketStatus());
        cancelButton.setOnAction(mouseEvent -> showCancelTicketConfirmDialog());
        orderIdLabel.setText("#" + kitchenOrderDao.getIdKitchenOrder());

        setupTicketDetails();
        updateTimerLabel();
        setupAnimations();
        setupTicketTimer();
        refreshTicketStatus();
    }

    private void setupTicketDetails() {
        List<ProductDao> productDaoList = Order.getTicketProductsById(kitchenOrderDao.getIdKitchenOrder());

        assert productDaoList != null;
        assert !productDaoList.isEmpty();

        StringBuilder productDescription = new StringBuilder();
        for (var item : productDaoList) {
            productDescription.append(item.getQuantity()).append("x ");
            productDescription.append(item.getDescription()).append("\n");
        }

        productsListLabel.setText(productDescription.toString());
        detailsText.setText(kitchenOrderDao.getKitchenOrderDetails());
    }

    private void setupTicketTimer() {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {
                updateTimerLabel();
                refreshTicketStatus();
            })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void showCancelTicketConfirmDialog() {
        System.out.println("NOT IMPLEMENTED");
    }

    private void updateTicketStatus() {
        switch (kitchenOrderDao.getKitchenOrderStatus()) {
            case WAITING:
                final KitchenOrderDao.KitchenOrderStatus cooking = KitchenOrderDao.KitchenOrderStatus.COOKING;
                Order.updateKitchenOrderStatus(kitchenOrderDao.getIdKitchenOrder(), cooking.getValue());
                kitchenOrderDao.setKitchenOrderStatus(cooking);
                confirmButton.setText("Pronto");
                NotificationHandler.showInfo("Pedido recebido pela cozinha.\nIniciando preparo.");
                break;
            case DELIVERED:
            case CANCELLED:
            case LATE:
            case COOKING:
                final KitchenOrderDao.KitchenOrderStatus ready = KitchenOrderDao.KitchenOrderStatus.READY;
                Order.updateKitchenOrderStatus(kitchenOrderDao.getIdKitchenOrder(), ready.getValue());
                kitchenOrderDao.setKitchenOrderStatus(ready);
                NotificationHandler.showInfo("Pedido pronto");
                confirmButton.setText("Entregue");
                break;
            case RETURNED:
            default:
                break;
        }
    }

    private void refreshTicketStatus() {
        boolean shouldBlink = false;

        final String waitingLabelStyle = "-fx-background-color:  #e69900; -fx-background-radius: 5";
        final String lateLabelStyle = "-fx-background-color:  #EF4422FF; -fx-background-radius: 5";
        final String cookingLabelStyle = "-fx-background-color:  #6E7070; -fx-background-radius: 5";
        final String ticketReadyStyle = "-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5";

        switch (kitchenOrderDao.getKitchenOrderStatus()) {
            case COOKING:
                timerLabel.setOpacity(1.0);
                timerLabel.setStyle(cookingLabelStyle);
                timerLabel.setVisible(true);
                statusLabel.setText("Em preparo");
                confirmButton.setText("Pronto");
                break;
            case WAITING:
                timerLabel.setStyle(waitingLabelStyle);
                timerLabel.setVisible(true);
                shouldBlink = true;
                break;
            case LATE:
                timerLabel.setStyle(lateLabelStyle);
                timerLabel.setVisible(true);
                shouldBlink = true;
                break;
            case READY:
                statusLabel.setText("Aguardando retirada");
                statusLabel.getStylesheets().add(ticketReadyStyle);
                tableLabel.getStylesheets().add(ticketReadyStyle);
                tableLabel.getStylesheets().add(ticketReadyStyle);
                statusBox.setStyle(ticketReadyStyle);
            case CANCELLED:
            case DELIVERED:
            default:
                timerLabel.setVisible(false);
                break;
        }

        assert blinkStatusAnimationTransition != null;
        if (!shouldBlink) {
            blinkStatusAnimationTransition.pause();
            blinkConfirmButtonAnimationTransition.pause();
            return;
        }

        if (!blinkStatusAnimationTransition.getStatus().equals(Animation.Status.RUNNING)) {
            blinkStatusAnimationTransition.play();
        }

        if (!blinkConfirmButtonAnimationTransition.getStatus().equals(Animation.Status.RUNNING)) {
            blinkConfirmButtonAnimationTransition.play();
        }
    }

    private void updateTimerLabel() {
        LocalDateTime currentTime = LocalDateTime.now();
        long seconds = java.time.Duration.between(ticketInitialTime, currentTime).getSeconds();
        long minutes = seconds/60;
        long remainingSeconds = seconds%60;
        timerLabel.setText(String.format("%02d:%02d", minutes, remainingSeconds));
    }

    public void setupAnimations() {
        blinkStatusAnimationTransition = new FadeTransition(Duration.seconds(0.5), timerLabel);
        blinkStatusAnimationTransition.setFromValue(1.0);
        blinkStatusAnimationTransition.setToValue(0.1);
        blinkStatusAnimationTransition.setCycleCount(Animation.INDEFINITE);
        blinkStatusAnimationTransition.setAutoReverse(true);

        blinkConfirmButtonAnimationTransition = new FadeTransition(Duration.seconds(1), confirmButton);
        blinkConfirmButtonAnimationTransition.setFromValue(1.0);
        blinkConfirmButtonAnimationTransition.setToValue(0.1);
        blinkConfirmButtonAnimationTransition.setCycleCount(2);
        blinkConfirmButtonAnimationTransition.setAutoReverse(true);
    }

    public void setTableLabel(String text) {
        tableLabel.setText(text);
    }
}
