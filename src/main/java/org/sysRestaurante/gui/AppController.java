package org.sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.SessionDao;
import org.sysRestaurante.model.Authentication;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class AppController implements DateFormatter {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(AppController.class.getName());
    private static long timerInMillies;
    private final Authentication certs = new Authentication();
    private Label sessionTimer;

    @FXML
    private BorderPane borderPaneHolder;

    public void initialize() throws IOException {
        startChronometer();
        SessionDao session = new SessionDao();
        AppFactory.setAppController(this);
        AppFactory.setSessionDao(session);
        borderPaneHolder.leftProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.MENU_TOOL_BAR)));
        borderPaneHolder.centerProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.DASHBOARD)));
        SceneNavigator.loadScene(borderPaneHolder);
        borderPaneHolder.setAlignment(borderPaneHolder.getCenter(), Pos.CENTER);
        Stage stage = (Stage) borderPaneHolder.getScene().getWindow();
        stage.setWidth(1215);
        stage.setHeight(700);
        stage.centerOnScreen();
        Platform.runLater(this::setFullScreenShortcut);
    }

    public void setFullScreenShortcut() {
        Stage stage = (Stage) borderPaneHolder.getParent().getScene().getWindow();
        stage.setFullScreenExitHint("Você entrou em modo tela cheia.\nPara sair pressione ESC ou F11");
        Runnable maximize = () -> {
            if (stage.isFullScreen()) stage.setFullScreen(false);
            else stage.setFullScreen(true); };
        AppFactory.getMainController().getScene().getAccelerators().put(SceneNavigator.F11_FULLSCREEN_MODE, maximize);
    }

    public HBox getFooter() {
        String lastSessionDate = DATE_FORMAT.format(certs.getLastSessionDate());
        Label timeStatusLabel = new Label("Logado em: " + lastSessionDate);
        timeStatusLabel.setStyle("-fx-font: Carlito 14");
        timeStatusLabel.setOpacity(0.6);
        Label copyleftLabel = new Label("(C) 2020 Saulo Felix GNU SysRestaurante");
        copyleftLabel.setStyle("-fx-font: Carlito 14");
        copyleftLabel.setOpacity(0.6);
        Pane growPane = new Pane();
        HBox footer = new HBox();
        footer.setSpacing(3);
        footer.setPadding(new Insets(2, 3, 1, 3));
        footer.setStyle("-fx-border-color: #CBCBCC");
        footer.setAlignment(Pos.CENTER);
        footer.setHgrow(growPane, Priority.ALWAYS);
        footer.getChildren().addAll(timeStatusLabel,
                new Separator(Orientation.VERTICAL),
                sessionTimer,
                new Separator(Orientation.VERTICAL));
        footer.getChildren().addAll(growPane, copyleftLabel);
        return footer;
    }

    public HBox getHeader() {
        Label titleLabel = new Label("Bar & Restaurante Frutos do Mar");
        titleLabel.setFont(Font.font("carlito", FontWeight.BOLD, FontPosture.REGULAR, 30));
        HBox header = new HBox();
        header.setPadding(new Insets(1, 1, 1, 1));
        header.getChildren().add(titleLabel);
        return header;
    }

    private void startChronometer() {
        LocalDateTime initialTime = LocalDateTime.now();
        timerInMillies = 0L;
        sessionTimer = new Label();
        Timeline chronometer = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long elapsedTimeInSeconds = ChronoUnit.SECONDS.between(initialTime, LocalDateTime.now());
            String elapsedTime = LocalTime.ofSecondOfDay(elapsedTimeInSeconds).toString();
            sessionTimer.setText("" + elapsedTime);
            sessionTimer.setStyle("-fx-font: Carlito 14");
            sessionTimer.setPadding(new Insets(0, 5, 0, 5));
            sessionTimer.setOpacity(0.6);
            timerInMillies += 1L;
        }), new KeyFrame(Duration.millis(1000)));

        chronometer.setCycleCount(Animation.INDEFINITE);
        chronometer.play();
        LOGGER.info("Chronometer initialized normally");
    }


    public long getElapsedSessionTime() {
        return timerInMillies;
    }

    public void loadPage(MouseEvent e, String fxml) {
        try {
            borderPaneHolder.centerProperty().setValue(FXMLLoader.load(AppController.class.getResource(fxml)));
            e.consume();
        } catch (IOException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Couldn't load " + fxml + " page.");
            ex.printStackTrace();
        }
    }

    public static void showDialog(String fxml, boolean main) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(AppFactory.getMainController().getScene().getWindow());
            if (main) AppFactory.getMainController().darkenScreen();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(fxml));
            Scene scene = new Scene(loader.load());
            stage.setTitle("SysRestaurante: Dialog " + fxml);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            if (main) AppFactory.getMainController().brightenScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDialog(String fxml, Object owner) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner((Window) owner);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(fxml));
            Scene scene = new Scene(loader.load());
            stage.setTitle("SysRestaurante: Dialog " + fxml);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.severe("Couldn't create stage.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            e.printStackTrace();
        }
    }

    public static void showUndecoratedDialog(String fxml, Object owner) {
        try {
            Window parent = AppFactory.getAppController().borderPaneHolder.getScene().getWindow();
            double centerPosX = parent.getX() + parent.getWidth()/2d;
            double centerPosY = parent.getY() + parent.getHeight()/2d;

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.initOwner((Window) owner);
            stage.initStyle(StageStyle.UNDECORATED);

            AppFactory.getMainController().darkenScreen();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(fxml));
            VBox popPup = loader.load();
            Scene scene = new Scene(popPup);

            stage.setX(centerPosX - popPup.getPrefWidth()/2d);
            stage.setY(centerPosY - popPup.getPrefHeight()/2d);

            stage.setTitle("SysRestaurante: Dialog " + fxml);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
            AppFactory.getMainController().brightenScreen();
        } catch (IOException e) {
            LOGGER.severe("Couldn't create stage.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            e.printStackTrace();
        }
    }

    public static void openPOS() {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(AppFactory.getMainController().getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(SceneNavigator.CASHIER_POS));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("SysRestaurante: Point of Sale");
            stage.setMinWidth(720);
            stage.setMinHeight(430);
            stage.setMinWidth(840);
            stage.setWidth(1090);
            stage.setResizable(true);
            stage.showAndWait();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void openFinishSell() {
        try {
            Stage stage = new Stage();
            AppFactory.getMainController().darkenScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(AppFactory.getMainController().getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(SceneNavigator.FINISH_SELL_DIALOG));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("SysRestaurante: Finalizando pedido");
            stage.setResizable(false);
            stage.showAndWait();
            AppFactory.getMainController().brightenScreen();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void openFinishSell(Object owner) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner((Window) owner);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(SceneNavigator.FINISH_SELL_DIALOG));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("SysRestaurante: Finalizando pedido");
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void openFinishSell(Object owner, Node object) {
        try {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner((Window) owner);
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-0.5);
            AppFactory.getMainController().darkenScreen();
            object.getParent().getParent().setEffect(colorAdjust);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(AppController.class.getResource(SceneNavigator.FINISH_SELL_DIALOG));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("SysRestaurante: Finalizando pedido");
            stage.setResizable(false);
            stage.showAndWait();
            colorAdjust.setBrightness(0);
            object.getParent().getParent().setEffect(colorAdjust);
            AppFactory.getMainController().brightenScreen();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
