package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.NoteDao;
import org.sysRestaurante.model.Reminder;
import org.sysRestaurante.util.LoggerHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DashboardController {

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(DashboardController.class.getName());
    private ArrayList<NoteDao> notesList;

    @FXML
    private Button clearNotesButton;
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox notesPane;

    public void initialize() {
        AppFactory.setDashboardController(this);
        borderPane.setBottom(AppFactory.getAppController().getFooter());
        borderPane.setTop(AppFactory.getAppController().getHeader());
        clearNotesButton.setOnMouseClicked(e -> showClearAlertWindow());
        Platform.runLater(() -> notesPane.requestFocus());
        reloadNotes();

        LOGGER.info("At dashboard flow.");
    }

    public void reloadNotes() {
        removeNotesFromList();
        updateNotesList();
        for (NoteDao item : notesList) {
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

    public void addNoteToList(NoteDao noteDao) {
        notesList.add(noteDao);
    }
}
