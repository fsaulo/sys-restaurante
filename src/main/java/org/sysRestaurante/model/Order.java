package org.sysRestaurante.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.dao.KitchenOrderDao;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Order {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Order.class.getName());
    public static final int CANCELED = 3;

    public static OrderDao newOrder(int idUser, int idCashier, double inCash, double byCard, int type, double discount,
                                    double taxes, String note) {
        String query = "INSERT INTO pedido (id_usuario, id_caixa, data_pedido, observacao, " +
                "valor_cartao, valor_avista, id_categoria_pedido, hora_pedido, descontos, status, taxas) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        int idOrder;
        OrderDao orderDao = new OrderDao();
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();
            ps = Objects.requireNonNull(con).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idUser);
            ps.setInt(2, idCashier);
            ps.setDate(3, Date.valueOf(nowDate));
            ps.setString(4, note);
            ps.setDouble(5, byCard);
            ps.setDouble(6, inCash);
            ps.setInt(7, type);
            ps.setTime(8, Time.valueOf(nowTime));
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
            orderDao.setOrderTime(nowTime);
            orderDao.setDiscount(discount);
            orderDao.setOrderDate(nowDate);
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

    public static ComandaDao newComanda(int idTable, int idOrder, int idCashier, int idEmployee, int type) {
        PreparedStatement ps;
        ResultSet rs;
        String query = "INSERT INTO comanda (id_caixa, data_abertura, id_mesa, id_categoria_pedido, hora_abertura, " +
                "id_pedido, id_funcionario, is_aberto) VALUES (?,?,?,?,?,?,?,?)";

        ComandaDao comandaDao = new ComandaDao();

        try {
            Connection con = DBConnection.getConnection();

            LocalDate nowDate = LocalDate.now();
            LocalTime nowTime = LocalTime.now();

            ps = Objects.requireNonNull(con).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idCashier);
            ps.setDate(2, Date.valueOf(nowDate));
            ps.setInt(3, idTable);
            ps.setInt(4, type);
            ps.setTime(5, Time.valueOf(nowTime));
            ps.setInt(6, idOrder);
            ps.setInt(7, idEmployee);
            ps.setBoolean(8, true);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();

            while (rs.next()) {
                int idComanda = rs.getInt(1);
                comandaDao.setIdOrder(idComanda);
            }

            comandaDao.setIdCashier(idCashier);
            comandaDao.setDateOpening(nowDate);
            comandaDao.setTimeOpening(nowTime);
            comandaDao.setIdTable(idTable);
            comandaDao.setIdEmployee(idEmployee);
            comandaDao.setIdOrder(idOrder);
            comandaDao.setOpen(true);

            LOGGER.info("Comanda was registered successfully.");
            ps.close();
            con.close();

            return comandaDao;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }

        return null;
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
                comanda.setOpen(rs.getBoolean("is_aberto"));

                try {
                    comanda.setTimeOpening(rs.getTime("hora_abertura").toLocalTime());
                    comanda.setDateOpening(rs.getDate("data_abertura").toLocalDate());
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

    public static boolean isComandaOpen(int idComanda) {
        String query = "SELECT is_aberto FROM comanda WHERE id_comanda = ?";
        PreparedStatement ps;
        ResultSet rs;

        boolean isOpen = false;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idComanda);
            rs = ps.executeQuery();

            while (rs.next()) {
                isOpen = rs.getBoolean("is_aberto");
            }

            ps.close();
            rs.close();
            con.close();
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }

        return isOpen;
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
            rs.close();
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
            rs.close();
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
            rs.close();
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

    public static void updateItemQtyFromOrderByOrderId(int idOrder, int qty) {
        String query = "UPDATE pedido_has_produtos SET qtd_pedido = ? WHERE id_pedido = ?";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, qty);
            ps.setInt(2, idOrder);
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

    public static void removeProductFromOrderByKitchenOrderId(int idKitchenOrder) {
        String query1 = "SELECT id_produto FROM pedido_cozinha_has_produtos WHERE id_pedido_cozinha = ?";
        String query2 = "DELETE FROM pedido_has_produtos WHERE id_produto = ?";
        PreparedStatement ps1, ps2;
        ResultSet rs1;

        try {
            Connection con = DBConnection.getConnection();
            ps1 = Objects.requireNonNull(con).prepareStatement(query1);
            ps1.setInt(1, idKitchenOrder);
            rs1 = ps1.executeQuery();

            if (rs1.next()) {
                int idProduct = rs1.getInt("id_produto");
                ps2 = con.prepareStatement(query2);
                ps2.setInt(1, idProduct);
                ps2.executeUpdate();
                ps2.close();
            }

            ps1.close();
            rs1.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            LOGGER.severe("Error trying to delete a product from order.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            NotificationHandler.errorDialog(ex);
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
            ps.setString(4, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
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
            ex.printStackTrace();
            LOGGER.severe("Error trying to register products in order.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            NotificationHandler.errorDialog(ex);
        }
    }

    public static boolean updateKitchenOrderStatus(int idKitchenOrder, int status) {
        PreparedStatement ps;
        String query = "UPDATE pedido_cozinha SET status = ? WHERE id_pedido_cozinha = ?";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, status);
            ps.setInt(2, idKitchenOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
        return false;
    }

    public static boolean updateKitchenOrderStatusWithDateTime(int idKitchenOrder, int status, LocalDateTime dateTime) {
        PreparedStatement ps;
        String query = "UPDATE pedido_cozinha SET status = ?, data_conclusao = ? WHERE id_pedido_cozinha = ?";

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, status);
            ps.setString(2, dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
            ps.setInt(3, idKitchenOrder);
            ps.executeUpdate();

            ps.close();
            con.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
        return false;
    }

    public static KitchenOrderDao getKitchenOrderById(int idKitchenOrder) {
        String query = "SELECT * FROM pedido_cozinha WHERE id_pedido_cozinha = ?";
        String name = null;
        PreparedStatement ps;
        ResultSet rs;

        KitchenOrderDao order = new KitchenOrderDao();

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idKitchenOrder);
            rs = ps.executeQuery();

            while (rs.next()) {
                String kitchenOrderDateString = rs.getString("data_pedido");
                String finalKitchenOrderDateString = rs.getString("data_conclusao");

                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                LocalDateTime kitchenOrderDateTime = kitchenOrderDateTime = LocalDateTime.parse(kitchenOrderDateString, formatter);
                LocalDateTime finalKitchenOrderDateTime = null;

                if (finalKitchenOrderDateString != null) {
                    finalKitchenOrderDateTime = LocalDateTime.parse(finalKitchenOrderDateString, formatter);
                }

                order.setKitchenOrderDateTime(kitchenOrderDateTime);
                order.setIdKitchenOrder(rs.getInt("id_pedido_cozinha"));
                order.setKitchenOrderStatus(KitchenOrderDao.KitchenOrderStatus.getByValue(rs.getInt("status")));
                order.setKitchenOrderDetails(rs.getString("observacoes"));
                order.setFinalKitchenOrderDateTime(finalKitchenOrderDateTime);
            }

            ps.close();
            rs.close();
            con.close();
            return order;
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
        return null;
    }

    public static ArrayList<KitchenOrderDao> getKitchenTicketsByComandaId(int idComanda) {
        String query1 = "SELECT * FROM comanda WHERE id_comanda = ?";
        String query2 = "SELECT * FROM pedido_cozinha WHERE id_comanda = ?";

        ArrayList<KitchenOrderDao> orderList = new ArrayList<>();
        KitchenOrderDao orderDao;
        PreparedStatement ps1, ps2;
        ResultSet rs1, rs2;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try {
            Connection con = DBConnection.getConnection();
            ps1 = Objects.requireNonNull(con).prepareStatement(query1);
            ps1.setInt(1, idComanda);
            rs1 = ps1.executeQuery();

            ComandaDao order = new ComandaDao();

            while (rs1.next()) {
                order.setIdComanda(idComanda);
                order.setIdCashier(rs1.getInt("id_caixa"));
                order.setIdTable(rs1.getInt("id_mesa"));
                order.setIdOrder(rs1.getInt("id_pedido"));
                order.setTotal(rs1.getDouble("total"));
                order.setIdCategory(rs1.getInt("id_categoria_pedido"));
                order.setIdEmployee(rs1.getInt("id_funcionario"));
                order.setOpen(rs1.getBoolean("is_aberto"));

                try {
                    order.setDateOpening(rs1.getDate("data_abertura").toLocalDate());
                    order.setTimeOpening(rs1.getTime("hora_abertura").toLocalTime());

                } catch (NullPointerException ex) {
                    ExceptionHandler.doNothing();
                }
            }

            ps2 = con.prepareStatement(query2);
            ps2.setInt(1, idComanda);
            rs2 = ps2.executeQuery();

            while (rs2.next()) {
                try {
                    orderDao = new KitchenOrderDao();

                    orderDao.setIdKitchenOrder(rs2.getInt("id_pedido_cozinha"));
                    orderDao.setKitchenOrderDetails(rs2.getString("observacoes"));
                    orderDao.setKitchenOrderDateTime(LocalDateTime.parse(rs2.getString("data_pedido"), formatter));
                    orderDao.setKitchenOrderStatus(KitchenOrderDao.KitchenOrderStatus.getByValue(rs2.getInt("status")));
                    orderDao.setIdOrder(order.getIdOrder());
                    orderDao.setIdCashier(order.getIdCashier());
                    orderDao.setIdTable(order.getIdTable());
                    orderDao.setTotal(order.getTotal());
                    orderDao.setIdCategory(order.getIdCategory());
                    orderDao.setIdEmployee(order.getIdEmployee());
                    orderDao.setOpen(order.isOpen());

                    orderList.add(orderDao);
                } catch (NullPointerException ignored) {
                    ExceptionHandler.doNothing();
                }
            }

            ps1.close();
            rs1.close();
            ps2.close();
            rs2.close();
            con.close();
            return orderList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
        return null;
    }

    public static List<KitchenOrderDao> getKitchenTicketsByCashierId(int idCashier) {
        String query1 = "SELECT * FROM comanda WHERE id_caixa = ?";
        String query2 = "SELECT * FROM pedido_cozinha WHERE id_comanda = ?";

        List<KitchenOrderDao> orderList = new ArrayList<>();
        KitchenOrderDao orderDao;
        PreparedStatement ps1, ps2;
        ResultSet rs1, rs2;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try {
            Connection con = DBConnection.getConnection();
            ps1 = Objects.requireNonNull(con).prepareStatement(query1);
            ps1.setInt(1, idCashier);
            rs1 = ps1.executeQuery();

            while (rs1.next()) {
                ComandaDao order = new ComandaDao();
                order.setIdCashier(idCashier);
                order.setIdComanda(rs1.getInt("id_comanda"));
                order.setIdTable(rs1.getInt("id_mesa"));
                order.setIdOrder(rs1.getInt("id_pedido"));
                order.setTotal(rs1.getDouble("total"));
                order.setIdCategory(rs1.getInt("id_categoria_pedido"));
                order.setIdEmployee(rs1.getInt("id_funcionario"));
                order.setOpen(rs1.getBoolean("is_aberto"));

                try {
                    order.setDateOpening(rs1.getDate("data_abertura").toLocalDate());
                    order.setTimeOpening(rs1.getTime("hora_abertura").toLocalTime());
                } catch (NullPointerException ex) {
                    ExceptionHandler.doNothing();
                }

                ps2 = con.prepareStatement(query2);
                ps2.setInt(1, order.getIdComanda());
                rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    try {
                        orderDao = new KitchenOrderDao();

                        orderDao.setIdKitchenOrder(rs2.getInt("id_pedido_cozinha"));
                        orderDao.setKitchenOrderDetails(rs2.getString("observacoes"));
                        orderDao.setKitchenOrderDateTime(LocalDateTime.parse(rs2.getString("data_pedido"), formatter));
                        orderDao.setKitchenOrderStatus(KitchenOrderDao.KitchenOrderStatus.getByValue(rs2.getInt("status")));
                        orderDao.setIdOrder(order.getIdOrder());
                        orderDao.setIdCashier(order.getIdCashier());
                        orderDao.setIdTable(order.getIdTable());
                        orderDao.setTotal(order.getTotal());
                        orderDao.setIdCategory(order.getIdCategory());
                        orderDao.setIdEmployee(order.getIdEmployee());
                        orderDao.setOpen(order.isOpen());
                        orderDao.setIdComanda(order.getIdComanda());

                        try {
                            orderDao.setFinalKitchenOrderDateTime(LocalDateTime.parse(rs2.getString("data_conclusao"), formatter));
                        } catch (NullPointerException ex) {
                            ExceptionHandler.doNothing();
                        }

                        orderList.add(orderDao);
                    } catch (NullPointerException ignored) {
                        ExceptionHandler.doNothing();
                    }
                }

                ps2.close();
                rs2.close();
            }

            ps1.close();
            rs1.close();
            con.close();
            return orderList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
        return null;
    }

    public static List<ProductDao> getItemsByKitchenOrderId(int idKitchenOrder) {
        String query1 = "SELECT * FROM pedido_cozinha_has_produtos WHERE id_pedido_cozinha = ?";
        String query2 = "SELECT * FROM produto WHERE id_produto = ?";

        ResultSet rs1, rs2;
        PreparedStatement ps1, ps2;
        List<ProductDao> productDaoList = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            ps1 = Objects.requireNonNull(con).prepareStatement(query1);
            ps1.setInt(1, idKitchenOrder);
            rs1 = ps1.executeQuery();

            while (rs1.next()) {
                int idProduct = rs1.getInt("id_produto");
                int qtyProduct = rs1.getInt("qtd_produto");

                ps2 = con.prepareStatement(query2);
                ps2.setInt(1, idProduct);
                rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    ProductDao productDao = new ProductDao();
                    productDao.setIdProduct(idProduct);
                    productDao.setDescription(rs2.getString("descricao"));
                    productDao.setQuantity(qtyProduct);
                    productDaoList.add(productDao);
                }

                ps2.close();
                rs2.close();
            }

            ps1.close();
            rs1.close();
            con.close();
            return productDaoList;
        } catch (SQLException ex) {
            ex.printStackTrace();
            NotificationHandler.errorDialog(ex);
        }
        return null;
    }
}
