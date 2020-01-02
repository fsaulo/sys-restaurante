package sysRestaurante.gui;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sysRestaurante.util.Animation;
import sysRestaurante.util.ExceptionHandler;
import sysRestaurante.util.LoggerHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class MenuToolBarController {

    public MenuToolBarController() { }

    @FXML
    private VBox vBoxMenuPrincipal;
    @FXML
    private ToggleButton toggleMenuPrincipal;
    @FXML
    private ToggleButton toggleGerenciarBalcao;
    @FXML
    private ToggleButton toggleComandas;
    @FXML
    private ToggleButton togglePedidos;
    @FXML
    private ToggleButton toggleHistoricoCaixa;
    @FXML
    private ToggleButton togglePainelCardapio;
    @FXML
    private ToggleButton g2;
    @FXML
    private ToggleButton g3;
    @FXML
    private ToggleButton g4;
    @FXML
    private ToggleButton g5;
    @FXML
    private ToggleButton g6;
    @FXML
    private ToggleGroup menuGroup;
    @FXML
    private ToggleGroup submenuGroup;
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
    private ToggleButton g4a;
    @FXML
    private ToggleButton g4b;
    @FXML
    private ToggleButton g4c;
    @FXML
    private VBox vboxG2;
    @FXML
    private VBox vboxG3;
    @FXML
    private VBox vboxG4;
    @FXML
    private VBox vboxHolder;
    @FXML
    private Label userLabel;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(MenuToolBarController.class.getName());

    public void initialize() {
        this.clearToggleGroup(menuGroup, submenuGroup);
        userLabel.setText("Olá, Saulo");
    }

    public void unfoldSubmenus(ToggleButton menu, VBox box, ToggleButton... toggleSubmenus) {
        if (box.getChildren().isEmpty()) {
            box.getChildren().addAll(toggleSubmenus);
            Animation.fade(box);
        } else {
            hideSubmenus(box);
        }
    }

    public void onLogoutRequest(ActionEvent event) {
        try {
            MainGUI.restartProgram(new Stage());
            LOGGER.info("User logged out");
        } catch (IOException e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Couldn't log out due to IOException.");
            e.printStackTrace();
        }
    }

    public void hideSubmenus(VBox... boxes) {
        for (VBox box : boxes) {
            box.getChildren().clear();
        }
    }

    public void menuPrincipal(ActionEvent event) {
        unfoldSubmenus(
                toggleMenuPrincipal,
                vBoxMenuPrincipal,
                toggleGerenciarBalcao,
                toggleComandas,
                toggleHistoricoCaixa,
                togglePainelCardapio,
                togglePedidos
        );
    }

    public void menuG2(ActionEvent event) {
        unfoldSubmenus(g2, vboxG2, g2a, g2b, g2c, g2d);
    }

    public void menuG3(ActionEvent event) {
        unfoldSubmenus(g3, vboxG3, g3a, g3b, g3c, g3d);
    }

    public void menuG4(ActionEvent event) {
        unfoldSubmenus(g4, vboxG4, g4a, g4b, g4c);
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
