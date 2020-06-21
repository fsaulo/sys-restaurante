package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Personnel;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;

public class DetailsCashierBoxController {

    @FXML
    private Label dateLabel;
    @FXML
    private Label withdrawalLabel;
    @FXML
    private Label inCashLabel;
    @FXML
    private Label byCardLabel;
    @FXML
    private Label revenueLabel;
    @FXML
    private Label codCashier;
    @FXML
    private Label userLabel;
    @FXML
    private Tooltip toolTip;

    private final CashierDao cashierDao;

    public DetailsCashierBoxController (CashierDao cashierDao) {
        this.cashierDao = cashierDao;
    }

    public void initialize() throws SQLException {
        final UserDao user = Personnel.getUserInfoById(cashierDao.getIdUser());
        final String dateOpening = DateTimeFormatter.ofPattern("dd/MM/yyyy H:s:m")
                .format(cashierDao.getDateOpening().atTime(cashierDao.getTimeOpening()));

        StringBuilder userTypeLabel = new StringBuilder("Aberto por " + user.getName());

        if (user.isAdmin()) {
            userTypeLabel.append(" (ADMINSTRADOR)");
        } else {
            userTypeLabel.append(" (FUNCIONÁRIO)");
        }

        NumberFormat formatter = CurrencyField.getBRLCurrencyFormat();
        withdrawalLabel.setText(formatter.format(cashierDao.getWithdrawal()));
        revenueLabel.setText(formatter.format(cashierDao.getRevenue()));
        inCashLabel.setText(formatter.format(cashierDao.getInCash()));
        byCardLabel.setText(formatter.format(cashierDao.getByCard()));
        dateLabel.setText(dateOpening);
        codCashier.setText("CAIXA #" + cashierDao.getIdCashier());
        userLabel.setText(userTypeLabel.toString());
        toolTip.setText("Caixa aberto no dia: " + dateOpening);
    }
}
