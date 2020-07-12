package org.sysRestaurante.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.util.*;

import java.io.IOException;
import java.util.logging.Logger;

public class MainGUI extends Application {

    private static final Logger LOGGER = new LoggerHandler().getGenericConsoleHandler(MainGUI.class.getName());
    private static final String KEY = "Jaguaric@3105";
    private static MainGUIController mainController;

    @Override
    public void start(Stage primaryStage) {
        AppFactory.setMainGUI(this);
        primaryStage.setHeight(450);
        primaryStage.setWidth(500);
        final String JAVA_VERSION = System.getProperty("java.version");
        final String JAVAFX_VERSION = System.getProperties().get("javafx.runtime.version").toString();
        LOGGER.info("Java JDK Runtime Version: " + JAVA_VERSION);
        LOGGER.info("JavaFX Runtime Version: " + JAVAFX_VERSION);
        Encryption.setKey(KEY);
        startProgram(primaryStage);
    }

    private static Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane wrapperPane = loader.load(MainGUI.class.getResourceAsStream(SceneNavigator.MAIN));
        wrapperPane.setOnMouseClicked(e -> wrapperPane.requestFocus());

        mainController = loader.getController();
        mainController.setMainPanePadding(300, 70, 300, 70);

        SceneNavigator.setMainGUIController(mainController);
        SceneNavigator.loadScene(SceneNavigator.LOGIN);
        LOGGER.info("Wrapper pane successfully loaded.");
        return wrapperPane;
    }

    public static void closeStage() {
        try {
            mainController = SceneNavigator.getMainController();
            mainController.getScene().getWindow().hide();
        } catch (NullPointerException ex) {
            LOGGER.severe("Tryied to acces null stage object.");
            ex.printStackTrace();
            ExceptionHandler.incrementGlobalExceptionsCount();
            exitProgram();
        }
    }

    public static void startProgram(Stage stage) {
        try {
            stage.setTitle("SysRestaurante");
            stage.setOnCloseRequest(e -> exitProgram());
            stage.setScene(createScene(loadMainPane()));
            stage.setMinHeight(390);
            stage.setMinWidth(450);
            stage.centerOnScreen();
            LOGGER.info("Program started with " + ExceptionHandler.getGlobalExceptionsCount() + " errors.");
            stage.show();
        } catch (IOException | IllegalStateException exception) {
            LOGGER.severe("Exception triggered by startProgram()");
            NotificationHandler.errorDialog(exception);
            exception.printStackTrace();
        }
    }

    public void restartProgram() {
        start((Stage) mainController.getScene().getWindow());
        closeStage();
        try {
            AppFactory.clearWorkspace();
            LOGGER.info("Workspace cleared.");
        } catch (Exception exception) {
            exception.printStackTrace();
            LOGGER.severe("Erro while trying to clear workspace");
        }

        start(new Stage());
    }

    public static void exitProgram() {
        if (AppFactory.getUserDao() != null) {
            AppFactory.getLoginController().storeLastSessionDuration();
        }

        LOGGER.info(DBConnection.getGlobalDBRequestsCount() + " requests to database.");
        Platform.exit();
        LOGGER.info("Program exited.");
        System.exit(0);
    }

    public static MainGUIController getMainController() {
        return mainController;
    }

    private static Scene createScene(Pane mainPane) {
        return new Scene(mainPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
