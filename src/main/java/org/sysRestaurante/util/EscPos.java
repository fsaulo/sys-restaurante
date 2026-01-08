package org.sysRestaurante.util;

import java.nio.charset.Charset;

class EscPos {

    public static final byte[] INIT = { 0x1B, 0x40 };
    public static final byte[] CUT = { 0x1D, 0x56, 0x01 };

    public static final byte[] CODEPAGE_CP860 = { 0x1B, 0x74, 0x03 };

    private static final Charset CHARSET = Charset.forName("CP860");

    public static byte[] boldOn() {
        return new byte[]{ 0x1B, 0x45, 0x01 };
    }

    public static byte[] boldOff() {
        return new byte[]{ 0x1B, 0x45, 0x00 };
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

    public static byte[] text(String text) {
        return text.getBytes(CHARSET);
    }
}
