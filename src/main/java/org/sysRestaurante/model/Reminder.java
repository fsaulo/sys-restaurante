package org.sysRestaurante.model;

import org.sysRestaurante.dao.NoteDao;
import org.sysRestaurante.util.DBConnection;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Reminder {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Personnel.class.getName());

    public void insert(int idUser, String content, LocalDate date) {
        PreparedStatement ps;
        Connection con;
        String query = "INSERT INTO lembrete (id_usuario, conteudo, data) VALUES (?, ?, ?)";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idUser);
            ps.setString(2, content);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException ex) {
            LOGGER.severe("Couldn't record annotation.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        }
    }

    public ArrayList<NoteDao> getAllPermanentNotes() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = null;
        String query = "SELECT * FROM lembrete";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            ArrayList<NoteDao> noteDaos = new ArrayList<>();

            while (rs.next()) {
                NoteDao noteDao = new NoteDao(rs.getString("conteudo"));
                noteDao.setIdUser(rs.getInt("id_usuario"));
                noteDao.setIdNote(rs.getInt("id_lembrete"));
                noteDao.setDate(rs.getDate("data").toLocalDate());
                noteDao.setChecked(rs.getBoolean("is_marcado"));
                noteDaos.add(noteDao);
            }
            return noteDaos;
        } catch (SQLException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        } finally {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
            if (con != null) con.close();
        }
        return null;
    }

    public void removeChecked() throws SQLException {
        PreparedStatement ps = null;
        Connection con = null;
        String query = "DELETE FROM lembrete WHERE is_marcado = ?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setBoolean(1, true);
            ps.executeUpdate();
        } catch (SQLException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            e.printStackTrace();
        } finally {
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    public void check(int id) {
        PreparedStatement ps;
        Connection con;
        String query = "UPDATE lembrete SET is_marcado = ? WHERE id_lembrete = ?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setBoolean(1, true);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void uncheck(int id) {
        PreparedStatement ps;
        Connection con;
        String query = "UPDATE lembrete SET is_marcado = ? WHERE id_lembrete = ?";
        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setBoolean(1, false);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
