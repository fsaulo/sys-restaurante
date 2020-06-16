package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;

public class CashierHistoryController {

    @FXML
    private GridPane gridWrapper;
    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private TableView<?> orderListTableView;
    @FXML
    private TableColumn<?, ?> codOrder;
    @FXML
    private TableColumn<?, ?> revenue;
    @FXML
    private TableColumn<?, ?> withdrawals;
    @FXML
    private TableColumn<?, ?> dateOpenning;
    @FXML
    private TableColumn<?, ?> dateClosing;
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
    }
}
