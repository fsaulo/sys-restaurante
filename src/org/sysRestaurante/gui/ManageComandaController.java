package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
        tileListBusyTables();
    }

    public void tileListBusyTables() {
        List<ComandaDao> openComandas = new Order().getComandasByIdCashier(AppFactory.getCashierDao().getIdCashier());
        tilePane.getChildren().clear();
        for (ComandaDao item : openComandas) {
            if (item.getStatus().equals("Aguardando preparo")) {
                VBox vbox = new VBox();
                Label label1 = new Label("Comanda #" + item.getIdComanda());
                Label label2 = new Label("Ocupado");
                Label label3 = new Label(CurrencyField.getBRLCurrencyFormat().format(item.getTotal()));
                Label label4 = new Label("MESA #" + item.getIdTable());
                label1.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold");
                label2.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold");
                label3.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold");
                label4.setStyle("-fx-font: Carlito 17; -fx-font-weight: bold");
                Circle icon = new Circle(4);
                icon.setFill(Color.ORANGE);
                label2.setGraphic(icon);
                Separator sep = new Separator();
                sep.setMinHeight(3);
                vbox.getChildren().addAll(label4, sep, label1, label2, label3);
                vbox.setStyle("-fx-background-color: #dddddd; -fx-border-color: black; -fx-cursor: hand");
                vbox.setSpacing(3);
                vbox.setPadding(new Insets(3));
                vbox.setAlignment(Pos.TOP_RIGHT);
                vbox.setMinWidth(150);
                vbox.setOnMouseClicked(event -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Informação do sistema");
                    alert.setHeaderText(null);
                    alert.setContentText("Comanda " + item.getIdComanda() + " selecionada");
                    alert.initOwner(vbox.getScene().getWindow());
                    alert.showAndWait();
                });

                vbox.setOnMouseEntered(event -> {
                    vbox.setStyle("-fx-background-color: gray; -fx-border-color: black; -fx-cursor: hand");
                    label1.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: white");
                    label2.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: white");
                    label3.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: white");
                    label4.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: white");
                });

                vbox.setOnMouseExited(event -> {
                    vbox.setStyle("-fx-background-color: #dddddd; -fx-border-color: black; -fx-cursor: hand");
                    label1.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: black");
                    label2.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: black");
                    label3.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: black");
                    label4.setStyle("-fx-font: Carlito 15; -fx-font-weight: bold; -fx-text-fill: black");
                });

                icon.setOnMouseEntered(event -> icon.setRadius(6));
                icon.setOnMouseExited(event -> icon.setRadius(4));
                tilePane.getChildren().addAll(vbox);
            }
        }
    }
}
