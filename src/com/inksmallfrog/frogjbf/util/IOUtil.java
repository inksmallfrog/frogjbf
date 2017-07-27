package com.inksmallfrog.frogjbf.util;

import java.io.*;

/**
 * Created by inksmallfrog on 17-7-27.
 */
public class IOUtil {
    public static String getStringFromFile(String fileName) throws IOException {
        BufferedInputStream bs = new BufferedInputStream(new FileInputStream(fileName));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024];
        int size = -1;
        while((size = bs.read(buffer)) > 0){
            baos.write(buffer, 0, size);
        }
        return baos.toString();
    }
    public static byte[] getBytesFromFile(String fileName) throws IOException {
        BufferedInputStream bs = new BufferedInputStream(new FileInputStream(fileName));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4 * 1024];
        int size = -1;
        while((size = bs.read(buffer)) > 0){
            baos.write(buffer, 0, size);
        }
        return baos.toByteArray();
    }
}
