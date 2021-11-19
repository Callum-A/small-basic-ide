package app.helloworld.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.Scanner;

public class HelloWorldController implements Initializable {

    @FXML private MenuItem saveFile;
    @FXML private MenuItem run;
    @FXML private TextArea text;
    @FXML private AnchorPane ap;
    @FXML private Label fileLabel;

    private File currentFile;

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
    }

    public void clickSave(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fc = new FileChooser();
        File toSave = currentFile == null ? fc.showSaveDialog(stage) : currentFile;
        if (toSave != null) {
            FileWriter myWriter = new FileWriter(toSave.getAbsolutePath());
            String t = text.getText();
            if (!t.endsWith("\n")) {
                t += "\n";
            }
            myWriter.write(t);
            myWriter.close();
            System.out.println("Saving file " + toSave.getAbsolutePath());
            currentFile = toSave;
            fileLabel.setText(currentFile.getName());
        } else {
            // TODO: show some error
        }
    }

    public void clickSaveAs(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fc = new FileChooser();
        File toSave = fc.showSaveDialog(stage);
        if (toSave != null) {
            FileWriter myWriter = new FileWriter(toSave.getAbsolutePath());
            myWriter.write(text.getText());
            myWriter.close();
            System.out.println("Saving file as " + toSave.getAbsolutePath());
            currentFile = toSave;
            fileLabel.setText(currentFile.getName());
        }
    }

    public void clickOpen(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fc = new FileChooser();
        File selected = fc.showOpenDialog(stage);
        if (selected != null) {
            FileInputStream fis = new FileInputStream(selected);
            byte[] data = new byte[(int) selected.length()];
            fis.read(data);
            fis.close();
            String contents = new String(data, StandardCharsets.UTF_8);
            text.setText(contents);
            currentFile = selected;
            fileLabel.setText(currentFile.getName());
        } else {
            // TODO: handle not selected file here
        }
    }

    public void clickRun(ActionEvent e) throws Exception {
        if (currentFile != null) {
            Runtime rt = Runtime.getRuntime();
            String cmd = "/Users/callumanderson/Documents/Programming/C++/small-basic/build/run.sh " + currentFile.getAbsolutePath();
            System.out.println(cmd);
            Process pr = rt.exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            String line = null;
            String out = "";
            String error = "";
            while((line = input.readLine()) != null) {
                out += line + "\n";
            }
            line = null;
            while ((line = err.readLine()) != null) {
                error += line + "\n";
            }
            int exitVal = pr.waitFor();
            System.out.println(out);
            if (exitVal != 0) {
                System.out.println(error);
            }
        }
    }
}
