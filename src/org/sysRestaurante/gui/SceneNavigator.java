package org.sysRestaurante.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class SceneNavigator {

    public static final String MAIN = "MainGUI.fxml";
    public static final String LOGIN = "Login.fxml";
    public static final String MENU_TOOL_BAR = "ToolBar.fxml";
    public static final String DASHBOARD = "Dashboard.fxml";
    public static final String APPLICATION_STAGE = "App.fxml";
    public static final String NOTE_PANE = "AddNotesDialog.fxml";
    public static final String CLEAR_NOTES_ALERT = "ClearNotesAlert.fxml";
    public static final String CASHIER = "Cashier.fxml";

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(MainGUIController.class.getName());

    public static MainGUIController mainController;

    public static void setMainGUIController(MainGUIController mainController) {
        SceneNavigator.mainController = mainController;
    }

    public static void loadScene(String fxml) {
        try {
            mainController.setScene(FXMLLoader.load(SceneNavigator.class.getResource(fxml)));
        } catch (IOException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Scene couldn't be loaded.");
            e.printStackTrace();
        }
    }

    public static void loadScene(Node node) {
        mainController.setScene(node);
    }
}
