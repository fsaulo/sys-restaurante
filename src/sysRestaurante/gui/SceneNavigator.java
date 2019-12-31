package sysRestaurante.gui;

import javafx.fxml.FXMLLoader;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class SceneNavigator {

    public static final String MAIN = "mainGUI.fxml";
    public static final String LOGIN = "loginPage.fxml";
    public static final String MENU = "menuPage.fxml";
    public static final String MENU_TOOL_BAR = "menuToolBar.fxml";

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
}
