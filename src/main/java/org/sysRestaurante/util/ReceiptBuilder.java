package org.sysRestaurante.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ReceiptBuilder {

    public static byte[] buildSampleReceipt() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(EscPos.INIT);
        out.write(EscPos.CODEPAGE_CP860);

        out.write(EscPos.alignCenter());
        out.write(EscPos.boldOn());
        out.write(EscPos.newLine());
        out.write(EscPos.text("MINHA LOJA LTDA"));
        out.write(EscPos.newLine());
        out.write(EscPos.boldOff());

        out.write(EscPos.text("CNPJ: 12.345.678/0001-99"));
        out.write(EscPos.newLine());
        out.write(EscPos.text("Rua Exemplo, 123"));
        out.write(EscPos.newLine());
        out.write(EscPos.newLine());

        out.write(EscPos.alignLeft());
        out.write(EscPos.text("Cupom Não Fiscal"));
        out.write(EscPos.newLine());
        out.write(EscPos.text("-------------------------------"));
        out.write(EscPos.newLine());

        out.write(EscPos.text("Produto A        1x  10,00"));
        out.write(EscPos.newLine());
        out.write(EscPos.text("Produto B        2x  15,00"));
        out.write(EscPos.newLine());

        out.write(EscPos.newLine());
        out.write(EscPos.boldOn());
        out.write(EscPos.text("TOTAL: R$ 40,00"));
        out.write(EscPos.boldOff());
        out.write(EscPos.newLine());

        out.write(EscPos.newLine());
        out.write(EscPos.alignCenter());
        out.write(EscPos.text("Obrigado pela preferência!"));
        out.write(EscPos.newLine());

        out.write(EscPos.feed(4));
        out.write(EscPos.CUT);

        return out.toByteArray();
    }
}
