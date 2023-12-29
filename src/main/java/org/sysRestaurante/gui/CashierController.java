package org.sysRestaurante.gui;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.gui.formatter.StatusCellFormatter;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.model.Management;
import org.sysRestaurante.model.Order;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.logging.Logger;

public class CashierController {

    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private Label openOrCloseCashierLabel;
    @FXML
    private VBox searchOrderBox;
    @FXML
    private VBox cancelOrderBox;
    @FXML
    private VBox newOrderBox;
    @FXML
    private VBox statusCashierBox;
    @FXML
    private Label statusCashierLabel;
    @FXML
    private TableColumn<OrderDao, Integer> codOrder;
    @FXML
    private TableColumn<OrderDao, String> details;
    @FXML
    private TableColumn<OrderDao, String> status;
    @FXML
    private TableColumn<OrderDao, String> notes;
    @FXML
    private TableColumn<OrderDao, LocalDate> date;
    @FXML
    private TableColumn<OrderDao, Double> total;
    @FXML
    private TableView<OrderDao> orderListTableView;
    @FXML
    private HBox wrapperBoxPicker;
    @FXML
    private VBox wrapperVBox;
    @FXML
    private VBox viewMesaComanda;
    @FXML
    private VBox wrapperBoxPicker1;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(CashierController.class.getName());
    private DatePicker datePicker;
    private Parent detailsBox = null;

    @FXML
    public void initialize() throws IOException {
        AppFactory.setCashierController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        cancelOrderBox.setOnMouseClicked(mouseEvent -> {
            if (orderListTableView.getSelectionModel().getSelectedItem() != null) {
                OrderDao orderDao = orderListTableView.getSelectionModel().getSelectedItem();
                onCancelOrder(orderDao);
            }
        });

        setSearchProperties();
        updateCashierElements();
        handleKeyEvent();

        orderListTableView.setRowFactory(orderDaoTableView -> {
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
            optionDeleteOrder.setOnAction(actionEvent -> {
                onCancelOrder(row.getItem());
                actionEvent.consume();
            });

            contextMenu.getItems().addAll(
                    optionDetailsOrder,
                    optionSeeReceipt,
                    separator,
                    optionDeleteOrder
            );

            row.setOnMouseClicked(e1 -> {
                if (e1.getButton().equals(MouseButton.PRIMARY) && e1.getClickCount() == 2) {
                    AppFactory.setOrderDao(row.getItem());
                    AppController.showDialog(SceneNavigator.ORDER_DETAILS_DIALOG, true);
                }
            });
            row.contextMenuProperty()
                    .bind(Bindings.when(row.emptyProperty().not())
                    .then(contextMenu)
                    .otherwise((ContextMenu) null));

            return row;
        });

        updateTableAndDetailBox();
        resetControllers();
    }

