package app.smallbasicide.util;

import javafx.scene.control.Tab;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

/**
 * Helper class containing some common utility functions.
 */
public class Util {
    /**
     * Read a given file into a string.
     */
    public static String readFile(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        String contents = new String(data, StandardCharsets.UTF_8);
        return contents;
    }

    /**
     * Write a given tab to a given file.
     */
    public static void writeFile(File file, Tab toWrite) throws Exception {
        CodeArea content = (CodeArea) toWrite.getContent();
        String text = content.getText();
        if (!text.endsWith("\n")) {
            text += "\n";
        }
        FileWriter myWriter = new FileWriter(file.getAbsolutePath());
        myWriter.write(text);
        myWriter.close();
    }
}
