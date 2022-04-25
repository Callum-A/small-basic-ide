package app.smallbasicide.controller;

import app.smallbasicide.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.Subscription;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Class binding to the IDE.fxml file. Handles all operations for the main editor window.
 */
public class IDEController implements Initializable {
    @FXML private MenuItem saveFile;              // The save file button
    @FXML private MenuItem run;                   // The run button
    @FXML private AnchorPane ap;                  // The anchor pane (used to get the scene)
    @FXML private TabPane tabs;                   // The tabs pane
    private final HashMap<Tab, File> tabToFileMap // Map of tabs to files
            = new HashMap<>();
    private int breakpoint = -1;                  // Current set breakpoint

    /**
     * Initialise the IDE window.
     */
    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {}

    /**
     * Handler for clicking the new file button. Creates a temp file and opens it.
     */
    public void clickNew(ActionEvent e) throws Exception {
        File tempFile = File.createTempFile("Untitled", ".sb");
        tempFile.deleteOnExit();
        Tab t = createFileTab(tempFile);
        tabs.getTabs().add(t);
        tabs.getSelectionModel().select(t);
    }

    /**
     * Handler for clicking the save button. Saves the file if one is open.
     */
    public void clickSave(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File toSave = tabToFileMap.get(selectedTab);
        if (toSave != null) {
            Util.writeFile(toSave, selectedTab);
            System.out.println("Saving file " + toSave.getAbsolutePath());
        }
    }

    /**
     * Handler for clicking the save as button. Saves the file in the specified
     * location. Uses the OS' native file explorer.
     */
    public void clickSaveAs(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        if (selectedTab == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not selected");
            alert.setContentText("You did not select a file!");
            alert.showAndWait();
            return;
        }

        FileChooser fc = new FileChooser();
        File toSave = fc.showSaveDialog(stage);
        if (toSave != null) {
            Util.writeFile(toSave, selectedTab);
            tabToFileMap.put(selectedTab, toSave);
            selectedTab.setText(toSave.getName());
            System.out.println("Saving file as " + toSave.getAbsolutePath());
        }
    }

    /**
     * Handler for clicking the open file button. Uses the OS' native file explorer.
     * Adds a tab and opens the file and reads the content.
     */
    public void clickOpen(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        // Open file selection dialog
        FileChooser fc = new FileChooser();
        File selected = fc.showOpenDialog(stage);
        // If we have selected a file
        if (selected != null) {
            // Create a tab using the file
            if (!isFileAlreadyOpen(selected)) {
                Tab t = createFileTab(selected);
                tabs.getTabs().add(t);
                tabs.getSelectionModel().select(t);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("File not selected");
            alert.setContentText("You did not select a file!");
            alert.showAndWait();
        }
    }

    /**
     * Helper to check if a file is already open, AKA it already has a tab open.
     * Returns true if the tab is already present.
     */
    private boolean isFileAlreadyOpen(File file) {
        for (File value : tabToFileMap.values()) {
            if (file.getAbsolutePath().equals(value.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper to create a file tab, reads the file, sets up the text area and highlighting,
     * sets up tab on closed events.
     */
    private Tab createFileTab(File file) throws Exception {
        String contents = Util.readFile(file);
        CodeArea ta = new CodeArea(contents);
        ta.setStyle("-fx-font-size: 1em; -fx-font-weight: bold;");
        ta.setParagraphGraphicFactory(HBoxFactory.buildSideBars(ta, breakpoint));
        ta.getStylesheets().add(getClass().getResource("/app/smallbasicide/view/style/keywords.css").toURI().toString());
        Subscription sub = new SmallBasicHighlight(ta).highlight();
        Tab t = new Tab(file.getName(), ta);
        t.setClosable(true);
        tabToFileMap.put(t, file);
        t.setOnClosed(evt -> {
            tabToFileMap.remove(t);
            sub.unsubscribe();
        });
        return t;
    }

    /**
     * Handler for clicking the run button for the open file. Calls the run hander.
     */
    public void clickRun(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File openFile = tabToFileMap.get(selectedTab);
        if (openFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No open file");
            alert.setContentText("You did not select a file!");
            alert.showAndWait();
            return;
        }

        // Ensure the file is up to date
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save?");
        alert.setHeaderText("Save " + openFile.getName() + "?");
        alert.setContentText("In order to run " + openFile.getName() + " it must be saved first. Continue?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            clickSave(null);
            run(openFile, false, false, -1);
        }
    }

    /**
     * Helper to run a given a file, allows setting of the sufficient flags.
     * Allows users to specify if they can run in debug mode, or symbol mode.
     * Opens the terminal controller scene.
     */
    public void run(File file, boolean debugMode, boolean symbolMode, int breakpoint) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/smallbasicide/view/Terminal.fxml"));
        Parent parent = fxmlLoader.load();
        TerminalController terminalController = fxmlLoader.getController();
        Scene scene = new Scene(parent, 300, 200);
        Stage dialog = new Stage();
        Stage parentStage = (Stage) ap.getScene().getWindow();
        dialog.initModality(Modality.NONE);
        dialog.setScene(scene);
        dialog.initOwner(parentStage);
        dialog.show();
        terminalController.startProgram(file, debugMode, symbolMode, breakpoint);
    }

    /**
     * Handler for clicking the update compiler path button. Opens a pop up so
     * users can write the path to the compiler.
     */
    public void clickUpdateCompiler(ActionEvent e) throws Exception {
        TextInputDialog dialog = new TextInputDialog(Config.PATH_TO_COMPILER);
        dialog.setTitle("Update Compiler");
        dialog.setHeaderText("Update path to the compiler");

        Optional<String> value = dialog.showAndWait();
        String v = value.get();
        if (v.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Must set a compiler path");
            alert.setContentText("You did not set a compiler path!");
            alert.showAndWait();
            return;
        }
        File f = new File(v);
        if (f.exists() && !f.isDirectory()) {
            Config.PATH_TO_COMPILER = v;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Compiler file does not exist");
            alert.setContentText("The specified file path does not exist!");
            alert.showAndWait();
        }
    }
}
