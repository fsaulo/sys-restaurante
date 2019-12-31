package sysRestaurante.util;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animation {

    @FXML
    private static FadeTransition fadeTransition;

    public static void fade(Node node) {
        fadeTransition = new FadeTransition(Duration.millis(500), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setCycleCount(1);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    public static void stopFade() {
        fadeTransition.stop();
    }
}
