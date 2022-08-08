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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Main.Chat.*;

public class NewGroupChat implements Initializable {

    final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    final String username = "root";
    final String Password = "Tondar.1400";

    Chats newChat = new Chats();
    ArrayList<Followers> members = new ArrayList<>();

    @FXML
    GridPane myGridPane;
    @FXML
    TextField myTextField;
    @FXML
    ImageView myImageView;
    @FXML
    TextField groupNameField;
    @FXML
    Label messageLabel;
    @FXML
    TableView addedMembers;
    @FXML
    TableView followersTable;
    @FXML
    TableColumn followersColumn;
    @FXML
    TableColumn membersColumn;


    public void back(MouseEvent mouseEvent) throws IOException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        Main.scene.setRoot(pane);
    }

    public void addMember(MouseEvent mouseEvent) {
        ObservableList<Followers> selectedMember ;
        selectedMember = followersTable.getSelectionModel().getSelectedItems();
        if (selectedMember.size() == 0){
            messageLabel.setText("You haven't select a follower !");
        }
        else {
            Followers followers = new Followers(selectedMember.get(0).getFollowerName());
            boolean isAdded = false ;
            for (Followers a:members) {
                if (a.getFollowerName().equals(followers.getFollowerName())){
                    isAdded = true;
                    break;
                }
            }
            if (isAdded){
                messageLabel.setText(followers.getFollowerName() + " is already added !");
            }
            else {
                members.add(followers);
                messageLabel.setText("Added successfully");
            }
        }
    }

    public void createGroup(MouseEvent mouseEvent) throws SQLException, IOException {
        if (groupNameField.getText().isEmpty()){
            messageLabel.setText("Please enter the group name !");
        }
        else if (addedMembers.getItems().size() == 0){
            messageLabel.setText("You have to add one member at least !");
        }
        else {
            newChat.setChatName(groupNameField.getText());
            newChat.setType("group");
            newChat.setReceiverName(groupNameField.getText());
            newChat.setRoll("admin");
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            String sql = "SELECT * FROM chatTable WHERE name LIKE '" + newChat.getChatName() + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                messageLabel.setText("The name " + newChat.getChatName() + " is already taken !");
            }
            else {
                receiverName = newChat.getReceiverName();
                receiverPhoto = newChat.getPhotoAddress();
                chatName = newChat.getChatName();
                chatType = newChat.getType();
                sql = "CREATE TABLE " + chatName + " ( id int not null auto_increment , senderName varchar(255) not null , message varchar (255) not null , replyTo int not null , date varchar (255) not null , time varchar (255) not null , forwarded varchar (255) not null , primary key (id))";
                statement.executeUpdate(sql);
                sql = "insert into chatTable (name,type,image)" + "values ('" + chatName + "' , \"group\" , '" + receiverPhoto + "')";
                statement.executeUpdate(sql);
                for (Followers a:members) {
                    sql = "insert into groupTable (groupName , username , roll) values ('" + chatName + "' , '" + a.getFollowerName() + "' , \"member\")";
                    statement.executeUpdate(sql);
                }
                sql = "insert into groupTable (groupName , username , roll) values ('" + chatName + "' , '" + senderName + "' , \"admin\")";
                statement.executeUpdate(sql);
                roll = "admin";
                Pane pane = null;
                pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
                Main.scene.setRoot(pane);
                ChatScreen.chatting();
            }
        }
    }

    public void setGroupPhoto(MouseEvent mouseEvent) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );

            Stage stage = (Stage) myGridPane.getScene().getWindow();
            File file=  fileChooser.showOpenDialog(stage);
            if(file!=null){
                myTextField.setText(file.getAbsolutePath());
                Image image = new Image(file.toURI().toString());
                newChat.setPhotoAddress(file.toURI().toString());
                myImageView.setImage(image);
            }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        membersColumn.setCellValueFactory(new PropertyValueFactory<>("followerName"));
        followersColumn.setCellValueFactory(new PropertyValueFactory<>("followerName"));
        addedMembers.setItems(FollowersList(members));
        followersTable.setItems(FollowersList(myFollowersList));
    }


    private ObservableList<Followers> FollowersList (ArrayList<Followers> temp) {
        ObservableList<Followers> followers = FXCollections.observableArrayList();
        followers.addAll(temp);
        return followers;
    }

}
