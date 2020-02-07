package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.NoteDao;
import org.sysRestaurante.model.Reminder;

import java.time.LocalDate;

public class AddNotesDialogController {

    @FXML
    private TextArea textNote;
    @FXML
    private HBox noteDialogBox;

    public void initialize() {
        Platform.runLater(() -> noteDialogBox.requestFocus());
    }

    public void addNote(ActionEvent event) {
        if (!textNote.getText().equals("")) {
            new Reminder().insert(
                    AppFactory.getUserDao().getIdUser(),
                    textNote.getText(),
                    LocalDate.now());
            AppFactory.getDashboardController().addNoteToList(new NoteDao(textNote.getText()));
            AppFactory.getDashboardController().reloadNotes();
        }
        textNote.clear();
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }

    public void cancel(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
