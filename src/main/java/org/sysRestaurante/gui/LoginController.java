package org.sysRestaurante.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.gui.formatter.DateFormatter;
import org.sysRestaurante.model.Authentication;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements DateFormatter {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(LoginController.class.getName());
    private static final String SIGNATURE_IMAGE = "/images/a1c7cfbbf306ef586600fcf2da1d5acd.png";
    private static final String LOGINTEXT_IMAGE = "/images/login-text.png";
    private final Authentication certs = new Authentication();
    private static UserDao userDaoData;

    @FXML
    private Label dbStatusLabel;
    @FXML
    private Label statusAccessLabel;
    @FXML
    private Label lastSessionLabel;
    @FXML
    private Label clockLabel;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private ImageView signatureImage;
    @FXML
    private ImageView loginTextImage;

    public void initialize() {
        AppFactory.setLoginController(this);
        loginPane.setMinHeight(250);
        loginPane.setMinWidth(440);
        statusAccessLabel.setText("");
        startClock();

        if (certs.isDatabaseConnected()) {
            dbStatusLabel.setTextFill(Color.web("Green"));
        } else {
            dbStatusLabel.setTextFill(Color.web("Red"));
            dbStatusLabel.setText("Desconectado");
        }

        try {
            setSignatureImage();
            setLoginTextImage();
        } catch (FileNotFoundException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Invalid path to image files");
            e.printStackTrace();
        }

        usernameField.setText("admin");
        passwordField.setText("123");

        Platform.runLater(() -> loginPane.requestFocus());

        setLastSessionMessage();
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Login pane initialized.");
    }

    public void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Date clockDate = new Date();
            String clockDateFormatted = CLOCK_FORMAT.format(clockDate);
            clockLabel.setText(clockDateFormatted);
        }), new KeyFrame(Duration.millis(1000)));

        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private Image loadImage(String resourcePath) {
        var url = getClass().getResource(resourcePath);
        if (url == null) {
            throw new IllegalStateException("Image resource not found: " + resourcePath);
        }
        return new Image(url.toExternalForm());
    }

    public void setSignatureImage() throws FileNotFoundException {
        signatureImage.setImage(loadImage(SIGNATURE_IMAGE));
    }

    public void setLoginTextImage() throws  FileNotFoundException {
        loginTextImage.setImage(loadImage(LOGINTEXT_IMAGE));
    }

    public void storeLastSessionDuration() {
        certs.setSessionDuration(userDaoData.getIdUser(),
                new Authentication().getLastSessionId(),
                AppFactory.getAppController().getElapsedSessionTime());
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

    private void onAuthenticationAccepted() {
        userDaoData = certs.getUserData(usernameField.getText());
        AppFactory.setUserDao(userDaoData);
        MainGUIController mainController = MainGUI.getMainController();
        mainController.setMainPanePadding(0, 0, 0, 0);
        SceneNavigator.loadScene(SceneNavigator.APPLICATION_STAGE);
    }

    @FXML
    public void loginRequested() throws SQLException {
        int typeAuthentication = certs.loginRequested(usernameField.getText(), passwordField.getText());
        LOGGER.config("Type of authentication: " + typeAuthentication);

        switch (typeAuthentication) {
            case 0:
                statusAccessLabel.setTextFill(Color.web("Green"));
                statusAccessLabel.setText("Acesso garantido");
                setLastSessionMessage();
                onAuthenticationAccepted();
            case 1:
                statusAccessLabel.setTextFill(Color.web("Green"));
                statusAccessLabel.setText("Acesso garantido");
                setLastSessionMessage();
                onAuthenticationAccepted();
                LOGGER.info("Admin login");
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
