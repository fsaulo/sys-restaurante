package org.sysRestaurante.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Order {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Order.class.getName());
    public static final int CANCELED = 3;

    public static OrderDao newOrder(int idCashier, double inCash, double byCard, int type, double discount,
                                    double taxes, String note) {
        String query = "INSERT INTO pedido (id_usuario, id_caixa, data_pedido, observacao, " +
                "valor_cartao, valor_avista, id_categoria_pedido, hora_pedido, descontos, status, taxas) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        int idUser = AppFactory.getUserDao().getIdUser();
        int idOrder;
        OrderDao orderDao = new OrderDao();
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUser);
            ps.setInt(2, idCashier);
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setString(4, note);
            ps.setDouble(5, byCard);
            ps.setDouble(6, inCash);
            ps.setInt(7, type);
            ps.setTime(8, Time.valueOf(LocalTime.now()));
            ps.setDouble(9, discount);
            ps.setInt(10, type);
            ps.setDouble(11, taxes);
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
            orderDao.setTaxes(taxes);

            LOGGER.info("Sell was registered successfully.");
            ps.close();
            con.close();
            rs.close();
            return orderDao;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public void newComanda(int idTable, int idOrder, int idCashier, int idEmployee, int type) {
        PreparedStatement ps;
        String query = "INSERT INTO comanda (id_caixa, data_abertura, id_mesa, id_categoria_pedido, hora_abertura, " +
                "id_pedido, id_funcionario, is_aberto) VALUES (?,?,?,?,?,?,?,?)";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idCashier);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setInt(3, idTable);
            ps.setInt(4, type);
            ps.setTime(5, Time.valueOf(LocalTime.now()));
            ps.setInt(6, idOrder);
            ps.setInt(7, idEmployee);
            ps.setBoolean(8, true);
            ps.executeUpdate();

            LOGGER.info("Comanda was registered successfully.");
            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static List<ComandaDao> getComandasByIdCashier(int idCashier) {
        String query = "SELECT * FROM comanda WHERE id_caixa = ?";
        List<ComandaDao> tables = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                ComandaDao comanda = new ComandaDao();
                comanda.setIdCashier(idCashier);
                comanda.setIdTable(rs.getInt("id_mesa"));
                comanda.setIdComanda(rs.getInt("id_comanda"));
                comanda.setIdOrder(rs.getInt("id_pedido"));
                comanda.setTotal(rs.getDouble("total"));
                comanda.setIdCategory(rs.getInt("id_categoria_pedido"));
                comanda.setIdEmployee(rs.getInt("id_funcionario"));
                comanda.setTimeOpening(rs.getTime("hora_abertura").toLocalTime());
                comanda.setDateOpening(rs.getDate("data_abertura").toLocalDate());
                comanda.setOpen(rs.getBoolean("is_aberto"));

                try {
                    comanda.setDateClosing(rs.getDate("data_fechamento").toLocalDate());
                    comanda.setTimeClosing(rs.getTime("hora_fechamento").toLocalTime());
                } catch (NullPointerException ignored) {
                    ExceptionHandler.doNothing();
                }

                tables.add(comanda);
            }

            ps.close();
            rs.close();
            con.close();
            return tables;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static ComandaDao getComandaByOrderId(int idOrder) {
        String query = "SELECT * FROM comanda WHERE id_pedido = ?";
        ComandaDao comanda = null;
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idOrder);
            rs = ps.executeQuery();

            while (rs.next()) {
                comanda = new ComandaDao();
                comanda.setIdCashier(idOrder);
                comanda.setIdTable(rs.getInt("id_mesa"));
                comanda.setIdComanda(rs.getInt("id_comanda"));
                comanda.setIdOrder(rs.getInt("id_pedido"));
                comanda.setTotal(rs.getDouble("total"));
                comanda.setIdCategory(rs.getInt("id_categoria_pedido"));
                comanda.setIdEmployee(rs.getInt("id_funcionario"));
                comanda.setTimeOpening(rs.getTime("hora_abertura").toLocalTime());
                comanda.setDateOpening(rs.getDate("data_abertura").toLocalDate());
                comanda.setOpen(rs.getBoolean("is_aberto"));

                try {
                    comanda.setDateClosing(rs.getDate("data_fechamento").toLocalDate());
                    comanda.setTimeClosing(rs.getTime("hora_fechamento").toLocalTime());
                } catch (NullPointerException ignored) {
                    ExceptionHandler.doNothing();
                }
            }

            ps.close();
            rs.close();
            con.close();
            return comanda;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static void closeComanda(int idComanda, double total) {
        PreparedStatement ps;
        String query = "UPDATE comanda " +
                "SET data_fechamento = ?, hora_fechamento = ?, total = ?, id_categoria_pedido = ?, is_aberto = ? " +
                "WHERE id_comanda = ?";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setTime(2, Time.valueOf(LocalTime.now()));
            ps.setDouble(3, total);
            ps.setInt(4, 6);
            ps.setBoolean(5, false);
            ps.setInt(6, idComanda);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void cancel(int idOrder) {
        String query = "UPDATE pedido SET status = ? WHERE id_pedido = ?";
        PreparedStatement ps;
        int CANCELED = 3;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, CANCELED);
            ps.setInt(2, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
            LOGGER.info("Order successfully canceled.");
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void updateOrderStatus(int idComanda, int idStatus) {
        String query1 = "SELECT id_pedido FROM comanda WHERE id_comanda = ?";
        String query2 = "UPDATE pedido SET status = ? WHERE id_pedido = ?";
        PreparedStatement ps;
        ResultSet rs;
        int idOrder = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query1);
            ps.setInt(1, idComanda);
            rs = ps.executeQuery();
            if (rs.next()) idOrder = rs.getInt("id_pedido");
            ps = con.prepareStatement(query2);
            ps.setInt(1, idStatus);
            ps.setInt(2, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void updateOrderAmount(int idComanda, double totalCash, double totalByCard, double discounts) {
        String query1 = "SELECT id_pedido FROM comanda WHERE id_comanda = ?";
        String query2 = "UPDATE pedido SET valor_cartao = ?, valor_avista = ?, descontos = ? WHERE id_pedido = ?";
        PreparedStatement ps;
        ResultSet rs;
        int idOrder = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query1);
            ps.setInt(1, idComanda);
            rs = ps.executeQuery();
            if (rs.next()) idOrder = rs.getInt("id_pedido");
            ps = con.prepareStatement(query2);
            ps.setDouble(1, totalByCard);
            ps.setDouble(2, totalCash);
            ps.setDouble(3, discounts);
            ps.setInt(4, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void updateEmployee(int idOrder, int idEmployee) {
        String query = "UPDATE comanda SET id_funcionario = ? WHERE id_pedido = ?";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idEmployee);
            ps.setInt(2, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void insertCustomerName(int idOrder, String customerName) {
        String query = "UPDATE pedido SET nome_cliente = ? WHERE id_pedido = ?";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setString(1, customerName);
            ps.setInt(2, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static String getCustomerName(int idOrder) {
        String query = "SELECT nome_cliente FROM pedido WHERE id_pedido = ?";
        String name = null;
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idOrder);
            rs = ps.executeQuery();

            if (rs.next()) name = rs.getString("nome_cliente");

            ps.close();
            con.close();
            return name;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static List<ProductDao> getItemsByOrderId(int idOrder) {
        String query = "SELECT * FROM pedido_has_produtos AS selecionado " +
                "JOIN produto ON produto.id_produto = selecionado.id_produto " +
                "WHERE selecionado.id_pedido = ?";
        List<ProductDao> products = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idOrder);
            rs = ps.executeQuery();

            while (rs.next()) {
                ProductDao product = new ProductDao();
                product.setQuantity(rs.getInt("qtd_pedido"));
                product.setIdProduct(rs.getInt("id_produto"));
                product.setDescription(rs.getString("descricao"));
                product.setSellPrice(rs.getDouble("preco_venda"));
                product.setBuyPrice(rs.getDouble("preco_varejo"));
                product.setTotal(product.getSellPrice() * product.getQuantity());
                products.add(product);
            }

            ps.close();
            rs.close();
            con.close();
            return products;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static void addProductsToOrder(int idOrder, ArrayList<ProductDao> productsList) {
        String query = "INSERT INTO pedido_has_produtos (id_produto, id_pedido, qtd_pedido) VALUES (?, ?, ?)";
        PreparedStatement ps;
        LOGGER.setLevel(Level.ALL);

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            for (ProductDao item : productsList) {
                ps.setInt(1, item.getIdProduct());
                ps.setInt(2, idOrder);
                ps.setInt(3, item.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.severe("Error trying to register products in order.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void removeProductsFromOrder(int idOrder) {
        String query = "DELETE FROM pedido_has_produtos WHERE id_pedido = ?";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.severe("Error trying to delete a product from order.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static ObservableList<OrderDao> getOrderByIdCashier(int idCashier) {
        String query = "SELECT * FROM pedido where id_caixa = ?";
        ObservableList<OrderDao> orderList = FXCollections.observableArrayList();
        OrderDao orderDao;
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idCashier);
            rs = ps.executeQuery();

            while (rs.next()) {
                try {
                    orderDao = new OrderDao();
                    orderDao.setIdOrder(rs.getInt("id_pedido"));
                    orderDao.setInCash(rs.getDouble("valor_avista"));
                    orderDao.setByCard(rs.getDouble("valor_cartao"));
                    orderDao.setNote(rs.getString("observacao"));
                    orderDao.setDetails(rs.getInt("id_categoria_pedido"));
                    orderDao.setOrderDate(rs.getDate("data_pedido").toLocalDate());
                    orderDao.setOrderTime(rs.getTime("hora_pedido").toLocalTime());
                    orderDao.setStatus(rs.getInt("status"));
                    orderDao.setTotal(rs.getDouble("valor_avista") + rs.getDouble("valor_cartao"));
                    orderDao.setTaxes(rs.getDouble("taxas"));
                    orderDao.setDiscount(rs.getDouble("descontos"));
                    orderList.add(orderDao);
                } catch (NullPointerException ignored) {
                    ExceptionHandler.doNothing();
                }
            }

            ps.close();
            rs.close();
            con.close();
            return orderList;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static int getLastOrderId() {
        String query = "SELECT id_pedido FROM pedido ORDER BY id_pedido DESC LIMIT 1";
        PreparedStatement ps;
        ResultSet rs;
        int idOrder = 0;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
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
            NotificationHandler.errorDialog(ex);
        }
        return 0;
    }

    public static void changeTable(int idComanda, int idTable) {
        PreparedStatement ps;
        String query = "UPDATE comanda SET id_mesa = ? WHERE id_comanda = ?";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idTable);
            ps.setInt(2, idComanda);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void setDiscounts(int idOrder, double value) {
        PreparedStatement ps;
        String query = "UPDATE pedido SET descontos = ? WHERE id_pedido = ?";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setDouble(1, value);
            ps.setInt(2, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static void setTaxes(int idOrder, double value) {
        PreparedStatement ps;
        String query = "UPDATE pedido SET taxas = ? WHERE id_pedido = ?";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setDouble(1, value);
            ps.setInt(2, idOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }

    public static int newKitchenOrder(int idComanda, int status, String details) {
        PreparedStatement ps;
        ResultSet rs;
        String query = "INSERT INTO pedido_cozinha (id_comanda,status,observacoes,data_pedido) " +
                "VALUES (?,?,?,?)";
        int idOrder = -1;

        try {
            Connection con = DBConnection.getConnection();

            ps = Objects.requireNonNull(con).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idComanda);
            ps.setInt(2, status);
            ps.setString(3, details);
            ps.setDate(4, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();

            while (rs.next()) {
                idOrder = rs.getInt(1);
            }

            LOGGER.info("Kitchen's order was registered successfully.");

            ps.close();
            rs.close();
            con.close();

        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }

        return idOrder;
    }

    public static void addProductToKitchenOrder(int idKitchenOrder, ProductDao product) {
        String query = "INSERT INTO pedido_cozinha_has_produtos (id_pedido_cozinha, id_produto, qtd_produto) VALUES (?, ?, ?)";
        PreparedStatement ps;
        LOGGER.setLevel(Level.ALL);

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idKitchenOrder);
            ps.setInt(2, product.getIdProduct());
            ps.setInt(3, product.getQuantity());

            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.severe("Error trying to register products in order.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
    }
}
