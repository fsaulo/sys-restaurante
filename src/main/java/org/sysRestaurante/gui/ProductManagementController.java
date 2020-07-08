package org.sysRestaurante.gui;

import javafx.scene.layout.BorderPane;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.util.LoggerHandler;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.logging.Logger;

public class ProductManagementController {

    @FXML
    private VBox addProductButton;
    @FXML
    private BorderPane borderPaneHolder;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(ProductManagementController.class.getName());

    public void initialize() {
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        addProductButton.setOnMouseClicked(mouseEvent -> {
            LOGGER.info("Mouse clicked. Working fine!");
        });
    }
}
