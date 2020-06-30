package org.sysRestaurante.gui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.StatusCellFormatter;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashierHistoryController {

    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private TableView<CashierDao> orderListTableView;
    @FXML
    private TableColumn<CashierDao, Integer> codCashier;
    @FXML
    private TableColumn<CashierDao, Double> revenue;
    @FXML
    private TableColumn<CashierDao, Double> revenueCash;
    @FXML
    private TableColumn<CashierDao, Double> revenueCard;
    @FXML
    private TableColumn<CashierDao, String> notes;
    @FXML
    private TableColumn<CashierDao, Double> withdrawals;
    @FXML
    private TableColumn<CashierDao, LocalDateTime> dateOpenning;
    @FXML
    private TableColumn<CashierDao, LocalDateTime> dateClosing;
    @FXML
    private VBox searchOrderBox;
    @FXML
    private VBox cashierDateDetailsBox;
    @FXML
    private VBox openOrCloseCashierButton;
    @FXML
    private Label openOrCloseCashierLabel;
    @FXML
    private VBox newOrderBox;
    @FXML
    private VBox cancelOrderBox;
    @FXML
    private HBox wrapperBoxPicker;
    @FXML
    private Label openOrCloseCashierLabel11;
    @FXML
    private Label openOrCloseCashierLabel21;
    @FXML
    private Label openOrCloseCashierLabel1;
    @FXML
    private Label openOrCloseCashierLabel2;
    @FXML
    private VBox statusCashierBox;
    @FXML
    private Label statusCashierLabel;
    @FXML
    private TabPane tabPane;

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());

        ObservableList<CashierDao> items = FXCollections.observableList(Cashier.getCashier());
        FXCollections.reverse(items);

        orderListTableView.setItems(items);
        codCashier.setCellValueFactory(new PropertyValueFactory<>("idCashier"));
        revenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        revenueCash.setCellValueFactory(new PropertyValueFactory<>("inCash"));
        revenueCard.setCellValueFactory(new PropertyValueFactory<>("byCard"));
        withdrawals.setCellValueFactory(new PropertyValueFactory<>("withdrawal"));
        dateClosing.setCellValueFactory(new PropertyValueFactory<>("dateTimeClosing"));
        dateOpenning.setCellValueFactory(new PropertyValueFactory<>("dateTimeOpening"));
        notes.setCellValueFactory(new PropertyValueFactory<>("note"));
        dateClosing.setCellFactory((CellFormatter<CashierDao, LocalDateTime>) value -> DateTimeFormatter
                        .ofPattern("dd/MM/yyyy H:m:ss")
                        .format(value));
        dateOpenning.setCellFactory((CellFormatter<CashierDao, LocalDateTime>) value -> DateTimeFormatter
                        .ofPattern("dd/MM/yyyy H:m:ss")
                        .format(value));
        revenue.setCellFactory((CellFormatter<CashierDao, Double>) value -> CurrencyField
                .getBRLCurrencyFormat()
                .format(value));
        revenueCash.setCellFactory((CellFormatter<CashierDao, Double>) value -> CurrencyField
                .getBRLCurrencyFormat()
                .format(value));
        revenueCard.setCellFactory((CellFormatter<CashierDao, Double>) value -> CurrencyField
                .getBRLCurrencyFormat()
                .format(value));
        withdrawals.setCellFactory((CellFormatter<CashierDao, Double>) value -> CurrencyField
                .getBRLCurrencyFormat()
                .format(value));

        searchOrderBox.setOnMouseClicked(e -> {
            ObservableList<OrderDao> orderDetails = null;
            CashierDao cashier = null;

            try {
                cashier = orderListTableView.getSelectionModel().getSelectedItem();
                orderDetails = FXCollections.observableList(Order.getOrderByIdCashier(cashier.getIdCashier()));
            } catch (Exception ignored) {
                ExceptionHandler.doNothing();
            }

            Tab newTab = new Tab("Caixa: " + DateTimeFormatter
                    .ofPattern("dd/MM/yyyy")
                    .format(cashier.getDateOpening()));

            tabPane.getTabs().add(newTab);
            tabPane.getSelectionModel().select(newTab);

            TableView<OrderDao> tableOrderDetails = new TableView<>();
            TableColumn<OrderDao, Integer> codOrder = new TableColumn<>("Cod.");
            TableColumn<OrderDao, String> details = new TableColumn<>("Detalhes");
            TableColumn<OrderDao, String> status = new TableColumn<>("Status");
            TableColumn<OrderDao, String> notes = new TableColumn<>("Observação");
            TableColumn<OrderDao, LocalDate> date = new TableColumn<>("Data");
            TableColumn<OrderDao, Double> total = new TableColumn<>("Total");

            tableOrderDetails.setRowFactory(orderDaoTableView -> {
                final TableRow<OrderDao> row = new TableRow<>();
                final ContextMenu contextMenu = new ContextMenu();

                SeparatorMenuItem separator = new SeparatorMenuItem();
                MenuItem optionDeleteOrder = new MenuItem("Cancelar pedido");
                MenuItem optionDetailsOrder = new MenuItem("Detalhes");
                MenuItem optionSeeReceipt = new MenuItem("Recibo");

                optionDetailsOrder.setOnAction(actionEvent -> {
                    AppFactory.setOrderDao(row.getItem());
                    AppController.showDialog(SceneNavigator.ORDER_DETAILS_DIALOG, true);
                });

                optionSeeReceipt.setDisable(true);
                optionDeleteOrder.setDisable(true);
                contextMenu.getItems().addAll(optionDetailsOrder, optionSeeReceipt, separator, optionDeleteOrder);

                row.contextMenuProperty()
                        .bind(Bindings
                        .when(row
                        .emptyProperty()
                        .not())
                        .then(contextMenu)
                        .otherwise((ContextMenu) null));

                return row;
            });

            tableOrderDetails.getColumns().addAll(codOrder, details, total, notes, status, date);
            tableOrderDetails.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableOrderDetails.setPlaceholder(new Label("Nenhum registro"));
            tableOrderDetails.setItems(orderDetails);

            codOrder.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
            total.setCellValueFactory(new PropertyValueFactory<>("total"));
            details.setCellValueFactory(new PropertyValueFactory<>("details"));
            date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
            status.setCellValueFactory(new PropertyValueFactory<>("status"));
            notes.setCellValueFactory(new PropertyValueFactory<>("note"));
            status.setCellFactory(tc -> new StatusCellFormatter());
            date.setCellFactory((CellFormatter<OrderDao, LocalDate>) value -> DateTimeFormatter
                    .ofPattern("dd-MM-yyyy")
                    .format(value));
            total.setCellFactory((CellFormatter<OrderDao, Double>) value -> CurrencyField
                    .getBRLCurrencyFormat()
                    .format(value));

            Parent detailsBox = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.DETAILS_CASHIER_BOX));
            loader.setController(new DetailsCashierBoxController(cashier));

            try {
                detailsBox = loader.load();
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            VBox wrapper = new VBox();
            wrapper.getChildren().addAll(tableOrderDetails, detailsBox);
            wrapper.setVgrow(tableOrderDetails, Priority.ALWAYS);
            wrapper.setPadding(new Insets(5,0,0,0));
            wrapper.setSpacing(5);
            newTab.setContent(wrapper);
        });
    }
}
