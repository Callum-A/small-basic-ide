package app.smallbasicide;

import app.smallbasicide.controller.IDEController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class IDE extends Application {

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

    public void runOnF5(KeyEvent e) {

    }

    public static void main(String[] args) { launch(args); }
}
