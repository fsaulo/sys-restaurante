package org.sysRestaurante.model;

import org.sysRestaurante.etc.Note;
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

public class Annotation {
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(Registration.class.getName());
    public void insert(int idUser, String content, LocalDate date) throws SQLException {
        PreparedStatement ps = null;
        Connection con = null;
        String query = "INSERT INTO anotacoes (idUsuario, conteudo, data) VALUES (?, ?, ?)";

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setInt(1, idUser);
            ps.setString(2, content);
            ps.setDate(3, java.sql.Date.valueOf(date));
            ps.executeUpdate();

        } catch (SQLException ex) {
            LOGGER.severe("Couldn't record annotation.");
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        } finally {
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    public ArrayList<Note> getAllPermanentNotes() throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM anotacoes";

        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            rs = ps.executeQuery();
            ArrayList<Note> notes = new ArrayList<>();
            while (rs.next()) {
                Note note = new Note(rs.getString("conteudo"));
                note.setIdUser(rs.getInt("idUsuario"));
                note.setIdNote(rs.getInt("idAnotacao"));
                note.setDate(rs.getDate("data").toLocalDate());
                note.setChecked(rs.getBoolean("isChecked"));
                notes.add(note);
            }
            return notes;
        } catch (SQLException ex) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            ex.printStackTrace();
        } finally {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        }
        return null;
    }

    public void removeAll() throws SQLException {
        PreparedStatement ps = null;
        String query = "DELETE FROM anotacoes";
        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.executeUpdate();
            con.close();
            ps.close();
        } catch (SQLException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            e.printStackTrace();
        } finally {
            if (ps != null) ps.close();
        }
    }

    public void check(int id) {
        PreparedStatement ps;
        String query = "UPDATE anotacoes SET isChecked = ? WHERE idAnotacao = ?";
        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setBoolean(1, true);
            ps.setInt(2, id);
            ps.executeUpdate();
            con.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void uncheck(int id) {
        PreparedStatement ps;
        String query = "UPDATE anotacoes SET isChecked = ? WHERE idAnotacao = ?";
        try {
            Connection con = DBConnection.getConnection();
            ps = con.prepareStatement(query);
            ps.setBoolean(1, false);
            ps.setInt(2, id);
            ps.executeUpdate();
            con.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
