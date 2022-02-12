package app.smallbasicide.util;

import java.io.File;

public class CommandHandler {
    public static String buildCommand(File inputFile, boolean debugMode, boolean outputSymbolTable, int breakpoint) {
        String base = Config.PATH_TO_COMPILER + inputFile.getAbsolutePath();
        if (debugMode) {
            base += " --debug";
        }
        if (outputSymbolTable) {
            base += " --sym";
        }

        if (breakpoint > 0) {
            base += " " + breakpoint;
        }

        return base;
    }
}
