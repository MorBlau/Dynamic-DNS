package com.mblau.ddns.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static boolean hasReadAccess(String fileName) {
        return new File(fileName).canRead();
    }

    public static String getStringFromFile(String fileName) throws IOException {
        return new String(new FileInputStream(fileName).readAllBytes());
    }

    public static boolean fileExists(String fileName) {
        return new File(fileName).exists();
    }

    public static void saveToFile(String ip, String fileName) throws IOException {
        new FileOutputStream(fileName).write(ip.getBytes(StandardCharsets.UTF_8));
    }
}
