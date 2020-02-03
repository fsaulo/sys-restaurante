package org.sysRestaurante.gui;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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
                vbox.getChildren().addAll(label1, label2, label3);
                vbox.setStyle("-fx-background-color: #dddddd; -fx-border-color: black; -fx-cursor: hand");
                vbox.setSpacing(3);
                vbox.setPadding(new Insets(3));
                vbox.setOnMouseClicked(event -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Informação do sistema");
                    alert.setHeaderText(null);
                    alert.setContentText("Comanda " + item.getIdComanda() + " selecionada");
                    alert.initOwner(vbox.getScene().getWindow());
                    alert.showAndWait();
                });
                tilePane.getChildren().addAll(vbox);
            }
        }
    }
}
