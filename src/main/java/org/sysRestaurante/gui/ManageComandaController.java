package org.sysRestaurante.gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import org.controlsfx.control.PopOver;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.ComandaDao;
import org.sysRestaurante.gui.formatter.CurrencyField;
import org.sysRestaurante.model.Order;

import java.io.IOException;
import java.util.List;

public class ManageComandaController {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TilePane tilePane;
    @FXML
    private BorderPane borderPaneHolder;

    private PopOver popOver;

    @FXML
    public void initialize() {
        AppFactory.setManageComandaController(this);
        scrollPane.setFitToWidth(true);
        tilePane.setPrefColumns(50);
        borderPaneHolder.setTop(AppFactory.getAppController().getHeader());
        borderPaneHolder.setBottom(AppFactory.getAppController().getFooter());
        tilePane.getChildren().clear();
        listBusyTable();
    }

    @FXML
    public void onNewComandaClicked(Event event) {
        AppController.showDialog(SceneNavigator.NEW_COMANDA_DIALOG, scrollPane.getScene().getWindow());
        event.consume();
    }

    public void listBusyTable() {
        List<ComandaDao> openComandas = new Order().getComandasByIdCashier(AppFactory.getCashierDao().getIdCashier());
        for (ComandaDao item : openComandas) {
            if (item.getIdCategory() != 6) {
                try {
                    buildAndAddTiles(item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void buildAndAddTiles(ComandaDao comanda) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Label comandaCod = new Label("#" + comanda.getIdComanda());
        Label statusLabel = new Label("Ocupada");
        Label cashSpent = new Label(CurrencyField.getBRLCurrencyFormat().format(comanda.getTotal()));
        Label tableCod = new Label("MESA " + comanda.getIdTable());

        Pane pane = new Pane();
        HBox topTile = new HBox();
        HBox.setHgrow(pane, Priority.ALWAYS);
        topTile.getChildren().addAll(comandaCod, pane, tableCod);
        Circle icon = new Circle(4);
        icon.getStyleClass().add("circle-status");
        comandaCod.setStyle("-fx-font-size: 13px; -fx-opacity: 0.5");
        statusLabel.setGraphic(icon);
        tableCod.setStyle("-fx-font-size: 17px;");
        Separator sep = new Separator();
        sep.setMinHeight(3);

        VBox tile = new VBox();
        tile.getChildren().addAll(topTile, sep, statusLabel, cashSpent);
        tile.setAlignment(Pos.TOP_RIGHT);
        tile.setMinWidth(163);
        tile.getStylesheets().add("css/menu.css");
        tile.getStyleClass().add("comanda-tile");
        tile.setOnMouseEntered(event -> changeLabelStyle("white", comandaCod, statusLabel, cashSpent, tableCod));
        tile.setOnMouseExited(event -> changeLabelStyle("black", comandaCod, statusLabel, cashSpent, tableCod));
        tilePane.getChildren().addAll(tile);

        VBox popVbox = loader.load(MainGUI.class.getResource(SceneNavigator.COMANDA_VIEW));
        popOver = new PopOver(popVbox);
        tile.setOnMouseClicked(event -> popOver.show(tile));
    }

    public void changeLabelStyle(String color, Label... labels) {
        if (color.equals("white")) {
            for (Label label : labels)
                label.getStyleClass().add("label-tile-hover");
        } else {
            for (Label label : labels)
                label.getStyleClass().remove("label-tile-hover");
        }
    }

    public void refreshTileList() {
        initialize();
    }
}
