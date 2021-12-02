package app.smallbasicide.util;

import app.smallbasicide.controller.IDEController;
import javafx.scene.control.TextArea;

import java.io.*;

public class StreamHandler extends Thread {
    private String cmd;
    private IDEController controller;
    private Process pr;
    private TextArea ta;
    private BufferedWriter writer;
    private BufferedReader input;
    private boolean debugMode;
    private String fullOutput;
    private String currentSymbolTable;

    public StreamHandler(File file, boolean debugMode, boolean outputSymbolTable, IDEController controller, TextArea ta) {
        this.debugMode = debugMode;
        this.cmd = CommandHandler.buildCommand(file, debugMode, outputSymbolTable);
        this.controller = controller;
        this.ta = ta;
        this.fullOutput = "";
        this.currentSymbolTable = "";
    }

    @Override
    public void run() {
        try {
            if (!debugMode) {
                Runtime rt = Runtime.getRuntime();
                controller.programStarted();
                pr = rt.exec(cmd);
                input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    ta.setText(ta.getText() + line + "\n");
                }
                System.out.println("Out while");
                controller.programFinished();
            } else {
                Runtime rt = Runtime.getRuntime();
                controller.programStarted();
                pr = rt.exec(cmd);
                input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(pr.getOutputStream()));
                // Waits for user to call next()
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e);
        }
    }

    public boolean next() throws Exception {
        if (pr.isAlive()) {
            String line;
            String tempOutput = "";
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
            return false;
        } else {
            stopProcess();
            return true; // Program finished so return true
        }
    }

    public void stopProcess() {
        pr.destroy();
        controller.programFinished();
    }
}
