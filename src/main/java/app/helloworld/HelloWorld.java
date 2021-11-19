package app.helloworld;

import app.helloworld.controller.HelloWorldController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class HelloWorld extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app/helloworld/view/HelloWorld.fxml"));
        Parent root = fxmlLoader.load();
        HelloWorldController controller = fxmlLoader.getController();
        primaryStage.setTitle("Hello World");
        Scene sc = new Scene(root);
        sc.setOnKeyPressed(e -> {
            System.out.println("KEY PRESSED");
            try {
                if (e.getCode() == KeyCode.F5) {
                    controller.clickRun(null);
                }
            } catch (Exception ex) {

            }
        });
        primaryStage.setScene(sc);
        primaryStage.show();
    }

    public void runOnF5(KeyEvent e) {

    }

    public static void main(String[] args) { launch(args); }
}
