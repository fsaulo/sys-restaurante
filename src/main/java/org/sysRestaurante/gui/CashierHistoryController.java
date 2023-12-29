package org.sysRestaurante.gui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
import java.util.Objects;

@SuppressWarnings("unchecked")
public class CashierHistoryController {

    @FXML
    public Label openOrCloseCashierLabel1;
    @FXML
    public Label openOrCloseCashierLabel2;
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
    private VBox statusCashierBox;
    @FXML
    private Label statusCashierLabel;
    @FXML
    private TabPane tabPane;

    public void initialize() {
        AppFactory.setCashierHistoryController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());

        ObservableList<CashierDao> items = FXCollections.observableList(Objects.requireNonNull(Cashier.getCashier()));
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

        orderListTableView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                expandTabWithOrderDetails(event);
            }
        });

        searchOrderBox.setOnMouseClicked(e -> {
            if (orderListTableView.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alerta do sistema");
                alert.setHeaderText("Nenhum registro selecionado!");
                alert.setContentText("Por favor, selecione um registro na lista para continuar.");
                alert.initOwner(AppFactory.getMainController().getScene().getWindow());
                alert.showAndWait();
                return;
            }
            expandTabWithOrderDetails(e);
        });

        updateCashierStatus();
    }

    public void updateCashierStatus() {
        boolean isCashierOpenned = Cashier.isOpen();
        Cashier.getCashierDataAccessObject(AppFactory.getCashierDao().getIdCashier());

        if (isCashierOpenned) {
            statusCashierLabel.setText("CAIXA LIVRE");
            statusCashierBox.setStyle("-fx-background-color: #58996A; -fx-background-radius: 5");
            statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
            statusCashierBox.getChildren().add(statusCashierLabel);
        } else {
            Label statusMessage = new Label("Use o atalho F10 para abrir o caixa");
            statusCashierLabel.setText("CAIXA FECHADO");
            statusCashierBox.setStyle("-fx-background-color: #bababa; -fx-background-radius: 5");
            statusCashierBox.getChildren().add(statusMessage);
            statusMessage.setStyle("-fx-font-family: carlito; " +
                    "-fx-font-size: 15; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-style: italic");
            statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
            statusCashierBox.getChildren().addAll(statusCashierLabel, statusMessage);
        }
    }

    public void buildTableOrderDetails(CashierDao cashier) {
        ObservableList<OrderDao> orderDetails = null;

        try {
            orderDetails = FXCollections.observableList(Objects.requireNonNull(Order.getOrderByIdCashier(cashier.getIdCashier())));
        } catch (Exception ignored) {
            ExceptionHandler.doNothing();
        }

        Tab newTab = new Tab("Caixa " + cashier.getIdCashier() + ": " + DateTimeFormatter
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

            row.setOnMouseClicked(e1 -> {
                if (e1.getButton().equals(MouseButton.PRIMARY) && e1.getClickCount() == 2) {
                    AppFactory.setOrderDao(row.getItem());
                    AppController.showDialog(SceneNavigator.ORDER_DETAILS_DIALOG, true);
                }
            });

            row.contextMenuProperty().bind(Bindings
                            .when(row.emptyProperty().not())
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
        VBox.setVgrow(tableOrderDetails, Priority.ALWAYS);
        wrapper.setPadding(new Insets(5,0,0,0));
        wrapper.setSpacing(5);
        newTab.setContent(wrapper);
    }

    private void expandTabWithOrderDetails(Event e1) {
        try {
            CashierDao cashier;
            cashier = orderListTableView.getSelectionModel().getSelectedItem();
            orderListTableView.getSelectionModel().clearSelection();
            buildTableOrderDetails(cashier);
        } catch (Exception ignored) {
            ExceptionHandler.doNothing();
        } finally {
            e1.consume();
        }
    }
}
