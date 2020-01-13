package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.DateFormatter;

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
    private VBox cashierDateDetailsBox;

    public void initialize() {
        AppFactory.setCashierController(this);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        updateCashierElements();
    }

    public void onOpenOrCloseCashierClicked(MouseEvent event) {
        boolean isCashierOpenned = Cashier.getLastCashierStatus();
        Cashier cashier = new Cashier();

        if (isCashierOpenned) {
            cashier.close(AppFactory.getCashierDao().getIdCashier(), 0);
        } else {
            cashier.open(AppFactory.getUserDao().getIdUsuario());
        }

        event.consume();
        updateCashierElements();
    }

    public void updateCashierElements() {
        boolean isCashierOpenned = Cashier.getLastCashierStatus();

        if (isCashierOpenned) {
            setDisableCashierOptions(false);
            openOrCloseCashierLabel.setText("Fechar caixa");
            statusCashierLabel.setText("CAIXA LIVRE");
            statusCashierBox.setStyle("-fx-background-color: #58996A; -fx-background-radius: 5");
            statusCashierBox.getChildren().removeAll(statusCashierBox.getChildren());
            statusCashierBox.getChildren().add(statusCashierLabel);
            changeCashierDetails(true);
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
            changeCashierDetails(false);
        }
    }

    public void setDisableCashierOptions(boolean status) {
        searchOrderBox.setDisable(status);
        newOrderBox.setDisable(status);
        cancelOrderBox.setDisable(status);
    }

    public void changeCashierDetails(boolean isCashierOpenned) {
        cashierDateDetailsBox.getChildren().removeAll(cashierDateDetailsBox.getChildren());

        if (isCashierOpenned) {
            Label message = new Label("Caixa aberto em");
            Label date = new Label();
            message.setStyle("-fx-font-family: carlito; -fx-font-size: 15; -fx-font-weight: bold");
            date.setStyle("-fx-font-family: carlito; -fx-font-size: 15; -fx-font-weight: bold");
            date.setText(DateFormatter
                    .TIME_DETAILS_FORMAT
                    .format(Cashier.getCashierDateTimeDetailsById(AppFactory.getCashierDao().getIdCashier())));
            cashierDateDetailsBox.getChildren().addAll(message, date);
        } else {
            Label message = new Label("Caixa está fechado");
            message.setStyle("-fx-font-family: carlito; -fx-font-size: 15; -fx-font-weight: bold");
            cashierDateDetailsBox.getChildren().add(message);
        }
    }
}
