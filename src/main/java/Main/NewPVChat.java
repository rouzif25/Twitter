package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Main.Chat.*;

public class NewPVChat implements Initializable {

    final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    final String username = "root";
    final String Password = "Tondar.1400";


    @FXML
    TableView followersTable;
    @FXML
    TableColumn followersColumn;
    @FXML
    TextField searchUser;
    @FXML
    Label messageLabel;


    public void back(MouseEvent mouseEvent) throws IOException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        Main.scene.setRoot(pane);
    }

    public void startChat(MouseEvent mouseEvent) throws SQLException, IOException {
        ObservableList<Followers> selectedMember ;
        selectedMember = followersTable.getSelectionModel().getSelectedItems();
        if (selectedMember.size() == 0){
            messageLabel.setText("You haven't select a follower !");
        }
        else {
            receiverName = selectedMember.get(0).getFollowerName();
            roll = "";
            chatType = "pv";
            String sql;
            ResultSet resultSet;
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            sql = "SELECT * FROM personalinformation WHERE username LIKE '" + receiverName + "'";
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                receiverPhoto = resultSet.getString("profileImage");
            }
            String name1 = senderName + "_" + receiverName;
            String name2 = receiverName + "_" + senderName;
            sql = "SELECT * FROM chatTable WHERE name LIKE '" + name1 + "' or name LIKE '" + name2 + "'";
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                chatName = resultSet.getString("name");
            } else {
                chatName = senderName + "_" + receiverName;
                sql = "CREATE TABLE " + chatName + " ( id int not null auto_increment , senderName varchar(255) not null , receiverName varchar(255) not null , message varchar (255) not null , replyTo int not null , seen varchar (255) not null , date varchar (255) not null , time varchar (255) not null , forwarded varchar (255) not null , primary key (id))";
                statement.executeUpdate(sql);
                sql = "insert into chatTable (name,type)" + "values ('" + chatName + "' , \"pv\")";
                statement.executeUpdate(sql);
            }
            Pane pane = null;
            pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
            Main.scene.setRoot(pane);
        }

    }

    public void search(MouseEvent mouseEvent) throws SQLException, IOException {
        if(searchUser.getText().isEmpty()){
            messageLabel.setText("Please enter the username !");
        }
        else {
            receiverName = searchUser.getText();
            roll = "";
            chatType = "pv";
            String sql;
            ResultSet resultSet;
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            sql = "SELECT * FROM personalinformation WHERE username LIKE '" + receiverName + "'";
            resultSet = statement.executeQuery(sql);
            if (!resultSet.next()) {
                messageLabel.setText("No username with name " + receiverName + " is available !");
            }
            else {
                receiverPhoto = resultSet.getString("profileImage");
                String name1 = senderName + "_" + receiverName;
                String name2 = receiverName + "_" + senderName;
                sql = "SELECT * FROM chatTable WHERE name LIKE '" + name1 + "' or name LIKE '" + name2 + "'";
                resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    chatName = resultSet.getString("name");
                } else {
                    chatName = senderName + "_" + receiverName;
                    sql = "CREATE TABLE " + chatName + " ( id int not null auto_increment , senderName varchar(255) not null , receiverName varchar(255) not null , message varchar (255) not null , replyTo int not null , seen varchar (255) not null , date varchar (255) not null , time varchar (255) not null , forwarded varchar (255) not null , primary key (id))";
                    statement.executeUpdate(sql);
                    sql = "insert into chatTable (name,type)" + "values ('" + chatName + "' , \"pv\")";
                    statement.executeUpdate(sql);
                }
                Pane pane = null;
                pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
                Main.scene.setRoot(pane);
            }
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        followersColumn.setCellValueFactory(new PropertyValueFactory<>("followerName"));
        followersTable.setItems(FollowersList(myFollowersList));

    }

    private ObservableList<Followers> FollowersList (ArrayList<Followers> temp) {
        ObservableList<Followers> followers = FXCollections.observableArrayList();
        followers.addAll(temp);
        return followers;
    }

}
