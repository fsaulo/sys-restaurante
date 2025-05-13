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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Cashier {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Cashier.class.getName());

    public int open(int userId, double initialAmount, String note) {
        String query = "INSERT INTO caixa (id_usuario, data_abertura, hora_abertura, balanco_inicial, is_aberto, " +
                "observacao) VALUES (?,?,?,?,?,?)";

        Connection con;
        PreparedStatement ps;
        int idCashier = -1;

        if (initialAmount < 0) initialAmount = 0.0;

        CashierDao cashier = new CashierDao();
        cashier.setIdUser(userId);
        cashier.setDateOpening(LocalDate.now());
        cashier.setTimeOpening(LocalTime.now());
        cashier.setOpenned(true);
        cashier.setInitialAmount(initialAmount);

        try {
            con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(cashier.getDateOpening()));
            ps.setTime(3, Time.valueOf(cashier.getTimeOpening()));
            ps.setDouble(4, initialAmount);
            ps.setBoolean(5, true);
            ps.setString(6, note);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();

            while (keys.next()) {
                idCashier = keys.getInt(1);
                cashier.setIdCashier(idCashier);
            }

            AppFactory.setCashierDao(cashier);
            LOGGER.info("Cashier openned.");
            keys.close();
            ps.close();
            con.close();
        } catch (SQLException | NullPointerException ex) {
            LOGGER.severe("Cashier couldn't be oppenned.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }

        return idCashier;
    }

    public static void close(int idCashier) {
        String query = "UPDATE caixa SET data_fechamento = ?, hora_fechamento = ?, is_aberto = ? WHERE id_caixa = ?";
        PreparedStatement ps;
        Connection con;

        try {
            con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setTime(2, Time.valueOf(LocalTime.now()));
            ps.setBoolean(3, false);
            ps.setInt(4, idCashier);
            ps.executeUpdate();

            LOGGER.info("Cashier closed.");
            ps.close();
            con.close();
        } catch (SQLException | NullPointerException ex) {
            LOGGER.severe("Cashier couldn't be oppened.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }

    public static void setRevenue(int idCashier, double inCash, double byCard, double withdrawals) {
        String query1 = "SELECT balanco, total_avista, total_acartao, total_retiradas FROM caixa WHERE id_caixa = ?";
        String query2 = "UPDATE caixa SET total_avista = ?, total_acartao = ?, balanco = ?, total_retiradas = ? " +
                "WHERE id_caixa = ?";

        PreparedStatement ps;
        double oldRevenue = 0;
        double oldInCash = 0;
        double oldByCard = 0;
        double oldWithdrawals = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query1);
            ps.setInt(1, idCashier);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                oldRevenue = rs.getDouble("balanco");
                oldInCash = rs.getDouble("total_avista");
                oldByCard = rs.getDouble("total_acartao");
                oldWithdrawals = rs.getDouble("total_retiradas");
            }

            ps = con.prepareStatement(query2);
            ps.setDouble(1, inCash + oldInCash);
            ps.setDouble(2, byCard + oldByCard);
            ps.setDouble(3, oldRevenue + inCash + byCard);
            ps.setDouble(4, oldWithdrawals + withdrawals);
            ps.setInt(5, idCashier);
            ps.executeUpdate();

            rs.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isOpen() {
        String query = "SELECT id_caixa, is_aberto FROM caixa ORDER BY id_caixa DESC LIMIT 1";
        PreparedStatement ps;
        ResultSet rs;
        boolean isOpen = false;
        int idCashier = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                isOpen = rs.getBoolean("is_aberto");
                idCashier = rs.getInt("id_caixa");
            }

            if (AppFactory.getCashierDao() == null) {
                AppFactory.setCashierDao(new CashierDao(idCashier));
            }

            ps.close();
            con.close();
            rs.close();
            return isOpen;
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static boolean isOpen(int idCashier) {
        String query = "SELECT is_aberto FROM caixa WHERE id_caixa = ?";
        PreparedStatement ps;
        ResultSet rs;
        boolean isOpen = false;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                isOpen = rs.getBoolean("is_aberto");
            }

            ps.close();
            con.close();
            rs.close();
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return isOpen;
    }
    public static LocalDateTime getCashierDateTimeDetailsById(int idCashier) {
        String query = "SELECT data_abertura, hora_abertura FROM caixa WHERE id_caixa = ?";
        PreparedStatement ps;
        ResultSet rs;
        LocalDateTime localDateTime = null;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                Date date = rs.getDate("data_abertura");
                Time time = rs.getTime("hora_abertura");
                localDateTime = date.toLocalDate().atTime(time.toLocalTime());
            }

            ps.close();
            rs.close();
            con.close();
            return localDateTime;
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void getCashierDataAccessObject(int idCashier) {
        String query = "SELECT * FROM caixa WHERE id_caixa = ?";
        PreparedStatement ps;
        ResultSet rs;
        CashierDao cashierDao = new CashierDao();

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
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
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public static List<CashierDao> getCashier() {
        String query = "SELECT * FROM caixa";
        PreparedStatement ps;
        ResultSet rs;
        List<CashierDao> list = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                CashierDao cashierDao = new CashierDao();
                cashierDao.setIdCashier(rs.getInt("id_caixa"));
                cashierDao.setIdUser(rs.getInt("id_usuario"));
                cashierDao.setDateOpening(rs.getDate("data_abertura").toLocalDate());
                cashierDao.setTimeOpening(rs.getTime("hora_abertura").toLocalTime());
                cashierDao.setRevenue(rs.getDouble("balanco"));
                cashierDao.setWithdrawal(rs.getDouble("total_retiradas"));
                cashierDao.setInCash(rs.getDouble("total_avista"));
                cashierDao.setByCard(rs.getDouble("total_acartao"));
                cashierDao.setInitialAmount(rs.getDouble("balanco_inicial"));

                try {
                    cashierDao.setDateClosing(rs.getDate("data_fechamento").toLocalDate());
                    cashierDao.setTimeClosing(rs.getTime("hora_fechamento").toLocalTime());
                    cashierDao.configDateTimeEvent();
                } catch (NullPointerException exception) {
                    ExceptionHandler.doNothing();
                }

                list.add(cashierDao);
            }

            ps.close();
            rs.close();
            con.close();
            return list;
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
