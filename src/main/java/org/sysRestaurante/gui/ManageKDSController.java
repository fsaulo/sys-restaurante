package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.ExceptionHandler;

import java.io.IOException;
import java.util.List;

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

    private List<KitchenOrderDao> kitchenTickets;
    public void initialize() {
        AppFactory.setManageKDSController(this);
        kitchenTickets = Order.getKitchenTicketsByCashierId(AppFactory.getCashierDao().getIdCashier());
        tilePane.setPrefColumns(5);
        listKitchenTickets();
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
    }

    public void listKitchenTickets() {
        tilePane.getChildren().clear();
        kitchenTickets = Order.getKitchenTicketsByCashierId(AppFactory.getCashierDao().getIdCashier());
        assert kitchenTickets != null;
        int pending = 0;
        for (var item : kitchenTickets) {
            if (item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.WAITING) ||
                item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.LATE  )
                    || item.getKitchenOrderStatus().equals(KitchenOrderDao.KitchenOrderStatus.COOKING  ) ) {
                pending += 1;

                try {
                    buildAndAddTickets(item);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ExceptionHandler.incrementGlobalExceptionsCount();
                }
            }
        }

        pendingTickets.setText(String.valueOf(pending));
    }

    public void buildAndAddTickets(KitchenOrderDao ticket) throws IOException {
        AppFactory.setKitchenOrderDao(ticket);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.KITCHEN_TICKET_VIEW));
        Parent parent = loader.load();

        KitchenTicketViewController controller = loader.getController();
        controller.setTableLabel("Mesa " + ticket.getIdTable());

        tilePane.getChildren().addAll(parent);
    }
}
