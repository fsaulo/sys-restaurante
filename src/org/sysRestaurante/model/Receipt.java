package org.sysRestaurante.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.util.CurrencyField;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Receipt {

    private StringBuilder receipt;
    private String strSubtotal;
    private String strDiscount;
    private String strTotal;
    private String strFuncName;
    private String strDate;
    private String strTime;
    private String strCompanyName;
    private String strCompanyAddress;
    private String strCompanyTel;
    private String strCompanyCNPJ;
    private ArrayList<ProductDao> productList;
    private final static String NON_THIN = "[^iIl1\\.,']";

    public Receipt(ArrayList<ProductDao> productList, OrderDao order) {
        UserDao func = AppFactory.getUserDao();
        double subtotal = order.getTotal();
        double discount = order.getDiscount();
        double total = subtotal * (1 - discount/100);
        this.productList = productList;
        strCompanyName = center("NOME DA EMPRESA");
        strCompanyAddress = center("Av. Brasil, 2113, Aracaju-SE");
        strCompanyTel = center("CONTATO: (44) 91214-5566");
        strCompanyCNPJ = center("CNPJ: 00.000.000/0001-00");
        strFuncName = func.getName();
        strDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(order.getOrderDate());
        strTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(order.getOrderTime());
        strSubtotal = CurrencyField.getBRLCurrencyFormat().format(subtotal);
        strTotal = CurrencyField.getBRLCurrencyFormat().format(total);
        strDiscount = (int) discount + "%";
        buildReceipt();
    }

    public Receipt() {
        strCompanyName = center("NOME DA EMPRESA");
        strCompanyAddress = center("Av. Brasil, 2113, Aracaju-SE");
        strCompanyTel = center("CONTATO: (44) 91214-5566");
        strCompanyCNPJ = center("CNPJ: 00.000.000/0001-00");
        strSubtotal = "R$ 0,00";
        strDiscount = "0%";
        strTotal = "R$ 0,00";
        strFuncName = "";
        strDate = "01/01/2020";
        strTime = "00:00:00";
        buildReceipt();
    }

    public String getReceipt() {
        return this.receipt.toString();
    }

    public void buildReceipt() {
        receipt = new StringBuilder();
        receipt.append(getHeader());
        receipt.append(getBody());
        receipt.append(getFooter());
    }

    public static String center(String string) {
        int length = 50;
        char pad = ' ';
        StringBuilder sb = new StringBuilder(length);
        sb.setLength((length - string.length()) / 2);
        sb.append(string);
        sb.setLength(length);
        return sb.toString().replace('\0', pad);
    }

    private static int textWidth(String str) {
        return str.length() - str.replaceAll(NON_THIN, "").length() / 2;
    }

    public static String ellipsize(String text, int max) {
        if (textWidth(text) <= max)
            return text;
        int end = text.lastIndexOf(' ', max - 3);
        if (end == -1)
            return text.substring(0, max-3) + "..";
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);
            if (newEnd == -1) newEnd = text.length();
        } while (textWidth(text.substring(0, newEnd) + "..") < max);
        return text.substring(0, end) + "..";
    }

    private String getHeader() {
        String sep1 = "--------------------------------------------------";
        String msg1 = center("ESSE RECIBO NÃO É CUPOM FISCAL");
        String header = strCompanyName + "\n" +
                        strCompanyAddress + "\n" +
                        strCompanyTel + "\n" +
                        strCompanyCNPJ + "\n\n" +
                        sep1 + "\n" +
                        msg1 + "\n" +
                        sep1 + "\n\n";
        return header;
    }

    private String getBody() {
        String sep1 = "--------------------------------------------------";
        String strItem;
        String strQty;
        String strPrice;
        String title = String.format("%-28s %10s %10s\n", "Item", "Qtd", "Preço");
        StringBuilder body = new StringBuilder();
        body.append(title).append(sep1);
        body.append("\n");

        if (productList != null) {
            for (ProductDao item : productList) {
                strItem = ellipsize(item.getDescription(), 30);
                strQty = "x" + item.getQuantity();
                strPrice = CurrencyField.getBRLCurrencyFormat().format(item.getSellPrice());
                body.append(String.format("%-28s %10s %10s\n", strItem, strQty, strPrice));
            }
        }

        return body.toString();
    }

    public String getFooter() {
        String sep1 = "--------------------------------------------------";
        String sep2 = "==================================================";
        String subtotal = String.format("%-24s %25s", "SUBTOTAL:", strSubtotal);
        String total = String.format("%-24s %25s", "TOTAL:", strTotal);
        String discount = String.format("%-24s %25s", "DESCONTOS APLICADOS:", strDiscount);
        String msg1 = center("OBRIGADO, VOLTE SEMPRE!");
        String dateTime = String.format("%-24s %25s", "DATA: " + strDate, "HORA: " + strTime);
        String footer = "\n\n" + sep1 + "\n" +
                        subtotal + "\n" +
                        discount + "\n" +
                        total + "\n" +
                        sep1 + "\n" +
                        "NOME FUNC. " + strFuncName + "\n" +
                        dateTime + "\n" +
                        sep1 + "\n" +
                        msg1 + "\n" +
                        sep1 + "\n";
        return footer;
    }

    public WritableImage getReceiptImageFile() {
        Text rpp = new Text(getReceipt());
        rpp.setFont(new Font("DejaVu Sans Mono", 13));
        TextFlow receipt = new TextFlow();
        receipt.getChildren().add(rpp);
        receipt.setTextAlignment(TextAlignment.JUSTIFY);
        receipt.setMinWidth(395);
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(3));
        stackPane.getChildren().add(receipt);
        return stackPane.snapshot(new SnapshotParameters(), null);
    }

    public void saveReceiptAsPng() throws MalformedURLException {
        WritableImage nodeshot = getReceiptImageFile();
        int idOrder = AppFactory.getOrderDao().getIdOrder();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("SysRecibo_Caixa_Cod" + idOrder + "_" + LocalDate.now() + ".png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            if(!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".png");
            } else {
                if (!file.getName().endsWith(".png")) {
                    throw new MalformedURLException(file.getName() + " has no valid file-extension.");
                }
            }

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(nodeshot, null), "png", file);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Informação do sistema");
                alert.setHeaderText(null);
                alert.setContentText("Recibo salvo com sucesso");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.addSuppressed(new Throwable());
            }
        }
    }
}
