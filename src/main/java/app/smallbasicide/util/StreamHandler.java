package app.smallbasicide.util;

import app.smallbasicide.controller.IDEController;
import app.smallbasicide.controller.TerminalController;
import javafx.scene.control.TextArea;

import java.io.*;

public class StreamHandler extends Thread {
    private String cmd;
    private TerminalController controller;
    private Process pr;
    private TextArea ta;
    private TextArea symTa;
    private BufferedWriter writer;
    private BufferedReader input;
    private BufferedReader error;
    private boolean debugMode;
    private String fullOutput;
    private String currentSymbolTable;

    public StreamHandler(File file, boolean debugMode, boolean outputSymbolTable, int breakpoint, TerminalController controller, TextArea ta, TextArea symTa) {
        this.debugMode = false;
        this.cmd = CommandHandler.buildCommand(file, false, true, breakpoint);
        this.controller = controller;
        this.ta = ta;
        this.fullOutput = "";
        this.currentSymbolTable = "";
        this.symTa = symTa;
    }

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

    public void stopProcess() {
        pr.destroy();
    }
}
