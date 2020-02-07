package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Order;

import java.util.List;

public class ManageComandaController {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TilePane tilePane;
    @FXML
    private BorderPane borderPaneHolder;

    @FXML
    public void initialize() {
        scrollPane.setFitToWidth(true);
        tilePane.setPrefColumns(50);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        listBusyTable();
    }

    public void listBusyTable() {
        List<ComandaDao> openComandas = new Order().getComandasByIdCashier(AppFactory.getCashierDao().getIdCashier());
        tilePane.getChildren().clear();
        for (ComandaDao item : openComandas) {
            if (item.getIdCategory() != 6) {
                buildAndAddTiles(item);
            }
        }
    }

    public void buildAndAddTiles(ComandaDao comanda) {
        VBox vbox = new VBox();
        Label comandaCod = new Label("#" + comanda.getIdComanda());
        Label statusLabel = new Label("Ocupada");
        Label cashSpent = new Label(CurrencyField.getBRLCurrencyFormat().format(comanda.getTotal()));
        Label tableCod = new Label("MESA " + comanda.getIdTable());
        HBox topTile = new HBox();
        Pane pane = new Pane();
        HBox.setHgrow(pane, Priority.ALWAYS);
        topTile.getChildren().addAll(comandaCod, pane, tableCod);
        Circle icon = new Circle(4);
        icon.getStyleClass().add("circle-status");
        comandaCod.setStyle("-fx-font-size: 13px; -fx-opacity: 0.5");
        statusLabel.setGraphic(icon);
        tableCod.setStyle("-fx-font-size: 17px;");
        Separator sep = new Separator();
        sep.setMinHeight(3);
        vbox.getChildren().addAll(topTile, sep, statusLabel, cashSpent);
        vbox.setAlignment(Pos.TOP_RIGHT);
        vbox.setMinWidth(163);
        vbox.getStylesheets().add("css/menu.css");
        vbox.getStyleClass().add("comanda-tile");
        tilePane.getChildren().addAll(vbox);
    }
}
