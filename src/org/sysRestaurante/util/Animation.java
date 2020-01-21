package org.sysRestaurante.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {

    @FXML
    private static FadeTransition fadeTransition;

    public static void fade(Node node) {
        if (fadeTransition != null) fadeTransition.stop();
        fadeTransition = new FadeTransition(Duration.millis(250), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }
}
