package sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;
import sysRestaurante.model.Authentication;
import sysRestaurante.util.LoggerHandler;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sysRestaurante.util.SystemClock;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(LoginController.class.getName());
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    private Authentication certs;

    @FXML
    private Label dbStatusLabel;

    @FXML
    private Label statusAccessLabel;

    @FXML
    private AnchorPane loginPane;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ToggleSwitch isAdminToggle;

    @FXML
    private ImageView signatureImage;

    @FXML
    private Label lastSessionLabel;

    @FXML
    private Label clockLabel;


    public void initialize() {
         certs = new Authentication();

        if (certs.isDatabaseConnected()) {
            dbStatusLabel.setTextFill(Color.web("Green"));
            dbStatusLabel.setText("Conectado");

        } else {
            dbStatusLabel.setTextFill(Color.web("Red"));
            dbStatusLabel.setText("Desconectado");
        }

        loginPane.setMinHeight(250);
        loginPane.setMinWidth(440);

        statusAccessLabel.setText("");

        startClock();
        setSignatureImage();
        setLastSessionMessage();

        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Login pane initialized.");
    }

    public void setLastSessionMessage() {
        String sessionMessage;
        if (certs.getLastSessionDate() == null) {
            sessionMessage = "Não há registro de sessões";
        } else {
            String lastSessionDate = DATE_FORMAT.format(certs.getLastSessionDate());
            sessionMessage = "Última sessão em: " + lastSessionDate;
        }

        lastSessionLabel.setText(sessionMessage);
    }

    public void setSignatureImage() {
        signatureImage.setImage(new Image("file: @../../resources/images/a1c7cfbbf306ef586600fcf2da1d5acd.png"));
    }

    public void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            Date clockDate = new Date();
            String clockDateFormatted = CLOCK_FORMAT.format(clockDate);
            clockLabel.setText(clockDateFormatted.toString());
        }),
                new KeyFrame(Duration.millis(1000)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void loginRequested() throws SQLException {
        int typeAuthentication =  certs.systemAuthentication(usernameField.getText(), passwordField.getText(),
                isAdminToggle.isSelected());

        LOGGER.config("Type of authentication: " + typeAuthentication);

        switch (typeAuthentication) {
            case 0:
                statusAccessLabel.setTextFill(Color.web("Green"));
                statusAccessLabel.setText("Acesso garantido");
                setLastSessionMessage();
                break;
            case 1:
                statusAccessLabel.setTextFill(Color.web("Green"));
                statusAccessLabel.setText("Acesso garantido");
                setLastSessionMessage();
                if (!isAdminToggle.isSelected())
                    isAdminToggle.fire();
                break;
            case 2:
                statusAccessLabel.setTextFill(Color.web("Red"));
                statusAccessLabel.setText("Acesso não autorizado: usuário ou senha incorretos.");
                break;
            case 3:
                statusAccessLabel.setTextFill(Color.web("Red"));
                statusAccessLabel.setText("Acesso não autorizado: usuário não é administrador.");
                break;
            default:
                statusAccessLabel.setTextFill(Color.web("Red"));
                statusAccessLabel.setText("Acesso negado");
        }
    }
}





















