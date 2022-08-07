package Main;

import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class NewPVChat {

    public void back(MouseEvent mouseEvent) throws IOException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        Main.scene.setRoot(pane);
    }

    public void startChat(MouseEvent mouseEvent) {

    }
}
