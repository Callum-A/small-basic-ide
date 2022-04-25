package app.smallbasicide.util;

import app.smallbasicide.controller.TerminalController;
import javafx.scene.control.TextArea;

import java.io.*;

/**
 * Helper thread to handle live I/O stream from the running program.
 */
public class StreamHandler extends Thread {
    private String cmd;                    // The command to run
    private TerminalController controller; // The terminal window
    private Process pr;                    // The current running process
    private TextArea ta;                   // The text area to write output to
    private TextArea symTa;                // The symbol table text area to write to
    private BufferedWriter writer;         // Write to the program
    private BufferedReader input;          // Read stdout of the program
    private BufferedReader error;          // Read stderr of the program
    private boolean debugMode;             // Run in debug mode
    private String fullOutput;             // Full output
    private String currentSymbolTable;     // Current symbol table

    /**
     * Constructor building the thread, builds the command and sets all relevant variables.
     */
    public StreamHandler(File file, boolean debugMode, boolean outputSymbolTable,
                         int breakpoint, TerminalController controller, TextArea ta, TextArea symTa) {
        this.debugMode = false;
        this.cmd = CommandHandler.buildCommand(file, false, true, breakpoint);
        this.controller = controller;
        this.ta = ta;
        this.fullOutput = "";
        this.currentSymbolTable = "";
        this.symTa = symTa;
    }

    /**
     * Start the thread. Executes the program and live captures output and writes to the text area.
     */
    @Override
    public void run() {
        try {
            Runtime rt = Runtime.getRuntime();
            pr = rt.exec(cmd);
            input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
            String line;

            // Read errors
            while ((line = error.readLine()) != null) {
                ta.setText(ta.getText() + line + "\n");
            }

            // Read output
            while ((line = input.readLine()) != null && !line.endsWith("-- Symbol Table Start --")) {
                ta.setText(ta.getText() + line + "\n");
            }

            // Read symbol table
            while ((line = input.readLine()) != null && !line.endsWith("-- Symbol Table End --")) {
                symTa.setText(symTa.getText() + line + "\n");
            }
            symTa.setText("-- Symbol Table Start --\n" + symTa.getText() + "-- Symbol Table End --");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EXCEPTION: " + e);
        }
    }

    /**
     * Kill the process.
     */
    public void stopProcess() {
        pr.destroy();
    }
}