    private void updateTableAndDetailBox() {
        Cashier.getCashierDataAccessObject(AppFactory.getCashierDao().getIdCashier());
        CashierDao cashier = AppFactory.getCashierDao();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.DETAILS_CASHIER_BOX));
        loader.setController(new DetailsCashierBoxController(cashier));

        try {
            if (detailsBox != null) {
                wrapperVBox.getChildren().remove(detailsBox);
            }

            detailsBox = loader.load();
            wrapperVBox.getChildren().add(detailsBox);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        updateOrderTableList();
    }

    private void setSearchProperties() {
        VBox wrapper = new VBox();
        Label dateLabel = new Label("Filtrar pedido");
        TextField codField = new TextField();
        Button buttonSearch = new Button("Pesquisar");
        HBox byCodFilterBox = new HBox();

        datePicker = new DatePicker();
        datePicker.setPrefWidth(220);
        datePicker.setPromptText("Filtrar por data");

        buttonSearch.setOnAction(actionEvent -> {
            try {
                int cod = Integer.parseInt(codField.getText());
                setFilterByCod(cod);
            } catch (NumberFormatException ex) {
                setFilterByDate();
            }
        });

        dateLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        byCodFilterBox.getChildren().addAll(codField, buttonSearch);
        byCodFilterBox.setSpacing(5);
        codField.setPromptText("Código do pedido");
        wrapper.setSpacing(5);
        wrapper.setPadding(new Insets(10));
        wrapper.getChildren().addAll(dateLabel, datePicker, byCodFilterBox);

        PopOver datePopOver = new PopOver(wrapper);
        datePopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        datePopOver.setDetachable(false);
        searchOrderBox.setOnMouseClicked(mouseEvent -> datePopOver.show(wrapperBoxPicker));
    }

    @FXML
    public void onOpenOrCloseCashier() {
        boolean isCashierOpen = Cashier.isOpen();

        if (isCashierOpen) {
            AppController.showDialog(SceneNavigator.CLOSE_CASHIER_DIALOG, true);
            if (!Cashier.isOpen()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informações do sistema");
                alert.setHeaderText("Caixa fechado com sucesso!");
                alert.setContentText("Para realizar novas operações de caixa, será necessário abri-lo novamente.");
                alert.initOwner(borderPaneHolder.getScene().getWindow());
                alert.showAndWait();
            }
        } else {
            AppController.showDialog(SceneNavigator.OPEN_CASHIER_DIALOG, true);
            updateCashierElements();
            if (Cashier.isOpen()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informações do sistema");
                alert.setHeaderText(null);
                alert.setContentText("Caixa aberto com sucesso!");
                alert.initOwner(borderPaneHolder.getScene().getWindow());
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void onNewOrder() {
        newOrderBox.setDisable(true);
        AppController.showPOS();

        if (AppController.isSellConfirmed()) {
            AppController.setSellConfirmed(false);
            NotificationHandler.showInfo("Pedido realizado com sucesso");
        }

        newOrderBox.setDisable(false);
        updateTableAndDetailBox();
    }

    public void onCancelOrder(OrderDao order) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Alerta do sistema");

        try {
            if (order.getStatus().equals("Cancelado")) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Não foi possível cancelar o pedido!");
                alert.setContentText("Esse pedido já foi cancelado.");
                alert.initOwner(borderPaneHolder.getScene().getWindow());
                alert.showAndWait();
                return;
            } else {
                alert.setHeaderText("Tem certeza que deseja cancelar o pedido?");
                alert.setContentText("Essa operação não poderá ser desfeita.");
                alert.initOwner(borderPaneHolder.getScene().getWindow());
                orderListTableView.setDisable(true);
                alert.showAndWait();
            }

            if (alert.getResult() == ButtonType.OK) {
                int idOrder = order.getIdOrder();
                int idCashier = AppFactory.getCashierDao().getIdCashier();
                double total = order.getTotal();

                if (order.getDetails().equals("Pedido em comanda")) {
                    ComandaDao comanda = Order.getComandaByOrderId(order.getIdOrder());
                    int idComanda = Objects.requireNonNull(comanda).getIdComanda();
                    int idTable = comanda.getIdTable();
                    Order.closeComanda(idComanda, total);
                    Order.updateOrderStatus(idComanda, Order.CANCELED);
                    Order.updateOrderAmount(idComanda, total, 0, 0);
                    Management.closeTable(idTable);
                }

                if (order.getStatus().equals("Concluído")) {
                    double inCash = order.getInCash();
                    double byCard = order.getByCard();
                    Cashier.setRevenue(idCashier, -inCash, -byCard, 0);
                }

                Order.cancel(idOrder);
                order.setStatus(Order.CANCELED);
                NotificationHandler.showInfo("Pedido cancelado com sucesso!");
            }
        } catch (NullPointerException ex) {
            LOGGER.warning("Trying to access null object. Probably it was already reloaded in memory.");
        }

        updateTableAndDetailBox();
    }

    public void handleKeyEvent() {
        AppFactory.getMainController().getScene().getAccelerators().clear();
        AppFactory.getAppController().setFullScreenShortcut();
        AppFactory.getMainController().getScene().getAccelerators()
                .put(SceneNavigator.F10_OPEN_OR_CLOSE_CASHIER, this::onOpenOrCloseCashier);
        AppFactory.getMainController().getScene().getAccelerators()
                .put(SceneNavigator.F2_CONFIRMATION, this::onNewOrder);
    }

    public void updateCashierElements() {
        updateCashierStatus();
        updateOrderTableList();
    }

    public void updateCashierStatus() {
        boolean isCashierOpened = Cashier.isOpen();
        AppFactory.setCashierController(this);
        Cashier.getCashierDataAccessObject(AppFactory.getCashierDao().getIdCashier());

        if (isCashierOpened) {
            setDisableCashierOptions(false);
            openOrCloseCashierLabel.setText("Fechar caixa");
            statusCashierLabel.setText("CAIXA LIVRE");
            statusCashierBox.setStyle("-fx-background-color: #58996A; -fx-background-radius: 5");
            statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
            statusCashierBox.getChildren().add(statusCashierLabel);
        } else {
            setDisableCashierOptions(true);
            openOrCloseCashierLabel.setText("Abrir caixa");
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

    public void updateOrderTableList() {
        setFilterByDate();
        codOrder.setCellValueFactory(new PropertyValueFactory<>("idOrder"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        details.setCellValueFactory(new PropertyValueFactory<>("details"));
        date.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        notes.setCellValueFactory(new PropertyValueFactory<>("note"));
        status.setCellFactory(tc -> new StatusCellFormatter());
        date.setCellFactory((CellFormatter<OrderDao, LocalDate>) value -> DateTimeFormatter.ofPattern("dd-MM-yyyy")
                .format(value));
        total.setCellFactory((CellFormatter<OrderDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        orderListTableView.refresh();
        orderListTableView.setDisable(false);
    }

    public void setFilterByDate() {
        int idCashier = AppFactory.getCashierDao().getIdCashier();
        ObservableList<OrderDao> data = Order.getOrderByIdCashier(idCashier);
        assert data != null;
        FilteredList<OrderDao> filteredList = new FilteredList<>(data);
        datePicker.valueProperty().addListener((newValue) ->
                filteredList.setPredicate(orderDao -> {
                    if (newValue == null) {
                        return true;
                    }
                    return orderDao.getOrderDate().isEqual(datePicker.getValue());
                }));

        SortedList<OrderDao> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(orderListTableView.comparatorProperty());
        orderListTableView.setItems(sortedData);
    }

    public void setFilterByCod(Number cod) {
        int idCashier = AppFactory.getCashierDao().getIdCashier();
        ObservableList<OrderDao> data = Order.getOrderByIdCashier(idCashier);
        assert data != null;
        FilteredList<OrderDao> filteredList = new FilteredList<>(data);
        filteredList.setPredicate(orderDao -> {
            if (cod == null) {
                return true;
            }
            return orderDao.getIdOrder() == cod.intValue();
        });

        SortedList<OrderDao> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(orderListTableView.comparatorProperty());
        orderListTableView.setItems(sortedData);
    }

    public void setDisableCashierOptions(boolean status) {
        searchOrderBox.setDisable(status);
        cancelOrderBox.setDisable(status);
        newOrderBox.setDisable(status);
    }

    public void resetControllers() {
        try {
            handleAddComanda();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void handleAddComanda() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SceneNavigator.NEW_COMANDA_DIALOG));
        VBox node = loader.load();
        PopOver popOver = new PopOver(node);
        popOver.arrowLocationProperty().setValue(PopOver.ArrowLocation.RIGHT_TOP);
        popOver.setDetachable(false);
        popOver.setOnHiding(e -> {
            updateTableAndDetailBox();
            resetControllers();
        });
        viewMesaComanda.setOnMouseClicked(e1 -> popOver.show(wrapperBoxPicker1));
    }
}