package org.sysRestaurante.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.Encryption;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class MainGUI extends Application {

    private static final Logger LOGGER = new LoggerHandler().getGenericConsoleHandler(MainGUI.class.getName());
    private static final String KEY = "Jaguaric@3105";
    private static MainGUIController mainController;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setHeight(450);
        primaryStage.setWidth(550);
        final String JAVA_VERSION = System.getProperty("java.version");
        final String JAVAFX_VERSION = System.getProperties().get("javafx.runtime.version").toString();
        LOGGER.info("Java JDK Runtime Version: " + JAVA_VERSION);
        LOGGER.info("JavaFX Runtime Version: " + JAVAFX_VERSION);
        Encryption.setKey(KEY);
        startProgram(primaryStage);

        LOGGER.info("Program started with " + ExceptionHandler.getGlobalExceptionsCount() + " errors.");
    }

    private static Pane loadMainPane() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Pane wrapperPane = loader.load(MainGUI.class.getResourceAsStream(SceneNavigator.MAIN));

        mainController = loader.getController();
        LOGGER.info("Wrapper pane successfully loaded.");
        mainController.setMainPanePadding(300, 70, 300, 70);
        wrapperPane.setOnMouseClicked(e -> wrapperPane.requestFocus());

        SceneNavigator.setMainGUIController(mainController);
        SceneNavigator.loadScene(SceneNavigator.LOGIN);
        return wrapperPane;
    }

    public static void closeStage() {
        try {
            Stage stage = (Stage) mainController.getScene().getWindow();
            stage.close();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Tryied to acces null stage object.");
            exitProgram();
        }
    }

    public static void startProgram(Stage stage) {
        stage.setTitle("SysRestaurante");

        try {
            stage.setScene(createScene(loadMainPane()));
            stage.setMinHeight(390);
            stage.setMinWidth(800);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException exception) {
            NotificationHandler.errorDialog(exception);
            exception.printStackTrace();
        }

        stage.setOnCloseRequest(e -> exitProgram());
    }

    public static void restartProgram() {
        closeStage();
        Stage stage = new Stage();
        stage.setHeight(450);
        stage.setWidth(550);
        startProgram(stage);
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
