package org.sysRestaurante.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.etc.Note;
import org.sysRestaurante.etc.User;
import org.sysRestaurante.model.Annotation;
import org.sysRestaurante.util.ExceptionHandler;

import java.sql.SQLException;
import java.time.LocalDate;

public class NotePaneController {

    @FXML
    private CheckBox isPermanentNote;
    @FXML
    private TextArea textNote;


    public void addNote(ActionEvent event) {
        if (!textNote.getText().equals("")) {
            if (isPermanentNote.isSelected()) {
                Annotation notesDB = new Annotation();
                User userData = AppFactory.getUser();
                try {
                    notesDB.insert(userData.getIdUsuario(), textNote.getText(), LocalDate.now());
                } catch (SQLException ex) {
                    ExceptionHandler.incrementGlobalExceptionsCount();
                    ex.printStackTrace();
                }
            }
            AppFactory.getDashbordController().addNoteToList(new Note(textNote.getText()));
            AppFactory.getDashbordController().reloadNotes();
        }
        textNote.clear();
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public void cancel(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
