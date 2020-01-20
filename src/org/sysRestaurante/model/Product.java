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
import java.util.logging.Logger;

public class Product {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Product.class.getName());

    public ObservableList<ProductDao> getProducts() {
        PreparedStatement ps;
        ResultSet rs;
        ProductDao productDao;
        String query = "SELECT * from produto";
        ObservableList<ProductDao> products = FXCollections.observableArrayList();

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                productDao = new ProductDao();
                productDao.setIdProduct(rs.getInt("id_produto"));
                productDao.setIdCategory(rs.getInt("id_categoria"));
                productDao.setDescription(rs.getString("descricao"));
                productDao.setSellPrice(rs.getDouble("preco_venda"));
                productDao.setBuyPrice(rs.getDouble("preco_varejo"));
                productDao.setBarCode(rs.getLong("codigo_de_barras"));
                products.add(productDao);
            }

            ps.close();
            rs.close();
            con.close();
            return products;
        } catch (SQLException ex) {
            LOGGER.severe("Coudn't get products list due to an SQLException");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }

        return null;
    }
}
