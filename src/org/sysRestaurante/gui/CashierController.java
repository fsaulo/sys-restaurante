package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.sysRestaurante.applet.AppFactory;

public class CashierController {

    @FXML
    private BorderPane borderPaneHolder;

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
    }
}
