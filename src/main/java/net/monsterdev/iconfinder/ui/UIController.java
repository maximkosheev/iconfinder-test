package net.monsterdev.iconfinder.ui;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import net.monsterdev.iconfinder.Main;

import java.net.URL;
import java.util.ResourceBundle;

public interface UIController extends Initializable {
    static Stage primaryStage() {
        return Main.primaryStage;
    }

    static void showErrorMessage(String msg) {
        Alert errorDlg = new Alert(Alert.AlertType.ERROR);
        errorDlg.setTitle("AutoMosreg");
        errorDlg.setHeaderText("Ошибка:");
        errorDlg.setContentText(msg);
        errorDlg.showAndWait();
    }

    void initialize(URL location, ResourceBundle resources);

    Node getView();
    void setView(Node node);
}
