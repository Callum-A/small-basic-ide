package app.smallbasicide.controller;


import app.smallbasicide.util.CommandHandler;
import app.smallbasicide.util.Config;
import app.smallbasicide.util.StreamHandler;
import app.smallbasicide.util.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.ResourceBundle;

public class IDEController implements Initializable {

    @FXML private MenuItem saveFile;
    @FXML private MenuItem run;
    @FXML private MenuItem stop;
    @FXML private AnchorPane ap;
    @FXML private TabPane tabs;
    private final HashMap<Tab, File> tabToFileMap = new HashMap<>();
    private StreamHandler currentProgramRunning;

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
    }

    public void clickSave(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File toSave = tabToFileMap.get(selectedTab);
        if (toSave != null) {
            Util.writeFile(toSave, selectedTab);
            System.out.println("Saving file " + toSave.getAbsolutePath());
        } else {
            // TODO: show some error
        }
    }

    public void clickSaveAs(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fc = new FileChooser();
        File toSave = fc.showSaveDialog(stage);
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        if (toSave != null) {
            Util.writeFile(toSave, selectedTab);
            tabToFileMap.put(selectedTab, toSave);
            selectedTab.setText(toSave.getName());
            System.out.println("Saving file as " + toSave.getAbsolutePath());
        }
    }

    public void clickOpen(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        // Open file selection dialog
        FileChooser fc = new FileChooser();
        File selected = fc.showOpenDialog(stage);
        // If we have selected a file
        if (selected != null) {
            // Create a tab using the file
            if (!isFileAlreadyOpen(selected)) {
                Tab t = createFileTab(selected);
                tabs.getTabs().add(t);
                tabs.getSelectionModel().select(t);
            }
        } else {
            // TODO: handle not selected file here
        }
    }

    private boolean isFileAlreadyOpen(File file) {
        for (File value : tabToFileMap.values()) {
            if (file.getAbsolutePath().equals(value.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    private Tab createFileTab(File file) throws Exception {
        String contents = Util.readFile(file);
        TextArea ta = new TextArea("");
        ta.setFont(Font.font("Monospaced", 13));
        ta.setText(contents);
        Tab t = new Tab(file.getName(), ta);
        t.setClosable(true);
        tabToFileMap.put(t, file);
        t.setOnClosed(evt -> tabToFileMap.remove(t));
        return t;
    }

    public void clickRun(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File openFile = tabToFileMap.get(selectedTab);
        // Ensure the file is up to date
        // TODO: show pop up here
        clickSave(null);
        // TODO: set debug and sym table mode in opts
        String cmd = CommandHandler.buildCommand(openFile, true, true);
        if (currentProgramRunning == null) {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.NONE);
            Stage stage = (Stage) ap.getScene().getWindow();
            dialog.initOwner(stage);
            TextArea ta = new TextArea("");
            ta.setEditable(false);
            ta.setFont(Font.font("Monospaced", 13));
            Scene dialogScene = new Scene(ta, 600, 400);
            dialog.setScene(dialogScene);
            dialog.setTitle("Program Output");
            dialog.show();
            currentProgramRunning = new StreamHandler(openFile, true, true, this, ta);
            currentProgramRunning.start();
        }
    }

    public void programStarted() {
        stop.setDisable(false);
        run.setDisable(true);
    }

    public void programFinished() {
        stop.setDisable(true);
        run.setDisable(false);
        currentProgramRunning = null;
    }

    public void clickStop(ActionEvent e) {
        if (currentProgramRunning != null) {
            currentProgramRunning.stopProcess();
            currentProgramRunning = null;
        }
    }

    public void nextStmt(ActionEvent e) throws Exception {
        currentProgramRunning.next();
    }
}
