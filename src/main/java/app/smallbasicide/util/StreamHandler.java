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
                String line;
                while (!(line = input.readLine()).endsWith("-- Symbol Table Start --")) {
                    ta.setText(ta.getText() + line + "\n");
                }

                while (!(line = input.readLine()).endsWith("-- Symbol Table End --")) {
                    symTa.setText(symTa.getText() + line + "\n");
                }
                symTa.setText("-- Symbol Table Start --\n" + symTa.getText() + "-- Symbol Table End --");
            } else {
                Runtime rt = Runtime.getRuntime();
                pr = rt.exec(cmd);
                input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
                next();
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e);
        }
    }

    public boolean next() throws Exception {
        if (pr.isAlive()) {
            System.out.println("is alive");
            String line = "";
            String tempOutput = "";
            System.out.println("setting output");
            while (!(line = input.readLine()).endsWith("-- Symbol Table End --")) {
                tempOutput += line + "\n";
            }
            tempOutput += line + "\n"; // Get the symbol table end lines
            String[] split = tempOutput.split("-- Symbol Table Start --");
            fullOutput += split[0];
            currentSymbolTable = "-- Symbol Table Start --" + split[1];
            writer.write("NEXT\n");
            writer.flush();
            ta.setText(fullOutput);
            System.out.println("setting output");
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
