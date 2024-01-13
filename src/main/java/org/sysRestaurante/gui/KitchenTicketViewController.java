package org.sysRestaurante.gui;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.KitchenOrderDao;

import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.event.EventBus;
import org.sysRestaurante.event.TicketStatusChangedEvent;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.NotificationHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

    private EventBus eventBus;

    @FXML
    void initialize() {
        this.eventBus = new EventBus();

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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");
        alert.setHeaderText("Tem certeza que deseja cancelar pedido?");
        alert.setContentText("O balcão será notificado dessa ação.");
        alert.initOwner(statusLabel.getParent().getScene().getWindow());
        alert.showAndWait();

        // TODO: check product quantity before removing it from order.
        //       Sometimes the ticket may contain less units than the total count.
        //       In these cases, we want to update the total quantity instead of removing the product.
        if (alert.getResult() == ButtonType.OK && !kitchenOrderDao.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.CANCELLED)) {
            final KitchenOrderDao.KitchenOrderStatus cancelled = KitchenOrderDao.KitchenOrderStatus.CANCELLED;
            final LocalDateTime finalDateTime = LocalDateTime.now();
            int ticketId = kitchenOrderDao.getIdKitchenOrder();
            Order.removeProductFromOrderByKitchenOrderId(ticketId);
            Order.updateKitchenOrderStatusWithDateTime(ticketId, cancelled.getValue(), LocalDateTime.now());
            kitchenOrderDao.setKitchenOrderStatus(cancelled);
            kitchenOrderDao.setFinalKitchenOrderDateTime(finalDateTime);
            NotificationHandler.showInfo("O pedido #" + kitchenOrderDao.getIdKitchenOrder() + " foi cancelado");
            timerLabel.setText(timeBetweenLocalDateTimeAsMinSec(ticketInitialTime, finalDateTime));
            eventBus.fireEvent(new TicketStatusChangedEvent(cancelled));
            refreshTicketStatus();
        }
    }

    private void updateTicketStatus() {
        switch (kitchenOrderDao.getKitchenOrderStatus()) {
            case WAITING:
                final KitchenOrderDao.KitchenOrderStatus cooking = KitchenOrderDao.KitchenOrderStatus.COOKING;
                Order.updateKitchenOrderStatus(kitchenOrderDao.getIdKitchenOrder(), cooking.getValue());
                kitchenOrderDao.setKitchenOrderStatus(cooking);
                confirmButton.setText("Pronto");
                NotificationHandler.showInfo("Pedido recebido pela cozinha\nIniciando preparo");
                eventBus.fireEvent(new TicketStatusChangedEvent(cooking));
                break;
            case READY:
                final KitchenOrderDao.KitchenOrderStatus delivered = KitchenOrderDao.KitchenOrderStatus.DELIVERED;
                final LocalDateTime finalDateTime = LocalDateTime.now();
                Order.updateKitchenOrderStatusWithDateTime(kitchenOrderDao.getIdKitchenOrder(), delivered.getValue(), finalDateTime);
                kitchenOrderDao.setKitchenOrderStatus(delivered);
                kitchenOrderDao.setFinalKitchenOrderDateTime(finalDateTime);
                confirmButton.setDisable(true);
                timerLabel.setText(timeBetweenLocalDateTimeAsMinSec(ticketInitialTime, finalDateTime));
                NotificationHandler.showInfo("Pedido finalizado");
                eventBus.fireEvent(new TicketStatusChangedEvent(delivered));
                break;
            case COOKING:
                final KitchenOrderDao.KitchenOrderStatus ready = KitchenOrderDao.KitchenOrderStatus.READY;
                Order.updateKitchenOrderStatus(kitchenOrderDao.getIdKitchenOrder(), ready.getValue());
                kitchenOrderDao.setKitchenOrderStatus(ready);
                NotificationHandler.showInfo("Pedido pronto");
                confirmButton.setText("Entregue");
                eventBus.fireEvent(new TicketStatusChangedEvent(ready));
                break;
            case CANCELLED:
                eventBus.fireEvent(new TicketStatusChangedEvent());
            case LATE:
            case RETURNED:
            default:
                break;
        }
        refreshTicketStatus();
    }

    private void refreshTicketStatus() {
        boolean shouldBlink = false;

        final String waitingLabelStyle = "-fx-background-color:  #e69900; -fx-background-radius: 5";
        final String lateLabelStyle = "-fx-background-color:  #EF4422FF; -fx-background-radius: 5";
        final String cookingLabelStyle = "-fx-background-color:  #6E7070; -fx-background-radius: 5";
        final String ticketReadyStyle = "-fx-background-color: green; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5";
        final String ticketCancelledStyle = "-fx-background-color: red; -fx-text-fill: white; -fx-background-radius: 5; -fx-border-radius: 5";

        switch (kitchenOrderDao.getKitchenOrderStatus()) {
            case COOKING:
                timerLabel.setOpacity(1.0);
                timerLabel.setStyle(cookingLabelStyle);
                timerLabel.setVisible(true);
                statusLabel.setText("Em preparo");
                confirmButton.setText("Pronto");
                break;
            case WAITING:
                statusLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-family: \"Carlito\";");
                timerLabel.setStyle(waitingLabelStyle);
                timerLabel.setVisible(true);
                statusLabel.setText("Aguardando confirmação");
                shouldBlink = true;
                break;
            case LATE:
                statusLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-family: \"Carlito\";");
                timerLabel.setStyle(lateLabelStyle);
                timerLabel.setVisible(true);
                shouldBlink = true;
                break;
            case READY:
                statusLabel.setText("Aguardando retirada");
                statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: \"Carlito\";");
                tableLabel.setStyle("-fx-text-fill: white");
                orderIdLabel.setStyle("-fx-text-fill: white");
                statusBox.setStyle(ticketReadyStyle);
                break;
            case DELIVERED:
                statusLabel.setText("Finalizado");
                statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: \"Carlito\";");
                tableLabel.setStyle("-fx-text-fill: white");
                orderIdLabel.setStyle("-fx-text-fill: white");
                statusBox.setStyle(ticketReadyStyle);
                detailsText.setOpacity(0.6);
                popOverVbox.setOpacity(0.4);
                popOverVbox.setDisable(true);
                assert kitchenOrderDao.getFinalKitchenOrderDateTime() != null;
                timerLabel.setText(timeBetweenLocalDateTimeAsMinSec(
                        ticketInitialTime,
                        Objects.requireNonNull(kitchenOrderDao.getFinalKitchenOrderDateTime())
                ));
                break;
            case CANCELLED:
                statusLabel.setText("Pedido cancelado");
                statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-family: \"Carlito\";");
                tableLabel.setStyle("-fx-text-fill: white");
                orderIdLabel.setStyle("-fx-text-fill: white");
                statusBox.setStyle(ticketCancelledStyle);
                cancelButton.setDisable(true);
                confirmButton.setDisable(true);
                detailsText.setOpacity(0.6);
                popOverVbox.setOpacity(0.4);
                popOverVbox.setDisable(true);
                break;
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
    }

    public void setTableLabel(String text) {
        tableLabel.setText(text);
    }

    public String timeBetweenLocalDateTimeAsMinSec(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        long seconds = java.time.Duration.between(dateTime1, dateTime2).getSeconds();
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }
}
