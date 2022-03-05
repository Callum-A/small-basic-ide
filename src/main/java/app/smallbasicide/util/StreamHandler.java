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
        this.debugMode = debugMode;
        this.cmd = CommandHandler.buildCommand(file, debugMode, outputSymbolTable, breakpoint);
        this.controller = controller;
        this.ta = ta;
        this.fullOutput = "";
        this.currentSymbolTable = "";
        this.symTa = symTa;
    }

    @Override
    public void run() {
        try {
            if (!debugMode) {
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
            } else {
                Runtime rt = Runtime.getRuntime();
                pr = rt.exec(cmd);
                input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
                writer = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
                next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("EXCEPTION: " + e);
        }
    }

    public boolean next() throws Exception {
        if (pr.isAlive()) {
            String line = "";
            String tempOutput = "";
            // Read error
            while ((line = error.readLine()) != null) {
                tempOutput += line + "\n";
            }

            // Read input
            while ((line = input.readLine()) != null && !line.endsWith("-- Symbol Table End --")) {
                tempOutput += line + "\n";
            }

            if (line != null) {
                tempOutput += line + "\n"; // Get the symbol table end lines
            }
            String[] split = tempOutput.split("-- Symbol Table Start --"); // Split into sym table and output
            fullOutput += split[0];
            System.out.println(fullOutput);
            if (split.length > 1) {
                currentSymbolTable = "-- Symbol Table Start --" + split[1];
            }

            // Ignore exceptions due to stream closing
            try {
                writer.write("NEXT\n");
                writer.flush();
            } catch (Exception ignored) {}
            ta.setText(fullOutput);
            symTa.setText(currentSymbolTable);
            return false;
        } else {
            stopProcess();
            return true; // Program finished so return true
        }
    }

    public void stopProcess() {
        pr.destroy();
    }
}
