package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Personnel;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DetailsCashierBoxController {

    @FXML
    private Label dateLabel;
    @FXML
    private Label userLabel;
    @FXML
    private Label withdrawalLabel;
    @FXML
    private Label inCashLabel;
    @FXML
    private Label byCardLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private Tooltip toolTip;
    @FXML
    private Separator invisibleSeparator;

    private final CashierDao cashierDao;

    public DetailsCashierBoxController (CashierDao cashierDao) {
        this.cashierDao = cashierDao;
    }

    public void initialize() throws SQLException {
        final Stage primaryStage = AppFactory.getAppController().getMainStage();
        final UserDao user = Personnel.getUserInfoById(cashierDao.getIdUser());
        final String dateOpening = DateTimeFormatter.ofPattern("dd/MM/yyyy H:mm:ss")
                .format(cashierDao.getDateOpening().atTime(cashierDao.getTimeOpening()));

        StringBuilder userTypeLabel = new StringBuilder("por " + Objects.requireNonNull(user).getName());

        if (user.isAdmin()) {
            userTypeLabel.append(" (ADMINISTRADOR)");
        } else {
            userTypeLabel.append(" (FUNCIONÁRIO)");
        }

        NumberFormat formatter = CurrencyField.getBRLCurrencyFormat();
        withdrawalLabel.setText(formatter.format(cashierDao.getWithdrawal()));
        revenueLabel.setText(formatter.format(cashierDao.getRevenue()));
        inCashLabel.setText(formatter.format(cashierDao.getInCash()));
        byCardLabel.setText(formatter.format(cashierDao.getByCard()));
        dateLabel.setText("Caixa aberto em " + dateOpening);
        userLabel.setText(userTypeLabel.toString());
        toolTip.setText("Caixa aberto no dia: " + dateOpening);
        invisibleSeparator.setVisible(primaryStage.getWidth() >= 1400);

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            invisibleSeparator.setVisible(primaryStage.getWidth() >= 1400);
        });
    }
}
