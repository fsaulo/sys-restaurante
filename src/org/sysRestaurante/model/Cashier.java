package org.sysRestaurante.model;

import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class Cashier {

    private Connection con;

    public void open(int userId) {
        PreparedStatement ps;
        CashierDao cashier = new CashierDao();
        cashier.setIdUser(userId);
        cashier.setDateOpening(LocalDate.now());
        cashier.setTimeOpening(LocalTime.now());
        cashier.setOpenned(true);
        String query = "INSERT INTO caixa (id_usuario, data_abertura, hora_abertura, balanco, is_aberto) " +
                "VALUES (?,?,?,?,?)";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(cashier.getDateOpening()));
            ps.setTime(3, Time.valueOf(cashier.getTimeOpening()));
            ps.setDouble(4, 0.0);
            ps.setBoolean(5, true);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();

            while (keys.next()) {
                int idCashier = keys.getInt(1);
                cashier.setIdCashier(idCashier);
            }

            AppFactory.setCashierDao(cashier);
            keys.close();
            ps.close();
            con.close();
        } catch (SQLException ex) {
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
            ps.close();
            con.close();
        } catch (SQLException ex) {
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

            AppFactory.setCashierDao(new CashierDao(idCashier));
            ps.close();
            con.close();
            rs.close();
            return isOpenned;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
