package org.sysRestaurante.model;

import org.sysRestaurante.dao.EmployeeDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;
import org.sysRestaurante.util.Encryption;
import org.sysRestaurante.util.NotificationHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Personnel {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Personnel.class.getName());

    public void insert(String nome, String pass, String username, String email) throws SQLException {
        PreparedStatement ps = null;
        Connection con = null;
        String query = "INSERT INTO usuario (nome, senha, username, email, is_admin) VALUES (?, ?, ?, ?, ?)";
        String password = Encryption.encrypt(pass);

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setString(1, nome);
            ps.setString(2, password);
            ps.setString(3, username);
            ps.setString(4, email);
            ps.setBoolean(5, false);
            ps.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.severe("User couldn't be registered.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();

        } finally {
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    public static String getEmployeeNameById(int idEmployee) {
        String name = "Nenhum";
        String query = "SELECT nome FROM funcionario WHERE id_funcionario = ?";
        PreparedStatement ps;
        Connection con;
        ResultSet rs;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idEmployee);
            rs = ps.executeQuery();

            if (rs.next()) {
                name = rs.getString("nome");
            }

            rs.close();
            ps.close();
            con.close();
            return name;
        } catch (SQLException ex) {
            NotificationHandler.errorDialog(ex);
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<EmployeeDao> list() {
        PreparedStatement ps = null;
        Connection con = null;
        ResultSet rs = null;
        String query = "SELECT * FROM funcionario";
        ArrayList<EmployeeDao> employees = new ArrayList<>();

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                EmployeeDao employeeDao = new EmployeeDao();
                employeeDao.setIdEmployee(rs.getInt("id_funcionario"));
                employeeDao.setName(rs.getString("nome"));
                employeeDao.setSalary(rs.getDouble("salario"));
                employeeDao.setPaymentDay(rs.getInt("dia_salario"));
                employees.add(employeeDao);
            }

            ps.close();
            rs.close();
            con.close();
            return employees;
        } catch (SQLException ex) {
            LOGGER.severe("Error while listing employee");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }

        return null;
    }

    public static UserDao getUserInfoById(int idUser) throws SQLException {
        String query = "SELECT nome, username, email, is_admin FROM usuario WHERE id_usuario = ?";
        UserDao userDao = new UserDao();
        PreparedStatement ps = null;
        Connection connection = null;
        ResultSet rs = null;

        try {
            connection = DBConnection.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, idUser);
            rs = ps.executeQuery();

            while (rs.next()) {
                userDao.setAdmin(rs.getBoolean("is_admin"));
                userDao.setName(rs.getString("nome"));
                userDao.setUsername(rs.getString("username"));
                userDao.setEmail(rs.getString("email"));
            }

            return userDao;
        } catch (SQLException exception) {
            LOGGER.severe("An error has occurred while trying to fetch user data access from the database");
            exception.printStackTrace();
            NotificationHandler.errorDialog(exception);
        } finally {
            ps.close();
            rs.close();
            connection.close();
        }
        return null;
    }
}
