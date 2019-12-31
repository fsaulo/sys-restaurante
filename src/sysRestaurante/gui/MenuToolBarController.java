package sysRestaurante.gui;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import sysRestaurante.util.Animation;

public class MenuToolBarController {

    public MenuToolBarController() { }

    @FXML
    private ToggleButton g1;
    @FXML
    private ToggleButton g2;
    @FXML
    private ToggleButton g3;
    @FXML
    private ToggleButton g4;
    @FXML
    private ToggleButton g5;
    @FXML
    private ToggleGroup menuGroup;
    @FXML
    private ToggleGroup submenuGroup;
    @FXML
    private ToggleButton g1a;
    @FXML
    private ToggleButton g1b;
    @FXML
    private ToggleButton g1c;
    @FXML
    private ToggleButton g1d;
    @FXML
    private ToggleButton g2a;
    @FXML
    private ToggleButton g2b;
    @FXML
    private ToggleButton g2c;
    @FXML
    private ToggleButton g2d;
    @FXML
    private ToggleButton g3a;
    @FXML
    private ToggleButton g3b;
    @FXML
    private ToggleButton g3c;
    @FXML
    private ToggleButton g3d;
    @FXML
    private VBox vboxG1;
    @FXML
    private VBox vboxG2;
    @FXML
    private VBox vboxG3;

    public void initialize() {
        hideSubmenus(vboxG1, vboxG2, vboxG3);
        this.clearToggleGroup(menuGroup, submenuGroup);
    }

    public void unfoldSubmenus(ToggleButton menu, VBox box, ToggleButton... submenus) {
        if (box.getChildren().isEmpty()) {
            box.getChildren().addAll(submenus);
            Animation.fade(box);
        } else {
            hideSubmenus(box);
        }
    }

    public void hideSubmenus(VBox... boxes) {
        for (VBox box : boxes) {
            box.getChildren().clear();
        }
    }

    public void menuG1(ActionEvent event) {
        unfoldSubmenus(g1, vboxG1, g1a, g1b, g1c, g1d);
    }

    public void menuG2(ActionEvent event) {
        unfoldSubmenus(g2, vboxG2, g2a, g2b, g2c, g2d);
    }

    public void menuG3(ActionEvent event) {
        unfoldSubmenus(g3, vboxG3, g3a, g3b, g3c, g3d);
    }

    public void clearToggleGroup(ToggleGroup... grupoMenu) {
        for (ToggleGroup grupo : grupoMenu)
        {
            grupo.selectedToggleProperty().addListener(
                    (ObservableValue<? extends Toggle> obs, Toggle old, Toggle novo) -> {
                    if (grupo.getSelectedToggle() == null) {
                        grupo.selectToggle(old);
                    }});
        }
    }
}
