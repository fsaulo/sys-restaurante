package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;

public class BusinessInfoController {

    @FXML
    public VBox statusCashierBox;
    @FXML
    public Label statusCashierLabel;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public TilePane tilePane;
    @FXML
    private BorderPane borderPaneHolder;

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
    }
}
