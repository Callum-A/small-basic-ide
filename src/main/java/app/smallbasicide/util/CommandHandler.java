package app.smallbasicide.util;

import java.io.File;

/**
 * Helper class to build the command to run the IDE.
 */
public class CommandHandler {
    /**
     * Method to build a compiler command. Takes the path to the compiler and handles file and CLI args.
     */
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
