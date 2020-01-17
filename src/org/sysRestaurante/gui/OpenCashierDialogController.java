package org.sysRestaurante.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.model.Cashier;
import org.sysRestaurante.util.CurrencyField;

import java.util.Locale;

public class OpenCashierDialogController {

    @FXML
    private Button continueButton;
    @FXML
    private Button cancelButton;
    @FXML
    private HBox cashBox;
    @FXML
    private TextArea addNote;

    public void initialize() {
        Cashier cashier = new Cashier();
        CurrencyField startCashField = new CurrencyField(new Locale("pt",  "BR"));
        startCashField.setPrefWidth(340);
        startCashField.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 25));
        cashBox.getChildren().add(startCashField);
        Platform.runLater(startCashField::requestFocus);

        continueButton.setOnMouseClicked(event -> {
            cashier.open(AppFactory.getUserDao().getIdUsuario(), startCashField.getAmount(), addNote.getText());
            ((Node) event.getSource()).getScene().getWindow().hide();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informação do sistema");
            alert.setHeaderText(null);
            alert.setContentText("Caixa aberto com sucesso!");
            alert.showAndWait();
        });
        cancelButton.setCancelButton(true);
        cancelButton.setOnMouseClicked(event -> ((Node) event.getSource()).getScene().getWindow().hide());
    }
}
