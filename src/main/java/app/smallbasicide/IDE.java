package app.smallbasicide;

import app.smallbasicide.controller.IDEController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class IDE extends Application {

    /**
     * Start the primary stage. Loads the IDE window.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/smallbasicide/view/IDE.fxml"));
        Parent root = fxmlLoader.load();
        IDEController controller = fxmlLoader.getController();
        primaryStage.setTitle("Small Basic IDE");
        Scene sc = new Scene(root);
        primaryStage.setScene(sc);
        primaryStage.show();
    }

    /**
     * Program entry point.
     */
    public static void main(String[] args) { launch(args); }
}
