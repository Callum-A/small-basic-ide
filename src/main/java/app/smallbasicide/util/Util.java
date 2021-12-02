package app.smallbasicide.util;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class Util {
    public static boolean isProgramRunning = false;
    public static String readFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String contents = new String(data, StandardCharsets.UTF_8);
        return contents;
    }

    public static void writeFile(File file, Tab toWrite) throws Exception {
        TextArea content = (TextArea) toWrite.getContent();
        String text = content.getText();
        if (!text.endsWith("\n")) {
            text += "\n";
        }
        FileWriter myWriter = new FileWriter(file.getAbsolutePath());
        myWriter.write(text);
        myWriter.close();
    }
}
