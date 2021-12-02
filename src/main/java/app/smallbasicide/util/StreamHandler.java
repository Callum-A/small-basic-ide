package app.smallbasicide.util;

import app.smallbasicide.controller.IDEController;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamHandler extends Thread {
    private String cmd;
    private IDEController controller;
    private Process pr;
    private TextArea ta;

    public StreamHandler(String cmd, IDEController controller, TextArea ta) {
        this.cmd = cmd;
        this.controller = controller;
        this.ta = ta;
    }

    @Override
    public void run() {
        try {
            Runtime rt = Runtime.getRuntime();
            controller.programStarted();
            pr = rt.exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                ta.setText(ta.getText() + line + "\n");
//                System.out.println(line);
            }
            controller.programFinished();
        } catch (Exception ignored) {

        }
    }

    public void stopProcess() {
        pr.destroy();
        controller.programFinished();
    }
}
