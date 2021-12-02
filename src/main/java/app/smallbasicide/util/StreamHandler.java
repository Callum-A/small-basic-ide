package app.smallbasicide.util;

import app.smallbasicide.controller.IDEController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamHandler extends Thread {
    private String cmd;
    private IDEController controller;
    private Process pr;

    public StreamHandler(String cmd, IDEController controller) {
        this.cmd = cmd;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            Runtime rt = Runtime.getRuntime();
            controller.setRunButtonDisabled(true);
            controller.setStopButtonDisabled(false);
            pr = rt.exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            controller.setRunButtonDisabled(false);
            controller.setStopButtonDisabled(true);
        } catch (Exception ignored) {

        }
    }

    public void stopProcess() {
        pr.destroy();
        controller.setRunButtonDisabled(false);
        controller.setStopButtonDisabled(true);
    }
}
