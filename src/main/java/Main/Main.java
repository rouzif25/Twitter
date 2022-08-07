package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Scene scene;
    @Override
    public void start(Stage stage) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        scene = new Scene(pane, 1200, 800);
        stage.setTitle("Twitter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}