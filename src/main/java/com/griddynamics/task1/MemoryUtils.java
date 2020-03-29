package com.griddynamics.task1;

public class MemoryUtils {

    // https://habr.com/en/post/134102/
    private static boolean IS_64X_SYSTEM;
    private static int OBJECT_HEADER;
    private static int INT_FIELDS;
    private static int OBJECT_REFERENCE;
    private static int ARRAY_HEADER;

    static {
        String systemArch = System.getProperty("sun.arch.data.model");
        IS_64X_SYSTEM = !(systemArch == null || systemArch.contains("32"));
        OBJECT_HEADER = IS_64X_SYSTEM ? 16 : 8;
        INT_FIELDS = 12; //3 * 4
        OBJECT_REFERENCE = IS_64X_SYSTEM ? 8 : 4;
        ARRAY_HEADER = IS_64X_SYSTEM ? 24 : 12;
    }

    public static long getFreeMemorySize() {
        System.gc();
        return Runtime.getRuntime().freeMemory() / 2;
    }

    public static int getBytesCountInLine(String str) {
        // https://habr.com/en/post/134102/
        // Characters are 2 bytes in Java
        return OBJECT_HEADER + ARRAY_HEADER + INT_FIELDS + OBJECT_REFERENCE + (str.length() * 2);
    }

}
