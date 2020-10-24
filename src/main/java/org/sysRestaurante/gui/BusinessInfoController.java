package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;

public class BusinessInfoController {

    public VBox statusCashierBox;
    public Label statusCashierLabel;
    @FXML
    private BorderPane borderPaneHolder;

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
    }
}
