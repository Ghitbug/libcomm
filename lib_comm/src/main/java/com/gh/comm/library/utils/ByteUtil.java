package com.gh.comm.library.utils;


public class ByteUtil {
    protected static int a(byte b) {
        return b & 0xFF;
    }
    protected static byte b(byte a, byte b) {
        return (byte) (a << 4 | b);
    }
    protected static String string(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            return bytes.toString();
        }
    }
}
