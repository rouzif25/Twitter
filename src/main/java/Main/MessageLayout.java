package Main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class MessageLayout {


    @FXML
    Label messageLabel;
    @FXML
    GridPane messageGridPane;

    public void setMessageLabel(String messageLabel1){
        messageLabel.setText(messageLabel1);
    }

}
