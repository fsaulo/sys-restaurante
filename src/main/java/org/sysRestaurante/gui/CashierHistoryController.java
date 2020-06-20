package org.sysRestaurante.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.gui.formatter.CellFormatter;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Cashier;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CashierHistoryController {

    @FXML
    private GridPane gridWrapper;
    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private TableView<CashierDao> orderListTableView;
    @FXML
    private TableColumn<CashierDao, Integer> codCashier;
    @FXML
    private TableColumn<CashierDao, Double> revenue;
    @FXML
    private TableColumn<CashierDao, Double> withdrawals;
    @FXML
    private TableColumn<CashierDao, LocalDateTime> dateOpenning;
    @FXML
    private TableColumn<CashierDao, LocalDateTime> dateClosing;
    @FXML
    private VBox cashierDateDetailsBox;
    @FXML
    private Label withdrawalLabel;
    @FXML
    private Label inCashLabel;
    @FXML
    private Label byCardLabel;
    @FXML
    private Label revenueLabel;
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
    private VBox searchOrderBox;
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

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        ObservableList<CashierDao> items = FXCollections.observableList(Cashier.getCashier());
        FXCollections.reverse(items);
        orderListTableView.setItems(items);
        codCashier.setCellValueFactory(new PropertyValueFactory<>("idCashier"));
        revenue.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        withdrawals.setCellValueFactory(new PropertyValueFactory<>("withdrawal"));
        dateClosing.setCellValueFactory(new PropertyValueFactory<>("dateTimeClosing"));
        dateOpenning.setCellValueFactory(new PropertyValueFactory<>("dateTimeOpening"));
        dateClosing.setCellFactory((CellFormatter<CashierDao, LocalDateTime>) value ->
                DateTimeFormatter.ofPattern("dd/MM/yyyy H:m:ss").format(value));
        dateOpenning.setCellFactory((CellFormatter<CashierDao, LocalDateTime>) value ->
                DateTimeFormatter.ofPattern("dd/MM/yyyy H:m:ss").format(value));
        revenue.setCellFactory((CellFormatter<CashierDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
        withdrawals.setCellFactory((CellFormatter<CashierDao, Double>) value -> CurrencyField.getBRLCurrencyFormat()
                .format(value));
    }
}
