package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.event.EventBus;
import org.sysRestaurante.event.TicketStatusChangedEvent;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ManageKDSController {

    @FXML
    public VBox statusCashierBox;
    @FXML
    public Label statusCashierLabel;
    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private TilePane tilePane;
    @FXML
    private Label pendingTickets;
    @FXML
    private Label deliveredTickets;
    @FXML
    private Label cancelledTickets;
    @FXML
    private CheckBox cancelledCheckBox;
    @FXML
    private Label averageTime;
    @FXML
    private CheckBox finishedCheckBox;
    @FXML
    private CheckBox pendingCheckBox;
    @FXML
    private VBox updateVBox;

    private ObservableList<KitchenOrderDao> kitchenTickets = FXCollections.observableArrayList();
    private final ObservableList<KitchenOrderDao.KitchenOrderStatus> statusFilter = FXCollections.observableArrayList();

    public void initialize() {
        AppFactory.setManageKDSController(this);

        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.COOKING);
        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.WAITING);
        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.LATE);
        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.READY);

        tilePane.setPrefColumns(10);

        kitchenTickets = FXCollections.observableArrayList(fetchTicketsFromDatabase());
        kitchenTickets.sort(Comparator.comparing(KitchenOrderDao::getKitchenOrderDateTime));

        refreshTicketsTilePane();

        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        updateVBox.setOnMouseClicked(mouseEvent -> refreshTicketsTilePane());

        cancelledCheckBox.setOnAction(actionEvent -> {
            if (cancelledCheckBox.isSelected()) {
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.CANCELLED);
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.RETURNED);
            } else {
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.CANCELLED);
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.RETURNED);
            }
            kitchenTickets.sort(Comparator.comparing(KitchenOrderDao::getKitchenOrderDateTime).reversed());
            refreshTicketsTilePane();
        });

        pendingCheckBox.setOnAction(actionEvent -> {
            if (pendingCheckBox.isSelected()) {
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.WAITING);
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.COOKING);
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.LATE);
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.READY);
            } else {
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.WAITING);
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.COOKING);
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.LATE);
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.READY);
            }
            kitchenTickets.sort(Comparator.comparing(KitchenOrderDao::getKitchenOrderDateTime));
            refreshTicketsTilePane();
        });

        finishedCheckBox.setOnAction(actionEvent -> {
            if (finishedCheckBox.isSelected()) {
                statusFilter.add(KitchenOrderDao.KitchenOrderStatus.DELIVERED);
            } else {
                statusFilter.remove(KitchenOrderDao.KitchenOrderStatus.DELIVERED);
            }
            kitchenTickets.sort(Comparator.comparing(KitchenOrderDao::getKitchenOrderDateTime).reversed());
            refreshTicketsTilePane();
        });
    }

    public void refreshTicketsTilePane() {
        tilePane.getChildren().clear();
        assert kitchenTickets != null;

        int totalCount = 0;
        int cancelledCount = 0;
        int pendingCount = 0;
        int deliveredCount = 0;
        long timeElapsedCount = 0;
        LocalDateTime currentTime = LocalDateTime.now();

        for (var item : kitchenTickets) {
            boolean isCashierOpen = Cashier.isOpen(item.getIdCashier());
            if (!isCashierOpen) {
                return;
            }

            if (statusFilter.contains(item.getKitchenOrderStatus())) {
                try {
                    buildAndAddTickets(item);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ExceptionHandler.incrementGlobalExceptionsCount();
                }
            }

            if (item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.CANCELLED) ||
                item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.RETURNED)) {
                cancelledCount += 1;
            }

            if (item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.COOKING) ||
                item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.WAITING) ||
                item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.LATE)) {
                pendingCount += 1;
                totalCount += 1;
                timeElapsedCount += java.time.Duration.between(item.getKitchenOrderDateTime(), currentTime).getSeconds();
            }

            if (item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.DELIVERED)) {
                deliveredCount += 1;
                totalCount += 1;
                timeElapsedCount += java.time.Duration.between(item.getKitchenOrderDateTime(), item.getFinalKitchenOrderDateTime()).getSeconds();
            }
        }

        deliveredTickets.setText(Integer.toString(deliveredCount));
        pendingTickets.setText(Integer.toString(pendingCount));
        cancelledTickets.setText(Integer.toString(cancelledCount));
        totalCount = totalCount == 0 ? 1 : totalCount;
        averageTime.setText((int) (timeElapsedCount / 60) / totalCount + " minutos");
    }

    private List<KitchenOrderDao> fetchTicketsFromDatabase() {
        return new ArrayList<>(Objects.requireNonNull(Order.getKitchenTicketsByCashierId(AppFactory.getCashierDao().getIdCashier())));
    }

    private void sendCancelledTicketToKitchen(KitchenOrderDao ticket) {
        OrderDao order = Objects.requireNonNull(Order.getComandaByOrderId(ticket.getIdOrder()));
        ticket.setCustomerName(order.getCustomerName());
        ticket.setIdOrder(order.getIdOrder());
        ticket.setIdComanda(((ComandaDao) order).getIdComanda());
        ticket.setIdEmployee(((ComandaDao) order).getIdEmployee());
        ticket.setIdTable(((ComandaDao) order).getIdTable());

        AppController.printKitchenTicket(ticket, new ProductDao());
    }

    public void buildAndAddTickets(KitchenOrderDao ticket) throws IOException {
        AppFactory.setKitchenOrderDao(ticket);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.KITCHEN_TICKET_VIEW));
        Parent parent = loader.load();

        KitchenTicketViewController controller = loader.getController();
        controller.setTableLabel("Mesa " + ticket.getIdTable());

        EventBus ticketEventBus = controller.getEventBus();
        ticketEventBus.addEventHandler(TicketStatusChangedEvent.TICKET_STATUS_CHANGED_EVENT_EVENT_TYPE, ticketStatusChangedEvent -> {
            switch (ticketStatusChangedEvent.getTicketStatus()) {
                case DELIVERED:
                case CANCELLED:
                    sendCancelledTicketToKitchen(ticket);
                    refreshTicketsTilePane();
                    break;
                case RETURNED:
                case COOKING:
                case LATE:
                case WAITING:
                case READY:
                default:
                    break;
            }
        });

        tilePane.getChildren().addAll(parent);
    }
}
