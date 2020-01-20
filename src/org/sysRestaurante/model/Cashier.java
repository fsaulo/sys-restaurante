package org.sysRestaurante.model;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Logger;

public class Cashier {

    private Connection con;
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Cashier.class.getName());

    public void open(int userId, double initialAmount, String note) {
        PreparedStatement ps;
        CashierDao cashier = new CashierDao();
        cashier.setIdUser(userId);
        cashier.setDateOpening(LocalDate.now());
        cashier.setTimeOpening(LocalTime.now());
        cashier.setOpenned(true);
        if (initialAmount < 0) initialAmount = 0.0;
        String query = "INSERT INTO caixa (id_usuario, data_abertura, hora_abertura, balanco_inicial, is_aberto, " +
                "observacao) " +
                "VALUES (?,?,?,?,?,?)";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(cashier.getDateOpening()));
            ps.setTime(3, Time.valueOf(cashier.getTimeOpening()));
            ps.setDouble(4, initialAmount);
            ps.setBoolean(5, true);
            ps.setString(6, note);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();

            while (keys.next()) {
                int idCashier = keys.getInt(1);
                cashier.setIdCashier(idCashier);
            }

            AppFactory.setCashierDao(cashier);
            LOGGER.info("Cashier openned.");
            keys.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.severe("Cashier couldn't be oppenned.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }

    public void close(int idCashier, double totalRevenue) {
        PreparedStatement ps;
        String query = "UPDATE caixa SET data_fechamento = ?, hora_fechamento = ?, balanco = ?, is_aberto = ?" +
                "WHERE id_caixa = ?";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setTime(2, Time.valueOf(LocalTime.now()));
            ps.setDouble(3, totalRevenue);
            ps.setBoolean(4, false);
            ps.setInt(5, idCashier);
            ps.executeUpdate();

            LOGGER.info("Cashier closed.");
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.severe("Cashier couldn't be oppened.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }

    public static boolean getLastCashierStatus() {
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT id_caixa, is_aberto FROM caixa ORDER BY id_caixa DESC LIMIT 1";
        boolean isOpenned = false;
        int idCashier = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                isOpenned = rs.getBoolean("is_aberto");
                idCashier = rs.getInt("id_caixa");
            }

            if (AppFactory.getCashierDao() == null) {
                AppFactory.setCashierDao(new CashierDao(idCashier));
            }

            ps.close();
            con.close();
            rs.close();
            return isOpenned;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static LocalDateTime getCashierDateTimeDetailsById(int idCashier) {
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT data_abertura, hora_abertura FROM caixa WHERE id_caixa = ?";
        LocalDateTime localDateTime = null;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                Date date = rs.getDate("data_abertura");
                Time time = rs.getTime("hora_abertura");
                localDateTime = date.toLocalDate().atTime(time.toLocalTime());
            }

            return localDateTime;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public CashierDao getCashierDataAccessObject(int idCashier) {
        PreparedStatement ps;
        ResultSet rs;
        String query = "SELECT * FROM caixa WHERE id_caixa = ?";
        CashierDao cashierDao = new CashierDao();

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                cashierDao.setIdCashier(idCashier);
                cashierDao.setIdUser(rs.getInt("id_usuario"));
                cashierDao.setDateOpening(rs.getDate("data_abertura").toLocalDate());
                cashierDao.setTimeOpening(rs.getTime("hora_abertura").toLocalTime());
                cashierDao.setRevenue(rs.getDouble("balanco"));
                cashierDao.setOpenned(rs.getBoolean("is_aberto"));
                cashierDao.setNote(rs.getString("observacao"));
                cashierDao.setWithdrawal(rs.getDouble("total_retiradas"));
                cashierDao.setInCash(rs.getDouble("total_avista"));
                cashierDao.setByCard(rs.getDouble("total_acartao"));
                cashierDao.setInitialAmount(rs.getDouble("balanco_inicial"));
                AppFactory.setCashierDao(cashierDao);
            }

            ps.close();
            rs.close();
            con.close();
            return cashierDao;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
