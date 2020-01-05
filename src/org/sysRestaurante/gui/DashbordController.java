package org.sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.etc.User;
import org.sysRestaurante.model.Authentication;
import org.sysRestaurante.util.DateFormatter;
import org.sysRestaurante.util.LoggerHandler;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

public class DashbordController implements DateFormatter {
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DashbordController.class.getName());
    private static long timerInMillies;

    @FXML
    private HBox hBoxFooter;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label userLabel;

    private Label sessionTimer = new Label();

    public void initialize() {
        setFooter();
        User userData = AppFactory.getUser();
        borderPane.setBottom(hBoxFooter);
        if (userData != null) {
            userLabel.setText("Bem vindo, " + userData.getName());
        }
    }

    public HBox getFooter() {
        return hBoxFooter;
    }

    private void setFooter() {
        startChronometer();

        Authentication certs = new Authentication();
        String lastSessionDate = DATE_FORMAT.format(certs.getLastSessionDate());
        Label timeStatusLabel = new Label("Logado em " + lastSessionDate);
        Pane _growPane = new Pane();
        Label copyleftLabel = new Label("Copyleft (C) 2020 Saulo Felix GNU SysRestaurante");

        hBoxFooter = new HBox();
        hBoxFooter.setPrefHeight(30);
        hBoxFooter.setPadding(new Insets(1, 3, 1, 3));
        hBoxFooter.setAlignment(Pos.CENTER);
        hBoxFooter.setHgrow(_growPane, Priority.ALWAYS);
        hBoxFooter.getChildren().addAll(timeStatusLabel, sessionTimer);
        hBoxFooter.getChildren().addAll(_growPane, copyleftLabel);
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

    public static long getElapsedSessionTime() {
        return timerInMillies;
    }
}