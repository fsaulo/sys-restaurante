package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.etc.Note;
import org.sysRestaurante.model.Reminder;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DashbordController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DashbordController.class.getName());
    private ArrayList<Note> notesList;

    @FXML
    private Button clearNotesButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox notesPane;

    public void initialize() {
        AppFactory.setDashbordController(this);
        HBox footer = AppFactory.getAppController().getFooter();
        borderPane.setBottom(footer);
        clearNotesButton.setOnMouseClicked(e -> showClearAlertWindow());
        Platform.runLater(() -> notesPane.requestFocus());
        reloadNotes();

        LOGGER.info("At dashboard flow.");
    }

    public void reloadNotes() {
        removeNotesFromList();
        updateNotesList();
        for (Note item : notesList) {
            CheckBox box = new CheckBox(item.getContent());
            box.setWrapText(true);
            box.setOnMouseClicked(e -> {
                if (box.isSelected())
                    new Reminder().check(item.getIdNote());
                else
                    new Reminder().uncheck(item.getIdNote());
            });
            if (item.isChecked())
                box.setSelected(true);
            notesPane.getChildren().add(box);
        }
    }

    public void showNotesWindow() {
        AppController.showDialog(SceneNavigator.NOTE_PANE);
    }

    public void showClearAlertWindow() {
        AppController.showDialog(SceneNavigator.CLEAR_NOTES_ALERT);
    }

    public void removeNotesFromList() {
        if (notesList != null) notesList.clear();
        notesPane.getChildren().removeAll(notesPane.getChildren());
    }

    public void updateNotesList() {
        try {
            notesList = new Reminder().getAllPermanentNotes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNoteToList(Note note) {
        notesList.add(note);
    }
}
