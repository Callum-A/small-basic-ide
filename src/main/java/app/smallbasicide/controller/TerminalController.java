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

/**
 * Class binding to the Terminal.fxml file. Handles all operations in the
 * terminal window.
 */
public class TerminalController implements Initializable {
    @FXML private TextArea output;               // The main output text area
    @FXML private TextArea symbol;               // The symbol table text area
    @FXML private Button stopButton;             // Button to kill any live running program
    private StreamHandler currentProgramRunning; // Thread with the current running program

    /**
     * Setup the terminal window. Sets the font for the text areas and font sizes.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        output.setFont(Font.font("Monospaced", 13));
        symbol.setFont(Font.font("Monospaced", 13));
    }

    /**
     * Start the program, creating a stream handler and starting the thread. Takes all relevant and needed arguements.
     */
    public void startProgram(File file, boolean debugMode, boolean symbolMode, int breakpoint) {
        currentProgramRunning = new StreamHandler(file, debugMode, symbolMode, breakpoint, this, output, symbol);
        currentProgramRunning.start();
    }

    /**
     * Handler for clicking the stop button. Kills the process and disables the stop button.
     */
    public void clickStop(ActionEvent e) {
        if (currentProgramRunning != null) {
            currentProgramRunning.stopProcess();
            currentProgramRunning = null;
            stopButton.setDisable(true);
        }
    }
}
