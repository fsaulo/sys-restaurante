package org.sysRestaurante.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KitchenTicketBuilder {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    public static byte[] build(
            String title,
            String orderId,
            String table,
            List<String> items,
            String notes,
            boolean isUrgent
    ) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(EscPos.INIT);
        out.write(EscPos.CODEPAGE_CP860);

        out.write(EscPos.alignCenter());
        out.write(EscPos.FONT_DOUBLE);
        out.write(EscPos.boldOn());

//        if (isUrgent) {
//            out.write(EscPos.invertOn());
//        }

//        out.write(EscPos.text(title.toUpperCase()));
//        out.write(EscPos.newLine());

//        if (isUrgent) {
//            out.write(EscPos.invertOff());
//        }

        out.write(EscPos.boldOff());
        out.write(EscPos.FONT_NORMAL);
        out.write(EscPos.newLine());

        out.write(EscPos.FONT_TRIPLE);
        out.write(EscPos.text("MESA " + table));
        out.write(EscPos.newLine());

        out.write(EscPos.FONT_DOUBLE);
        out.write(EscPos.text("PEDIDO " + orderId));
        out.write(EscPos.newLine());

        out.write(EscPos.FONT_NORMAL);
        out.write(EscPos.text(
                LocalDateTime.now().format(TIME_FMT)
        ));
        out.write(EscPos.newLine());
        out.write(EscPos.newLine());

        out.write(EscPos.alignLeft());
        out.write(EscPos.FONT_DOUBLE);

        for (String item : items) {
            out.write(EscPos.text(item));
            out.write(EscPos.newLine());
        }

        out.write(EscPos.FONT_NORMAL);
        out.write(EscPos.newLine());

        if (notes != null && !notes.isBlank()) {
            out.write(EscPos.boldOn());
            out.write(EscPos.text("OBS:"));
            out.write(EscPos.boldOff());
            out.write(EscPos.newLine());

            out.write(EscPos.text(notes));
            out.write(EscPos.newLine());
        }

        out.write(EscPos.feed(4));
        out.write(EscPos.CUT);

        return out.toByteArray();
    }
}
