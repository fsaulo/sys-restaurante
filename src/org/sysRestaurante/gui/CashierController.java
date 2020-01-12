package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Cashier;

public class CashierController {

    @FXML
    private BorderPane borderPaneHolder;
    @FXML
    private Label openOrCloseCashierLabel;

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        updateOpenOrCloseCashierLabel();
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
        updateOpenOrCloseCashierLabel();
    }

    public void updateOpenOrCloseCashierLabel() {
        boolean isCashierOpenned = Cashier.getLastCashierStatus();

        if (isCashierOpenned) {
            openOrCloseCashierLabel.setText("Fechar caixa");
        } else {
            openOrCloseCashierLabel.setText("Abrir caixa");
        }
    }
}
