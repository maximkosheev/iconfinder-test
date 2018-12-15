package net.monsterdev.iconfinder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    public static Stage primaryStage = null;
    private static Main instance = null;

    public static URL getResource(String name) {
        return instance.getClass().getResource(name);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Main.primaryStage = primaryStage;
        Main.instance = this;
        Parent root = FXMLLoader.load(getResource("/net/monsterdev/iconfinder/ui/sample.fxml"));
        primaryStage.setTitle("Iconfinder test");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
