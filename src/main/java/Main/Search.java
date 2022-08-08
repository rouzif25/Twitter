package Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import static Main.Chat.*;
import static Main.Chat.groupChats;
import static Main.ChatScreen.searchedMessages;


public class Search implements Initializable {

    @FXML
    Label textLabel;
    @FXML
    TextField messageId;
    @FXML
    Label messageLabel;

    final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    final String username = "root";
    final String Password = "Tondar.1400";


    public void showMessage(MouseEvent mouseEvent) throws SQLException {
        if (!messageId.getText().isEmpty()){
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            String id = messageId.getText();
            String sql = "SELECT * FROM " + chatName + " WHERE id LIKE " + id ;
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                messageLabel.setText(resultSet.getString("message"));
            }
            else {
                messageLabel.setText("No message with id = " + id);
            }
        }
    }

    public void back(MouseEvent mouseEvent) throws IOException, SQLException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
        Main.scene.setRoot(pane);
    }

    public void initialize(URL url, ResourceBundle resourceBundle){
        String text = "" ;
        for (String a:searchedMessages) {
            text = text + a + "\n";
        }
        textLabel.setText(text);
    }

}
