package org.sysRestaurante.util;

import java.nio.charset.Charset;

public class EscPos {

    public static final byte[] INIT = { 0x1B, 0x40 };
    public static final byte[] CUT = { 0x1D, 0x56, 0x01 };
    public static final byte[] FONT_NORMAL = { 0x1D, 0x21, 0x00 };
    public static final byte[] FONT_DOUBLE = { 0x1D, 0x21, 0x11 };
    public static final byte[] FONT_TRIPLE = { 0x1D, 0x21, 0x22 };

    public static final byte[] CODEPAGE_CP860 = { 0x1B, 0x74, 0x03 };

    private static final Charset CHARSET = Charset.forName("CP860");

    public static byte[] boldOn() {
        return new byte[]{ 0x1B, 0x45, 0x01 };
    }

    public static byte[] boldOff() {
        return new byte[]{ 0x1B, 0x45, 0x00 };
        }

    public static byte[] alignRight() {
        return new byte[]{ 0x1B, 0x61, 0x02 };
    }

    public static byte[] horizontalLine(char ch, int columns) {
        String line = String.valueOf(ch).repeat(columns);
        return EscPos.text(line);
    }

    public static byte[] alignCenter() {
        return new byte[]{ 0x1B, 0x61, 0x01 };
    }

    public static byte[] alignLeft() {
        return new byte[]{ 0x1B, 0x61, 0x00 };
    }

    public static byte[] newLine() {
        return new byte[]{ 0x0A };
    }

    public static byte[] feed(int lines) {
        return new byte[] { 0x1B, 0x64, (byte) lines };
    }

    public static byte[] invertOn() {
        return new byte[]{ 0x1D, 0x42, 0x01 };
    }

    public static byte[] invertOff() {
        return new byte[]{ 0x1D, 0x42, 0x00 };
    }

    public static byte[] text(String text) {
        return text.getBytes(CHARSET);
    }
}
