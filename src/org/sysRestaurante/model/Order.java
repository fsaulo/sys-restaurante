package org.sysRestaurante.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Order {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Order.class.getName());

    public OrderDao newOrder(int idCashier, double inCash, double byCard, int type, double discount, String note) {
        String query = "INSERT INTO pedido (id_usuario, id_caixa, id_categoria_status, data_pedido, observacao, " +
                "valor_cartao, valor_avista, id_categoria_pedido, hora_pedido, descontos) VALUES (?,?,?,?,?,?,?,?,?,?)";

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
            ps.setInt(8, type);
            ps.setTime(9, java.sql.Time.valueOf(LocalTime.now()));
            ps.setDouble(10, discount * 100);
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
            orderDao.setOrderTime(LocalTime.now());
            orderDao.setDiscount(discount);
            orderDao.setOrderDate(LocalDate.now());
            orderDao.setOrderTime(LocalTime.now());

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

    public OrderDao getOrderById(int idOrder) {
        String query = "SELECT * FROM pedido where id_pedido = ?";
        OrderDao orderDao = new OrderDao();
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idOrder);
            rs = ps.executeQuery();

            while (rs.next()) {
                orderDao.setIdOrder(rs.getInt("id_pedido"));
                orderDao.setInCash(rs.getDouble("valor_avista"));
                orderDao.setByCard(rs.getDouble("valor_cartao"));
                orderDao.setNote(rs.getString("observacao"));
                orderDao.setDiscount(rs.getInt("descontos"));
                orderDao.setDetails(rs.getInt("id_categoria_pedido"));
                orderDao.setOrderDate(rs.getDate("data_pedido").toLocalDate());
                orderDao.setOrderTime(rs.getTime("hora_pedido").toLocalTime());
                orderDao.setStatus(rs.getInt("id_categoria_status"));
                orderDao.setTotal(rs.getDouble("valor_avista") + rs.getDouble("valor_cartao"));
            }

            ps.close();
            rs.close();
            con.close();
            return orderDao;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
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

    public int getLastOrderId() {
        String query = "SELECT id_pedido FROM pedido ORDER BY id_pedido DESC LIMIT 1";
        PreparedStatement ps;
        ResultSet rs;
        int idOrder = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                idOrder = rs.getInt("id_pedido");
            }

            ps.close();
            con.close();
            rs.close();
            return idOrder;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
