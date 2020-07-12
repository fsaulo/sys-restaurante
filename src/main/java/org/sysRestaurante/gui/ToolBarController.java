package org.sysRestaurante.gui;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.util.Animation;
import org.sysRestaurante.util.ExceptionHandler;
import org.sysRestaurante.util.LoggerHandler;

import java.util.logging.Logger;

public class ToolBarController extends AppFactory {

    @FXML
    private VBox vBoxMenuPrincipal;
    @FXML
    private ToggleButton toggleMenuPrincipal;
    @FXML
    private ToggleButton toggleMenuFerramentas;
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
    private ToggleGroup menuGroup;
    @FXML
    private ToggleGroup submenuGroup;
    @FXML
    private ToggleButton g2a;
    @FXML
    private ToggleButton toggleGerenciarProdutos;
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
    private ToggleButton g5a;
    @FXML
    private ToggleButton g5b;
    @FXML
    private ToggleButton g5c;
    @FXML
    private VBox vBoxMenuFerramentas;
    @FXML
    private VBox vboxG3;
    @FXML
    private VBox vboxG4;
    @FXML
    private VBox vboxG5;
    @FXML
    private Label userLabel;
    @FXML
    private Label dashboardLinkLabel;

    private static final Logger LOGGER = LoggerHandler.getGenericConsoleHandler(ToolBarController.class.getName());

    public void initialize() {
        AppFactory.setToolBarController(this);
        UserDao user = null;
        String adminAccessString = "SysRestaurante | Adminstração";
        String employeeAccessString = "SysRestaurante | Funcionário";

        try {
            user = AppFactory.getUserDao();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Trying to access null user object.");
        }

        this.clearToggleGroup(menuGroup, submenuGroup);
        userLabel.setText("Olá, " + user.getName());

        if (user.isAdmin()) {
            dashboardLinkLabel.setText(adminAccessString);
        } else {
            dashboardLinkLabel.setText(employeeAccessString);
        }
    }

    public void unfoldSubmenus(VBox box, ToggleButton... toggleSubmenus) {
        if (box.getChildren().isEmpty()) {
            box.getChildren().addAll(toggleSubmenus);
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

    @FXML
    public void menuPrincipal(Event event) {
        unfoldSubmenus(vBoxMenuPrincipal,
                toggleGerenciarBalcao,
                toggleComandas,
                toggleHistoricoCaixa,
                togglePainelCardapio,
                togglePedidos);
        event.consume();
    }

    @FXML
    public void menuFerramentas(Event event) {
        unfoldSubmenus(vBoxMenuFerramentas,
                g2a,
                toggleGerenciarProdutos,
                g2c,
                g2d);
        event.consume();
    }

    public void selectMenuPrincipal() {
        toggleMenuPrincipal.setSelected(true);
    }

    public void selectMenuFerramentas() {
        toggleMenuFerramentas.setSelected(true);
    }

    @FXML
    public void dashboard(MouseEvent event) {
        untoggleGroup(submenuGroup);
        AppFactory.getAppController().loadPage(event, SceneNavigator.DASHBOARD);
    }

    @FXML
    public void submenuGerenciarBalcao(MouseEvent event) {
        if (!toggleGerenciarBalcao.isSelected()) {
            toggleGerenciarBalcao.setSelected(true);
        }

        selectMenuPrincipal();
        AppFactory.getAppController().loadPage(event, SceneNavigator.CASHIER);
    }

    @FXML
    public void submenuComandas(MouseEvent event) {
        selectMenuPrincipal();
        AppFactory.getAppController().loadPage(event, SceneNavigator.MANAGE_COMANDA);
    }

    @FXML
    public void submenuHistoricoCaixa(Event event) {
        selectMenuPrincipal();
        AppFactory.getAppController().loadPage(event, SceneNavigator.CASHIER_HISTORY_VIEW);
    }

    @FXML
    public void submenuPedidos() {
        selectMenuPrincipal();
    }

    @FXML
    public void submenuPainelCardapio() {
        selectMenuPrincipal();
    }

    @FXML
    public void submenuProdutos(Event event) {
        if (!toggleGerenciarProdutos.isSelected()) {
            toggleGerenciarProdutos.setSelected(true);
        }

        selectMenuFerramentas();
        AppFactory.getAppController().loadPage(event, SceneNavigator.PRODUCT_MANAGEMENT_VIEW);
    }

    public void menuG3(ActionEvent event) {
        unfoldSubmenus(vboxG3, g3a, g3b, g3c, g3d);
    }

    public void menuG4(ActionEvent event) {
        unfoldSubmenus(vboxG4, g4a, g4b, g4c);
    }

    public void menuG5(ActionEvent event) {
        unfoldSubmenus(vboxG5, g5a, g5b, g5c);
    }

    public void clearToggleGroup(ToggleGroup... grupoMenu) {
        for (ToggleGroup grupo : grupoMenu)
        {
            grupo.selectedToggleProperty()
                    .addListener((ObservableValue<? extends Toggle> obs, Toggle old, Toggle novo) -> {
                    if (grupo.getSelectedToggle() == null) {
                        grupo.selectToggle(old);
                    }});
        }
    }

    public void untoggleGroup(ToggleGroup group) {
        ToggleButton toggle = new ToggleButton();
        toggle.setToggleGroup(group);
        toggle.setSelected(true);
    }

    public void onLogoutRequest(MouseEvent event) {
        try {
            event.consume();
            AppFactory.setUserDao(null);
            LOGGER.info("User logged out");
            AppFactory.getLoginController().storeLastSessionDuration();
            AppFactory.getMainGUI().restartProgram();
        } catch (Exception e) {
            ExceptionHandler.incrementGlobalExceptionsCount();
            LOGGER.severe("Couldn't log out due to an exception.");
            e.printStackTrace();
        }
    }
}
