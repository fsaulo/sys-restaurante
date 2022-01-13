package org.sysRestaurante.model;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.sysRestaurante.applet.AppFactory;
import org.sysRestaurante.dao.OrderDao;
import org.sysRestaurante.dao.ProductDao;
import org.sysRestaurante.dao.UserDao;
import org.sysRestaurante.gui.formatter.CurrencyField;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Receipt {

    public StringBuilder receipt;
    public String strSubtotal;
    public String strDiscount;
    public String strTotal;
    public final String strFuncName;
    public String strDate;
    public String strTime;
    public String strCompanyName;
    public String strCompanyAddress;
    public String strCompanyTel;
    public String strCompanyCNPJ;
    public String strTaxes;
    public ArrayList<ProductDao> productList;
    public static final String NON_THIN = "[^iIl1\\.,']";
    public int length;

    public Receipt(OrderDao order, ArrayList<ProductDao> productList) {
        UserDao func = AppFactory.getUserDao();
        double subtotal = order.getTotal();
        double discount = order.getDiscount();
        double taxes = order.getTaxes();
        double total = subtotal - discount + taxes;
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
        strTaxes = CurrencyField.getBRLCurrencyFormat().format(taxes);
        strDiscount = CurrencyField.getBRLCurrencyFormat().format(discount);
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
        strTaxes = "";
        buildReceipt();
    }

    public String getReceipt() {
        return receipt.toString();
    }

    public void buildReceipt() {
        receipt = new StringBuilder();
        receipt.append(getHeader());
        receipt.append(getBody());
        receipt.append(getFooter());
    }

    public String center(String string) {
        length = 50;
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
        return strCompanyName + "\n" +
                        strCompanyAddress + "\n" +
                        strCompanyTel + "\n" +
                        strCompanyCNPJ + "\n\n" +
                        sep1 + "\n" +
                        msg1 + "\n" +
                        sep1 + "\n\n";
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
                strItem = ellipsize(item.getDescription(), 25);
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
        String taxes = String.format("%-24s %25s", "TAXA DE SERVICO: ", strTaxes);
        String msg1 = center("OBRIGADO PELA PREFERÊNCIA.");
        String msg2 = center("VOLTE SEMPRE!");
        String dateTime = String.format("%-24s %25s", "DATA: " + strDate, "HORA: " + strTime);
        return "\n\n" + sep2 + "\n" +
                        subtotal + "\n" +
                        discount + "\n" +
                        taxes + "\n" +
                        total + "\n" +
                        sep1 + "\n" +
                        "NOME FUNC. " + strFuncName + "\n" +
                        dateTime + "\n" +
                        sep2 + "\n\n" +
                        msg1 + "\n" +
                        msg2;
    }

    public Node getReceiptAsNode() {
        Text rpp = new Text(getReceipt());
        rpp.setFont(new Font("DejaVu Sans Mono", 13));
        TextFlow receipt = new TextFlow();
        receipt.getChildren().add(rpp);
        receipt.setTextAlignment(TextAlignment.JUSTIFY);
        receipt.setMinWidth(395);
        StackPane stackPane = new StackPane();
        stackPane.setPadding(new Insets(3));
        stackPane.getChildren().add(receipt);
        return stackPane;
    }

    public WritableImage getReceiptImageFile() {
        return getReceiptAsNode().snapshot(new SnapshotParameters(), null);
    }

    public void saveReceiptAsPng(Window owner) throws MalformedURLException {
        WritableImage nodeshot = getReceiptImageFile();
        int idOrder = AppFactory.getOrderDao().getIdOrder();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("SysRecibo_Caixa_Cod" + idOrder + "_" + LocalDate.now() + ".png");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        fileChooser.setTitle("Salvar recibo");
        File file = fileChooser.showSaveDialog(owner);

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
                alert.initOwner(owner);
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.addSuppressed(new Throwable());
            }
        }
    }

    public static class ThinReceipt extends Receipt {
        final ArrayList<ProductDao> productList;
        StringBuilder rc;

        public ThinReceipt(OrderDao order, ArrayList<ProductDao> pdL) {
            this.productList = pdL;
            strCompanyName = center("NOME DA EMPRESA");
            strCompanyAddress = center("Av. Brasil, 2113, Aracaju-SE");
            strCompanyTel = center("CONTATO: (44) 91214-5566");
            strCompanyCNPJ = center("CNPJ: 00.000.000/0001-00");
            double subtotal = order.getTotal();
            double discount = order.getDiscount();
            double taxes = order.getTaxes();
            double total = subtotal - discount + taxes;
            strCompanyName = center("NOME DA EMPRESA");
            strCompanyAddress = center("Av. Brasil, 2113, Aracaju-SE");
            strCompanyTel = center("CONTATO: (44) 91214-5566");
            strCompanyCNPJ = center("CNPJ: 00.000.000/0001-00");
            strDate = DateTimeFormatter.ofPattern("dd-MM-yyyy").format(order.getOrderDate());
            strTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(order.getOrderTime());
            strSubtotal = CurrencyField.getBRLCurrencyFormat().format(subtotal);
            strTotal = CurrencyField.getBRLCurrencyFormat().format(total);
            strTaxes = CurrencyField.getBRLCurrencyFormat().format(taxes);
            strDiscount = CurrencyField.getBRLCurrencyFormat().format(discount);
            buildReceipt();
        }

        public String center(String string) {
            length = 35;
            char pad = ' ';
            StringBuilder sb = new StringBuilder(length);
            sb.setLength((length - string.length()) / 2);
            sb.append(string);
            sb.setLength(length);
            return sb.toString().replace('\0', pad);
        }

        private String getHeader() {
            String sep1 = "----------------------------------";
            String msg1 = center("ESSE RECIBO NÃO É CUPOM FISCAL");
            return strCompanyName + "\n" +
                    strCompanyAddress + "\n" +
                    strCompanyTel + "\n" +
                    strCompanyCNPJ + "\n\n" +
                    sep1 + "\n" +
                    msg1 + "\n" +
                    sep1 + "\n\n";
        }

        private String getBody() {
            String sep1 = "----------------------------------";
            String strItem;
            String strQty;
            String strPrice;
            String title = String.format("%-15s %8s %8s\n", "Item", "Qtd", "Preço");
            StringBuilder body = new StringBuilder();
            body.append(title).append(sep1);
            body.append("\n");

            if (productList != null) {
                for (ProductDao item : productList) {
                    strItem = ellipsize(item.getDescription(), 15);
                    strQty = "x" + item.getQuantity();
                    strPrice = CurrencyField.getBRLCurrencyFormat().format(item.getSellPrice());
                    body.append(String.format("%-15s %8s %8s\n", strItem, strQty, strPrice));
                }
            }

            return body.toString();
        }

        public String getFooter() {
            String sep1 = "----------------------------------";
            String sep2 = "==================================";
            String subtotal = String.format("%-15s %17s", "SUBTOTAL:", strSubtotal);
            String total = String.format("%-15s %17s", "TOTAL:", strTotal);
            String msg1 = center("OBRIGADO PELA PREFERÊNCIA.");
            String msg2 = center("VOLTE SEMPRE!");
            String dateTime = String.format("%-18s %10s", "DATA: " + strDate, "HORA: " + strTime);
            return "\n\n" + sep2 + "\n" +
                    subtotal + "\n" +
                    total + "\n" +
                    sep1 + "\n" +
                    "NOME FUNC. " + strFuncName + "\n" +
                    dateTime + "\n" +
                    sep2 + "\n\n" +
                    msg1 + "\n" +
                    msg2;
        }

        public void buildReceipt() {
            rc = new StringBuilder();
            rc.append(getHeader());
            rc.append(getBody());
            rc.append(getFooter());
        }

        public String getReceipt() {
            return rc.toString();
        }
    }
}
