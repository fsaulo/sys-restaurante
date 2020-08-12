package org.sysRestaurante.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

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

    public static String getProductCategory() {
        String query = "SELECT * FROM categoria_produto";
        String category = "Sem categoria";
        PreparedStatement ps;
        ResultSet rs;

        try {
            Connection con = DBConnection.getConnection();
            ps = Objects.requireNonNull(con).prepareStatement(query);
            rs = ps.executeQuery();

            if (rs.next()) {
                category = rs.getString("descricao");
            }

            con.close();
            rs.close();
            ps.close();
            return category;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
