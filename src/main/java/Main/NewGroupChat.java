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
import static Main.ChatScreen.lastMessages;

public class NewGroupChat implements Initializable {

    final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    final String username = "root";
    final String Password = "Tondar.1400";

    Chats newChat = new Chats();

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


    public void back(MouseEvent mouseEvent) throws IOException, SQLException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        Main.scene.setRoot(pane);
        privateChats.removeAll(privateChats);
        groupChats.removeAll(groupChats);
        chatsList.clear();
        String name1 ;
        String name2 ;
        ResultSet resultSet;
        ResultSet resultSet1;
        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
        Statement statement1 = conn.createStatement();
        String sql;
        sql = "SELECT * FROM personalinformation ";
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            name1 = senderName + "_" + resultSet.getString("username");
            name2 = resultSet.getString("username") + "_" + senderName ;
            sql = "SELECT * FROM chatTable WHERE type LIKE \"pv\"";
            resultSet1 = statement1.executeQuery(sql);
            while (resultSet1.next()){
                if (resultSet1.getString("name").equals(name1)){
                    Chats newChat = new Chats();
                    newChat.setChatName(name1);
                    newChat.setReceiverName(resultSet.getString("username"));
                    newChat.setType("pv");
                    newChat.setPhotoAddress(resultSet.getString("profileImage"));
                    privateChats.add(newChat);
                    break;
                }
                else if (resultSet1.getString("name").equals(name2)){
                    Chats newChat = new Chats();
                    newChat.setChatName(name2);
                    newChat.setReceiverName(resultSet.getString("username"));
                    newChat.setType("pv");
                    newChat.setPhotoAddress(resultSet.getString("profileImage"));
                    privateChats.add(newChat);
                    break;
                }
            }
        }
        sql = "SELECT * FROM groupTable WHERE username LIKE '" + senderName + "'";
        resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            name1 = resultSet.getString("groupName");
            Chats newChat = new Chats();
            newChat.setChatName(name1);
            newChat.setReceiverName(name1);
            newChat.setType("group");
            newChat.setRoll(resultSet.getString("roll"));
            sql = "SELECT * FROM chatTable WHERE name LIKE '" + name1 + "' AND type LIKE \"group\"";
            resultSet1 = statement1.executeQuery(sql);
            if (resultSet1.next()){
                newChat.setPhotoAddress(resultSet1.getString("image"));
            }
            groupChats.add(newChat);
        }
        for (Chats a:privateChats) {
            sql = "SELECT * FROM " + a.getChatName() + " ORDER BY id DESC LIMIT 1";
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                a.setLastMessage(resultSet.getString("message"));
                a.setLastTime(resultSet.getString("date") + " | " + resultSet.getString("time"));
            }
        }
        for (Chats a:groupChats) {
            sql = "SELECT * FROM " + a.getChatName() + " ORDER BY id DESC LIMIT 1";
            resultSet = statement.executeQuery(sql);
            if (resultSet.next()){
                a.setLastMessage(resultSet.getString("message"));
                a.setLastTime(resultSet.getString("date") + " | " + resultSet.getString("time"));
            }
        }
        chatsList.addAll(privateChats);
        chatsList.addAll(groupChats);
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
                showMembers.clear();
                showMembers.addAll(members);
                messageLabel.setText("Added successfully");
                addedMembers.refresh();
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

                haveBlocked = false;
                isBlocked = false;
                conn = DriverManager.getConnection(DB_url, username, Password);
                statement = conn.createStatement();
                Statement statement1 = conn.createStatement();
                sql = "SELECT " + senderName + " FROM blockTable";
                resultSet = statement.executeQuery(sql);
                ResultSet resultSet1;
                while (resultSet.next()) {
                    if (resultSet.getString(senderName) != null) {
                        if (resultSet.getString(senderName).equals(receiverName)) {
                            haveBlocked = true;
                            break;
                        }
                    }
                }
                sql = "SELECT " + receiverName + " FROM blockTable";
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    if (resultSet.getString(receiverName) != null) {
                        if (resultSet.getString(receiverName).equals(senderName)) {
                            isBlocked = true;
                            break;
                        }
                    }
                }
                sql = "SELECT * FROM " + chatName + " ORDER BY id DESC";
                resultSet = statement.executeQuery(sql);
                lastMessages.clear();
                String message ;
                while (resultSet.next()) {
                    message = resultSet.getString("id") + " ) " + resultSet.getString("senderName") + "\n";
                    if (!resultSet.getString("replyTo").equals("0")){
                        sql = "SELECT * FROM " + chatName + " WHERE id LIKE " + resultSet.getString("replyTo");
                        resultSet1 = statement1.executeQuery(sql);
                        if (resultSet1.next()){
                            message = message + "Reply to --> " + resultSet1.getString("message").substring(0,Integer.min(15,resultSet1.getString("message").length())) + " : ";
                        }
                    }
                    else if (!resultSet.getString("forwarded").equals("-")){
                        message = message + "Forwarded! from " + resultSet.getString("forwarded") + " : ";
                    }
                    message = message + resultSet.getString("message") + "\n";
                    message = message + resultSet.getString("time");
                    lastMessages.add(message);
                }
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
        addedMembers.setItems(showMembers);
        followersTable.setItems(FollowersList(myFollowersList));
    }

    private ObservableList<Followers> FollowersList (ArrayList<Followers> temp) {
        ObservableList<Followers> followers = FXCollections.observableArrayList();
        followers.addAll(temp);
        return followers;
    }

}
