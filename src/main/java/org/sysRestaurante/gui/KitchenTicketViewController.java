package org.sysRestaurante.gui;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.KitchenOrderDao;

import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.time.LocalDateTime;

public class KitchenTicketViewController {

    @FXML
    private Button cancelButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Text detailsText;
    @FXML
    private ListView<KitchenOrderDao> itemsListView;
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

    private KitchenOrderDao kitchenOrderDao;
    private LocalDateTime ticketInitialTime;
    private boolean lastStatusRed;
    private FadeTransition blinkStatusAnimationTransition;

    @FXML
    void initialize() {
        kitchenOrderDao = AppFactory.getKitchenOrderDao();
        ticketInitialTime = kitchenOrderDao.getKitchenOrderDateTime();
        confirmButton.setOnAction(mouseEvent -> updateTicketStatus());
        cancelButton.setOnAction(mouseEvent -> showCancelTicketConfirmDialog());

        updateTimerLabel();
        setupAnimations();
        setupTicketTimer();
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
                NotificationHandler.showInfo("Pedido recebido pela cozinha.\nIniciando preparo.");
                break;
            case DELIVERED:
            case CANCELLED:
            case LATE:
            case COOKING:
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

        switch (kitchenOrderDao.getKitchenOrderStatus()) {
            case COOKING:
                timerLabel.setOpacity(1.0);
                timerLabel.setStyle(cookingLabelStyle);
                timerLabel.setVisible(true);
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
            case CANCELLED:
            case DELIVERED:
            default:
                timerLabel.setVisible(false);
                break;
        }

        assert blinkStatusAnimationTransition != null;
        if (!shouldBlink) {
            blinkStatusAnimationTransition.pause();
            return;
        }

        if (!blinkStatusAnimationTransition.getStatus().equals(Animation.Status.RUNNING)) {
            blinkStatusAnimationTransition.play();
        }
    }

    private void updateTimerLabel() {
        LocalDateTime currentTime = LocalDateTime.now();
        long seconds = java.time.Duration.between(ticketInitialTime, currentTime).getSeconds();
        timerLabel.setText(String.valueOf(seconds));
    }

    public void setupAnimations() {
        blinkStatusAnimationTransition = new FadeTransition(Duration.seconds(0.5), timerLabel);
        blinkStatusAnimationTransition.setFromValue(1.0);
        blinkStatusAnimationTransition.setToValue(0.1);
        blinkStatusAnimationTransition.setCycleCount(Animation.INDEFINITE);
        blinkStatusAnimationTransition.setAutoReverse(true);
    }

    public void setTableLabel(String text) {
        tableLabel.setText(text);
    }
}
