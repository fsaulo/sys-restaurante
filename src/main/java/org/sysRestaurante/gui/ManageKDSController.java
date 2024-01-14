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
import org.sysRestaurante.event.EventBus;
import org.sysRestaurante.event.TicketStatusChangedEvent;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.ExceptionHandler;

import java.io.IOException;
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
    private CheckBox cancelledCheckBox;
    @FXML
    private CheckBox finishedCheckBox;
    @FXML
    private CheckBox pendingCheckBox;

    private ObservableList<KitchenOrderDao> kitchenTickets = FXCollections.observableArrayList();
    private final ObservableList<KitchenOrderDao.KitchenOrderStatus> statusFilter = FXCollections.observableArrayList();

    public void initialize() {
        AppFactory.setManageKDSController(this);
        kitchenTickets = FXCollections.observableArrayList(Order.getKitchenTicketsByCashierId(AppFactory.getCashierDao().getIdCashier()));

        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.COOKING);
        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.WAITING);
        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.LATE);
        statusFilter.add(KitchenOrderDao.KitchenOrderStatus.READY);

        tilePane.setPrefColumns(10);
        listKitchenTickets();

        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());


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

    public void listKitchenTickets() {
        tilePane.getChildren().clear();
        kitchenTickets = FXCollections.observableArrayList(fetchTicketsFromDatabase());
        kitchenTickets.sort(Comparator.comparing(KitchenOrderDao::getKitchenOrderDateTime));
        for (var item : kitchenTickets) {
            boolean isCashierOpen = Cashier.isOpen(item.getIdCashier());
            if (statusFilter.contains(item.getKitchenOrderStatus()) && isCashierOpen) {
                try {
                    buildAndAddTickets(item);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ExceptionHandler.incrementGlobalExceptionsCount();
                }
            }
        }
    }

    public void refreshTicketsTilePane() {
        tilePane.getChildren().clear();
        assert kitchenTickets != null;
        for (var item : kitchenTickets) {
            boolean isCashierOpen = Cashier.isOpen(item.getIdCashier());
            if (statusFilter.contains(item.getKitchenOrderStatus()) && isCashierOpen) {
                try {
                    buildAndAddTickets(item);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ExceptionHandler.incrementGlobalExceptionsCount();
                }
            }
        }
    }

    private List<KitchenOrderDao> fetchTicketsFromDatabase() {
        return new ArrayList<>(Objects.requireNonNull(Order.getKitchenTicketsByCashierId(AppFactory.getCashierDao().getIdCashier())));
    }

    public void buildAndAddTickets(KitchenOrderDao ticket) throws IOException {
        AppFactory.setKitchenOrderDao(ticket);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.KITCHEN_TICKET_VIEW));
        Parent parent = loader.load();

        KitchenTicketViewController controller = loader.getController();
        controller.setTableLabel("Mesa " + ticket.getIdTable());

        EventBus ticketEventBus = controller.getEventBus();
        ticketEventBus.addEventHandler(TicketStatusChangedEvent.TICKET_STATUS_CHANGED_EVENT_EVENT_TYPE, ticketStatusChangedEvent -> {
            System.out.println("TICKET STATUS CHANGED " + ticketStatusChangedEvent.getTicketStatus());
            switch (ticketStatusChangedEvent.getTicketStatus()) {
                case DELIVERED:
                case CANCELLED:
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
