package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import org.sysRestaurante.applet.AppFactory;

public class ManageComandaController {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TilePane tilePane;
    @FXML
    private BorderPane borderPaneHolder;

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        tilePane.setPrefColumns(50);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
    }
}
