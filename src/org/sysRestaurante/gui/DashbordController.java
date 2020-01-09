package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.etc.Note;
import org.sysRestaurante.model.Annotation;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DashbordController {
    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DashbordController.class.getName());
    private static long timerInMillies;

    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox notesPane;
    @FXML
    private Button clearNotesButton;

    private Label sessionTimer = new Label();
    private ArrayList<Note> notesList;

    public void initialize() {
        AppFactory.setDashbordController(this);
        HBox footer = AppFactory.getAppController().getFooter();
        borderPane.setBottom(footer);
        clearNotesButton.setOnMouseClicked(e -> showClearAlertWindow());
        reloadNotes();
        LOGGER.info("At dashboard flow.");
    }

    public void reloadNotes() {
        notesPane.getChildren().removeAll(notesPane.getChildren());
        updateNotesList();
        for (Note item : notesList) {
            CheckBox box = new CheckBox(item.getContent());
            box.setWrapText(true);
            box.setOnMouseClicked(e -> {
                if (box.isSelected())
                    new Annotation().check(item.getIdNote());
                else
                    new Annotation().uncheck(item.getIdNote());
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
        notesList.clear();
    }

    public void updateNotesList() {
        try {
            notesList = new Annotation().getAllPermanentNotes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNoteToList(Note note) {
        notesList.add(note);
    }

}
