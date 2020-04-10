package net.kaaass.kflight.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    public static String readAll(String filename) throws IOException {
        var fr = new FileReader(filename);
        int ch;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = fr.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        fr.close();
        return stringBuilder.toString();
    }

    public static String readAll(InputStream stream) throws IOException {
        var fr = new InputStreamReader(stream);
        int ch;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = fr.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        fr.close();
        return stringBuilder.toString();
    }
}
