package app.smallbasicide.controller;

import app.smallbasicide.util.StreamHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class TerminalController implements Initializable {
    @FXML private TextArea output;
    @FXML private TextArea symbol;
    @FXML private Button stopButton;

    private StreamHandler currentProgramRunning;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        output.setFont(Font.font("Monospaced", 13));
        symbol.setFont(Font.font("Monospaced", 13));
    }

    public void startProgram(File file, boolean debugMode, boolean symbolMode, int breakpoint) {
        currentProgramRunning = new StreamHandler(file, debugMode, symbolMode, breakpoint, this, output, symbol);
        currentProgramRunning.start();
    }

    public void clickStop(ActionEvent e) {
        if (currentProgramRunning != null) {
            currentProgramRunning.stopProcess();
            currentProgramRunning = null;
            stopButton.setDisable(true);
        }
    }
}
