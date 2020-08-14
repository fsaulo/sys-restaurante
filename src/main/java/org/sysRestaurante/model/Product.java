package org.sysRestaurante.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.NotificationHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Product {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Product.class.getName());

    public static ObservableList<ProductDao> getProducts() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        ProductDao productDao;
        ProductDao.CategoryDao categoryDao;
        ObservableList<ProductDao> products = FXCollections.observableArrayList();
        List<ProductDao.CategoryDao> categories = new ArrayList<>();
        String query1 = "SELECT * FROM categoria_produto";
        String query2 = "SELECT * FROM produto";

        try {
            con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query1);
            rs = ps.executeQuery();

            while (rs.next()) {
                categoryDao = new ProductDao.CategoryDao();
                categoryDao.setDescription(rs.getString("descricao"));
                categoryDao.setIdCategory(rs.getInt("id_categoria_produto"));
                categories.add(categoryDao);
            }

            ps = Objects.requireNonNull(con).prepareStatement(query2);
            rs = ps.executeQuery();

            while (rs.next()) {
                productDao = new ProductDao();
                int categoryId = rs.getInt("id_categoria");

                productDao.setIdProduct(rs.getInt("id_produto"));
                productDao.setIdCategory(rs.getInt("id_categoria"));
                productDao.setDescription(rs.getString("descricao"));
                productDao.setSellPrice(rs.getDouble("preco_venda"));
                productDao.setBuyPrice(rs.getDouble("preco_varejo"));
                productDao.setTrackStock(rs.getBoolean("check_estoque"));
                productDao.setIngredient(rs.getBoolean("is_ingrediente"));
                productDao.setMenuItem(rs.getBoolean("is_cardapio"));
                productDao.setSupply(rs.getInt("qtd_estoque"));
                productDao.setMinSupply(rs.getInt("qtd_estoque_minimo"));
                productDao.setCategoryDao(categories.stream().filter(e -> e.getIdCategory() == categoryId)
                        .findFirst()
                        .orElse(new ProductDao.CategoryDao()));
                products.add(productDao);
            }

            return products;
        } catch (SQLException ex) {
            LOGGER.severe("Coudn't get products list due to an SQLException");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        } finally {
            assert ps != null;
            assert rs != null;
            ps.close();
            rs.close();
            con.close();
        }
        return null;
    }

    public static List<ProductDao.CategoryDao> getProductCategory() {
        String query = "SELECT * FROM categoria_produto";
        PreparedStatement ps;
        ResultSet rs;
        ProductDao.CategoryDao categoryDao;
        List<ProductDao.CategoryDao> categories = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                categoryDao = new ProductDao.CategoryDao();
                categoryDao.setDescription(rs.getString("descricao"));
                categoryDao.setIdCategory(rs.getInt("id_categoria_produto"));
                categories.add(categoryDao);
            }

            con.close();
            rs.close();
            ps.close();

            return categories;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static void insert(ProductDao product) {
        String query = "INSERT INTO produto " +
                "(id_categoria, qtd_estoque, descricao, preco_venda, preco_varejo, check_estoque, is_cardapio, " +
                "is_ingrediente, qtd_estoque_minimo) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, product.getCategoryDao().getIdCategory());
            ps.setInt(2, product.getSupply());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getSellPrice());
            ps.setDouble(5, product.getBuyPrice());
            ps.setBoolean(6, product.isTrackStock());
            ps.setBoolean(7, product.isMenuItem());
            ps.setBoolean(8, product.isIngredient());
            ps.setInt(9, product.getMinSupply());
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException exception) {
            NotificationHandler.errorDialog(exception);
            LOGGER.severe("Couldn't insert product into database due to SQLException.");
            exception.printStackTrace();
        }
    }

    public static void update(ProductDao product) {
        String query = "UPDATE produto " +
                "SET id_categoria = ?, qtd_estoque = ?, descricao = ?, preco_venda = ?, preco_varejo = ?, " +
                "check_estoque = ?, is_cardapio = ?, is_ingrediente = ?, qtd_estoque_minimo = ? " +
                "WHERE id_produto = ?";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, product.getCategoryDao().getIdCategory());
            ps.setInt(2, product.getSupply());
            ps.setString(3, product.getDescription());
            ps.setDouble(4, product.getSellPrice());
            ps.setDouble(5, product.getBuyPrice());
            ps.setBoolean(6, product.isTrackStock());
            ps.setBoolean(7, product.isMenuItem());
            ps.setBoolean(8, product.isIngredient());
            ps.setInt(9, product.getMinSupply());
            ps.setInt(10, product.getIdProduct());
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException exception) {
            NotificationHandler.errorDialog(exception);
            LOGGER.severe("Couldn't update product due to SQLException.");
            exception.printStackTrace();
        }
    }

    public static void remove(int idProduct) {
        String query = "DELETE FROM produto WHERE id_produto = ?";
        PreparedStatement ps;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            ps.setInt(1, idProduct);
            ps.executeUpdate();

            ps.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            LOGGER.severe("Error while trying to delete product from database.");
            NotificationHandler.errorDialog(ex);
        }
    }
}
