package org.sysRestaurante.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.CashierDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cashier {

    private Connection con;
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Cashier.class.getName());

    public void open(int userId, double initialAmount, String note) {
        String query = "INSERT INTO caixa (id_usuario, data_abertura, hora_abertura, balanco_inicial, is_aberto, " +
                "observacao) VALUES (?,?,?,?,?,?)";

        PreparedStatement ps;
        CashierDao cashier = new CashierDao();
        cashier.setIdUser(userId);
        cashier.setDateOpening(LocalDate.now());
        cashier.setTimeOpening(LocalTime.now());
        cashier.setOpenned(true);
        if (initialAmount < 0) initialAmount = 0.0;

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

    public void close(int idCashier) {
        String query = "UPDATE caixa SET data_fechamento = ?, hora_fechamento = ?, is_aberto = ? WHERE id_caixa = ?";
        PreparedStatement ps;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setTime(2, Time.valueOf(LocalTime.now()));
            ps.setBoolean(3, false);
            ps.setInt(4, idCashier);
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
            ps = con.prepareStatement(query1);
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

    public static double getRevenue(int idCashier) {
        String query = "SELECT balanco FROM caixa WHERE id_caixa = ?";
        double revenue = 0;
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                revenue = rs.getDouble("balanco");
            }

            ps.close();
            rs.close();
            con.close();
            return revenue;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static boolean getLastCashierStatus() {
        String query = "SELECT id_caixa, is_aberto FROM caixa ORDER BY id_caixa DESC LIMIT 1";
        PreparedStatement ps;
        ResultSet rs;
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

    public ObservableList<OrderDao> getOrderByIdCashier(int idCashier) {
        String query = "SELECT * FROM pedido where id_caixa = ?";
        ObservableList<OrderDao> orderList = FXCollections.observableArrayList();
        OrderDao orderDao;
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                orderDao = new OrderDao();
                orderDao.setIdOrder(rs.getInt("id_pedido"));
                orderDao.setInCash(rs.getDouble("valor_avista"));
                orderDao.setByCard(rs.getDouble("valor_cartao"));
                orderDao.setNote(rs.getString("observacao"));
                orderDao.setDetails(rs.getInt("id_categoria_pedido"));
                orderDao.setOrderDate(rs.getDate("data_pedido").toLocalDate());
                orderDao.setStatus(rs.getInt("id_categoria_status"));
                orderDao.setTotal(rs.getDouble("valor_avista") + rs.getDouble("valor_cartao"));
                orderList.add(orderDao);
            }

            ps.close();
            rs.close();
            con.close();
            return orderList;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static LocalDateTime getCashierDateTimeDetailsById(int idCashier) {
        String query = "SELECT data_abertura, hora_abertura FROM caixa WHERE id_caixa = ?";
        PreparedStatement ps;
        ResultSet rs;
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

            ps.close();
            rs.close();
            con.close();
            return localDateTime;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public CashierDao getCashierDataAccessObject(int idCashier) {
        String query = "SELECT * FROM caixa WHERE id_caixa = ?";
        PreparedStatement ps;
        ResultSet rs;
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

    public OrderDao newOrder(int idCashier, double inCash, double byCard, String note) {
        String query = "INSERT INTO pedido (id_usuario, id_caixa, id_categoria_status, data_pedido, observacao, " +
                "valor_cartao, valor_avista, id_categoria_pedido) VALUES (?,?,?,?,?,?,?,?)";

        int idUser = AppFactory.getUserDao().getIdUser();
        int idOrder;
        OrderDao orderDao = new OrderDao();
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUser);
            ps.setInt(2, idCashier);
            ps.setInt(3, 1);
            ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            ps.setString(5, note);
            ps.setDouble(6, byCard);
            ps.setDouble(7, inCash);
            ps.setInt(8, 1);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();

            while (rs.next()) {
                idOrder = rs.getInt(1);
                orderDao.setIdOrder(idOrder);
            }

            orderDao.setByCard(byCard);
            orderDao.setInCash(inCash);
            orderDao.setIdUser(idUser);
            orderDao.setNote(note);
            orderDao.setDetails(1);

            LOGGER.info("Sell was registered successfully.");
            ps.close();
            con.close();
            return orderDao;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void addProductsToOrder(int idOrder, ObservableList<ProductDao> productsList) {
        String query = "INSERT INTO pedido_has_produtos (id_produto, id_pedido, qtd_pedido) VALUES (?, ?, ?)";
        PreparedStatement ps = null;
        LOGGER.setLevel(Level.ALL);

        try {
            Connection con = DBConnection.getConnection();
            for (ProductDao item : productsList) {
                LOGGER.config("Adding products. Product id: " + item.getIdProduct());
                ps = con.prepareStatement(query);
                ps.setInt(1, item.getIdProduct());
                ps.setInt(2, idOrder);
                ps.setInt(3, item.getQuantity());
                ps.executeUpdate();
            }

            ps.close();
            con.close();
            LOGGER.info("Products added.");
        } catch (SQLException ex) {
            LOGGER.severe("Error trying to register products in order.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }
}
