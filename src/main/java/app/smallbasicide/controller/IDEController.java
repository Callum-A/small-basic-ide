package app.smallbasicide.controller;

import app.smallbasicide.IDE;
import app.smallbasicide.util.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.IntFunction;

public class IDEController implements Initializable {

    @FXML private MenuItem saveFile;
    @FXML private MenuItem run;
    @FXML private AnchorPane ap;
    @FXML private TabPane tabs;
    private final HashMap<Tab, File> tabToFileMap = new HashMap<>();
    private int breakpoint = -1;

    public void setBreakpoint(int breakpoint) {
        this.breakpoint = breakpoint;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
    }

    public void clickSave(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File toSave = tabToFileMap.get(selectedTab);
        if (toSave != null) {
            Util.writeFile(toSave, selectedTab);
            System.out.println("Saving file " + toSave.getAbsolutePath());
        } else {
            // TODO: show some error
        }
    }

    public void clickSaveAs(ActionEvent e) throws Exception {
        Stage stage = (Stage) ap.getScene().getWindow();
        FileChooser fc = new FileChooser();
        File toSave = fc.showSaveDialog(stage);
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        if (toSave != null) {
            Util.writeFile(toSave, selectedTab);
            tabToFileMap.put(selectedTab, toSave);
            selectedTab.setText(toSave.getName());
            System.out.println("Saving file as " + toSave.getAbsolutePath());
        }
    }

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
            // TODO: handle not selected file here
        }
    }
    public void clickBreakpointMenuItem(ActionEvent e) throws Exception {
        TextInputDialog input = new TextInputDialog(breakpoint + "");
        input.setTitle("Enter breakpoint line number");
        input.setHeaderText("Enter breakpoint line number");
        Optional<String> bp = input.showAndWait();
        if (bp.isPresent()) {
            String unparsed = bp.get();
            int newBreakpoint = breakpoint;
            try {
                newBreakpoint = Integer.parseInt(unparsed);
            } catch (Exception ignored) {}
            breakpoint = newBreakpoint;
            Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
            CodeArea ca = (CodeArea) selectedTab.getContent();
            ca.setParagraphGraphicFactory(HBoxFactory.buildSideBars(ca, breakpoint - 1));
        }
    }

    private boolean isFileAlreadyOpen(File file) {
        for (File value : tabToFileMap.values()) {
            if (file.getAbsolutePath().equals(value.getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

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

    public void clickDebugRun(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File openFile = tabToFileMap.get(selectedTab);
        // Ensure the file is up to date
        // TODO: show pop up here
        clickSave(null);
        // Pop up terminal dialog
        run(openFile, true, true, breakpoint);
    }

    public void clickRun(ActionEvent e) throws Exception {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        File openFile = tabToFileMap.get(selectedTab);
        // Ensure the file is up to date
        // TODO: show pop up here
        clickSave(null);
        // Pop up terminal dialog
        run(openFile, false, false, -1);
    }

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
}
