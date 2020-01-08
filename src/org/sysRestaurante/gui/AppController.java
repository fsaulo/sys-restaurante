package org.sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.sysRestaurante.model.Authentication;
import org.sysRestaurante.util.DateFormatter;
import org.sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class AppController implements DateFormatter {
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(AppController.class.getName());

    @FXML
    private BorderPane borderPaneHolder;
    private Label sessionTimer = new Label();
    private Authentication certs = LoginController.getCertifications();
    private static long timerInMillies;

    public void initialize() throws IOException {
        borderPaneHolder.leftProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.MENU_TOOL_BAR)));
        borderPaneHolder.centerProperty().setValue(
                FXMLLoader.load(AppController.class.getResource(SceneNavigator.DASHBOARD)));
        SceneNavigator.loadScene(borderPaneHolder);
        borderPaneHolder.setAlignment(borderPaneHolder.getCenter(), Pos.CENTER);
    }

    public HBox getFooter() {
        startChronometer();
        String lastSessionDate = DATE_FORMAT.format(certs.getLastSessionDate());
        Label timeStatusLabel = new Label("Logado em " + lastSessionDate);
        Pane _growPane = new Pane();
        HBox footer = new HBox();
        Label copyleftLabel = new Label("Copyleft (C) 2020 Saulo Felix GNU SysRestaurante");

        footer.setPrefHeight(30);
        footer.setPadding(new Insets(1, 3, 1, 3));
        footer.setAlignment(Pos.CENTER);
        footer.setHgrow(_growPane, Priority.ALWAYS);
        footer.getChildren().addAll(timeStatusLabel, sessionTimer);
        footer.getChildren().addAll(_growPane, copyleftLabel);
        return footer;
    }

    private void startChronometer() {
        LocalDateTime initialTime = LocalDateTime.now();
        timerInMillies = 0L;
        Timeline chronometer = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            long elapsedTimeInSeconds = ChronoUnit.SECONDS.between(initialTime, LocalDateTime.now());
            String elapsedTime = LocalTime.ofSecondOfDay(elapsedTimeInSeconds).toString();
            this.sessionTimer.setText(" | Tempo da sessao " + elapsedTime);
            timerInMillies += 1L;
        }), new KeyFrame(Duration.millis(1000)));

        chronometer.setCycleCount(Animation.INDEFINITE);
        chronometer.play();
        LOGGER.info("Chronometer initialized normally");
    }
}
