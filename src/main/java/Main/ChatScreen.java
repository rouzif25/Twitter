package Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Main.Chat.*;

public class ChatScreen implements Initializable {

    static final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    static final String username = "root";
    static final String Password = "Tondar.1400";


    static ArrayList<String> lastMessages = new ArrayList<>();
    String replyTo = "0";



    @FXML
    ImageView chatPhoto;
    @FXML
    Label name;
    @FXML
    Label groupMembers;
    @FXML
    TextField searchBox;
    @FXML
    TextField editedMessage;
    @FXML
    TextField editedMessageId;
    @FXML
    TextField forwardToPerson;
    @FXML
    TextField forwardToGroup;
    @FXML
    TextField forwardedMessageId;
    @FXML
    TextField repliedMessageId;
    @FXML
    TextField deletedMessageId;
    @FXML
    Label errorLabel;
    @FXML
    TextField textMessage;
    @FXML
    GridPane myGridPane;
    @FXML
    Button actionButton;
    @FXML
    VBox messageVBox;
    @FXML
    ScrollPane messageScrollPane;


    public void back(MouseEvent mouseEvent) throws IOException, SQLException {
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
                    privateChats.add(newChat);
                    break;
                }
                else if (resultSet1.getString("name").equals(name2)){
                    Chats newChat = new Chats();
                    newChat.setChatName(name2);
                    newChat.setReceiverName(resultSet.getString("username"));
                    newChat.setType("pv");
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
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        Main.scene.setRoot(pane);
    }

    public void search(MouseEvent mouseEvent) {
    }

    public void edit(MouseEvent mouseEvent) throws SQLException {
        if (isBlocked){
            errorLabel.setText("You have been blocked by this user !");
        }
        else if (haveBlocked){
            errorLabel.setText("You have blocked this user !");
        }
        else {
            if (editedMessageId.getText().isEmpty()) {
                errorLabel.setText("Enter message id !");
            } else if (editedMessage.getText().isEmpty()) {
                errorLabel.setText("Enter edited message !");
            } else {
                String message;
                String id = editedMessageId.getText();
                Connection conn = DriverManager.getConnection(DB_url, username, Password);
                Statement statement = conn.createStatement();
                String sql = "SELECT * FROM " + chatName + " WHERE id LIKE " + id;
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    if (!resultSet.getString("forwarded").equals("-")) {
                        errorLabel.setText("You can't edit forwarded messages ! ");
                    } else {
                        message = resultSet.getString("senderName");
                        if (message.equals(senderName)) {
                            sql = "SELECT * FROM " + chatName + " WHERE senderName LIKE '" + senderName + "' ORDER BY id DESC LIMIT 10 ";
                            resultSet = statement.executeQuery(sql);
                            boolean editable = false;
                            while (resultSet.next()) {
                                if (resultSet.getString("id").equals(id)) {
                                    editable = true;
                                    break;
                                }
                            }
                            if (editable) {
                                message = editedMessage.getText();
                                sql = "UPDATE " + chatName + " SET message = '" + message + "' WHERE id LIKE " + id;
                                statement.executeUpdate(sql);
                                errorLabel.setText("Edited successfully");
                            } else {
                                errorLabel.setText("You can only edit your last 10 messages !");
                            }
                        } else {
                            errorLabel.setText("You can't edit other's messages ! ");
                        }
                    }
                } else {
                    errorLabel.setText("No message with id " + id + " !");
                }
            }
        }
    }

    public void forward(MouseEvent mouseEvent) throws SQLException {
        if (forwardedMessageId.getText().isEmpty()){
            errorLabel.setText("Enter message id !");
        }
        else if (forwardToPerson.getText().isEmpty() && forwardToGroup.getText().isEmpty()){
            errorLabel.setText("Enter receiver name !");
        }
        else {
            String message;
            String id = editedMessageId.getText();
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            String sql = "SELECT * FROM " + chatName + " WHERE id LIKE " + id ;
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String sender = resultSet.getString("senderName");
                message = resultSet.getString("message");
                String receiver ;
                if (!forwardToPerson.getText().isEmpty()){
                    receiver = forwardToPerson.getText() ;
                    sql = "SELECT * FROM personalinformation WHERE username LIKE '" + receiver + "'";
                    resultSet = statement.executeQuery(sql);
                    if (resultSet.next()) {
                        String name1 = senderName + "_" + receiver;
                        String name2 = receiver + "_" + senderName;
                        String newChatName;
                        sql = "SELECT name FROM chatTable WHERE name LIKE '" + name1 + "' or name LIKE '" + name2 + "'";
                        resultSet = statement.executeQuery(sql);
                        if (resultSet.next()) {
                            newChatName = resultSet.getString("name");
                            boolean hasBlocked = false;
                            boolean wasBlocked = false;
                            sql = "SELECT " + receiver + " FROM blockTable";
                            resultSet = statement.executeQuery(sql);
                            while (resultSet.next()) {
                                if (resultSet.getString(receiver) != null) {
                                    if (resultSet.getString(receiver).equals(senderName)) {
                                        wasBlocked = true;
                                        break;
                                    }
                                }
                            }
                            sql = "SELECT " + senderName + " FROM blockTable";
                            resultSet = statement.executeQuery(sql);
                            while (resultSet.next()) {
                                if (resultSet.getString(senderName) != null) {
                                    if (resultSet.getString(senderName).equals(receiver)) {
                                        hasBlocked = true;
                                        break;
                                    }
                                }
                            }
                            if (wasBlocked){
                                errorLabel.setText("You have been blocked by " + receiver);
                            }
                            else if (hasBlocked){
                                errorLabel.setText("You have blocked " + receiver);
                            }
                            else {
                                sql = "insert into " + newChatName + " (senderName , receiverName , message , replyTo , seen , date , time , forwarded) values ('" + senderName + "' , '" + receiver + "' , '" + message + "' , '" + "0" + "' , \"no\" ,  " +
                                        LocalDate.now() + " , '" + LocalTime.now().format(Formatter1()) + "' , '" + sender + "')";
                                statement.executeUpdate(sql);
                                errorLabel.setText("Forwarded successfully :) ");
                            }
                        } else {
                            newChatName = senderName + "_" + receiver;
                            sql = "CREATE TABLE " + newChatName + " ( id int not null auto_increment , senderName varchar(255) not null , receiverName varchar(255) not null , message varchar (255) not null , replyTo int not null , seen varchar (255) not null , date varchar (255) not null , time varchar (255) not null , forwarded varchar (255) not null , primary key (id))";
                            statement.executeUpdate(sql);
                            sql = "insert into chatTable (name,type)" + "values ('" + newChatName + "' , \"pv\")";
                            statement.executeUpdate(sql);
                            sql = "insert into " + newChatName + " (senderName , receiverName , message , replyTo , seen , date , time , forwarded) values ('" + senderName + "' , '" + receiver + "' , '" + message + "' , '" + "0" + "' , \"no\" ,  " +
                                    LocalDate.now() + " , '" + LocalTime.now().format(Formatter1()) + "' , '" + sender + "')";
                            statement.executeUpdate(sql);
                            errorLabel.setText("Forwarded successfully :) ");
                        }
                    } else {
                        errorLabel.setText("No username with name " + receiver + " is available !");
                    }
                }
                if (!forwardToGroup.getText().isEmpty()){
                    receiver = forwardToGroup.getText();
                    sql = "SELECT * FROM chatTable WHERE name LIKE '" + receiver + "' AND type LIKE \"group\"";
                    resultSet = statement.executeQuery(sql);
                    if (resultSet.next()){
                        String newChatName = resultSet.getString("name");
                        boolean forwardAble = false;
                        sql = "SELECT * FROM groupTable WHERE groupName LIKE '" + receiver + "'";
                        resultSet = statement.executeQuery(sql);
                        while (resultSet.next()){
                            if (resultSet.getString("username").equals(senderName)){
                                forwardAble = true;
                                break;
                            }
                        }
                        if (forwardAble) {
                            sql = "insert into " + newChatName + " (senderName  , message , replyTo , date , time , forwarded) values ('" + senderName + "' , '" + message + "' , '" + "0" + "' , " +
                                    LocalDate.now() + " , '" + LocalTime.now().format(Formatter1()) + "' , '" + sender + "')";
                            statement.executeUpdate(sql);
                            System.out.println("Forwarded successfully :) ");
                        }
                        else {
                            errorLabel.setText("You are not a member of" + receiver);
                        }
                    }
                    else {
                        errorLabel.setText("No group with name " + receiver + " is available !");
                    }
                }
            } else {
                errorLabel.setText("No message with id " + id + " !");
            }
        }
    }

    public void reply(MouseEvent mouseEvent) throws SQLException {
        if (repliedMessageId.getText().isEmpty()){
            errorLabel.setText("Enter the message id to reply");
        }
        else {
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            String sql = "SELECT * FROM " + chatName + " WHERE id LIKE " + repliedMessageId.getText();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                replyTo = repliedMessageId.getText();
                errorLabel.setText("Replying to message " + replyTo + " ...");
            } else {
                errorLabel.setText("No message with id " + repliedMessageId.getText() + " !");
            }
        }
    }

    public void delete(MouseEvent mouseEvent) throws SQLException {
        if (deletedMessageId.getText().isEmpty()){
            errorLabel.setText("Enter the message id to delete !");
        }
        else {
            String id = deletedMessageId.getText();
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            String sql = "SELECT * FROM " + chatName + " WHERE id LIKE " + id;
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String message = resultSet.getString("senderName");
                if (message.equals(senderName)) {
                    sql = "SELECT * FROM " + chatName + " WHERE senderName LIKE '" + senderName + "' ORDER BY id DESC LIMIT 10 ";
                    resultSet = statement.executeQuery(sql);
                    boolean editable = false;
                    while (resultSet.next()) {
                        if (resultSet.getString("id").equals(id)) {
                            editable = true;
                            break;
                        }
                    }
                    if (editable) {
                        sql = "DELETE FROM " + chatName + " WHERE id LIKE " + id;
                        statement.executeUpdate(sql);
                        errorLabel.setText("Deleted successfully ");
                        sql = "UPDATE " + chatName + " SET replyTo = 0 WHERE replyTo LIKE " + id;
                        statement.executeUpdate(sql);
                        resetTable(chatName);
                    } else {
                        errorLabel.setText("You can only delete your last 10 messages !");
                    }
                } else {
                    errorLabel.setText("You can't delete other's messages ! ");
                }
            } else {
                errorLabel.setText("No message with id " + id + " !");
            }
        }
    }

    public void properAction(MouseEvent mouseEvent) throws SQLException, IOException {
        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
        String sql ;
        if (chatType.equals("group")){
            Pane pane = null;
            pane = FXMLLoader.load(getClass().getResource("/FXML/editGroup.fxml"));
            Main.scene.setRoot(pane);
            statement = conn.createStatement();
            sql = "SELECT " + senderName + " FROM followers";
            ResultSet resultSet = statement.executeQuery(sql);
            myFollowersList.clear();
            while (resultSet.next()) {
                if (resultSet.getString(senderName) != null) {
                    Followers follower = new Followers(resultSet.getString(senderName));
                    myFollowersList.add(follower);
                }
            }
        }
        else if (chatType.equals("pv")){
            if (haveBlocked){
                sql = "DELETE FROM blockTable WHERE " + senderName + " LIKE \"" + receiverName + "\"";
                statement.executeUpdate(sql);
                haveBlocked = false;
                errorLabel.setText("Unblocked successfully ");
                actionButton.setText("Block");
            }
            else {
                sql = "insert into blockTable (" + senderName + ") values (\"" + receiverName + "\")";
                statement.executeUpdate(sql);
                haveBlocked = true;
                errorLabel.setText("Blocked successfully ");
                actionButton.setText("Unblock");
            }
        }
        resetTable("groupTable");
        resetTable("blockTable");
    }

//    public void sendPhoto(MouseEvent mouseEvent) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("All Images", "*.*"),
//                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
//                new FileChooser.ExtensionFilter("PNG", "*.png")
//        );
//
//        Stage stage = (Stage) myGridPane.getScene().getWindow();
//        File file=  fileChooser.showOpenDialog(stage);
//        if(file!=null){
//            //myTextField.setText(file.getAbsolutePath());
//            Image image = new Image(file.toURI().toString());
//            //newChat.setPhotoAddress(file.toURI().toString());
//            //myImageView.setImage(image);
//        }
//    }

    public void sendMessage(MouseEvent mouseEvent) throws SQLException {
        if (textMessage.getText().isEmpty()){
            errorLabel.setText("Type a message first !");
        }
        else {
            String message = textMessage.getText();
            lastMessages.add(message);
            FXMLLoader fxmlLoader = new FXMLLoader(ChatScreen.class.getResource("/FXML/messageLayout.fxml"));
            Parent parent = null;
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MessageLayout message1 = fxmlLoader.getController();
            message1.setMessageLabel(message);
            messageVBox.getChildren().add(parent);
            String sql = "" ;
            if (chatType.equals("pv")) {
                sql = "insert into " + chatName + " (senderName , receiverName , message , replyTo , seen , date , time , forwarded) values ('" + senderName + "' , '" + receiverName + "' , '" + message + "' , '" + replyTo + "' , \"no\" , " +
                        LocalDate.now() + " , '" + LocalTime.now().format(Formatter1()) + "' , \"-\" )";
            }
            else if (chatType.equals("group")){
                sql = "insert into " + chatName + " (senderName , message , replyTo , date , time , forwarded) values ('" + senderName + "' , '" + message + "' , '" + replyTo + "' , " +
                        LocalDate.now() + " , '" + LocalTime.now().format(Formatter1()) + "' , \"-\")";
            }
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
            replyTo = "0";
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = new Image(receiverPhoto);
        chatPhoto.setImage(image);
        name.setText(receiverName);
        if (chatType.equals("group")){
            groupMembers.setText(members.get(0).getFollowerName() + " , " + members.get(1).getFollowerName() + " , ..." );
            actionButton.setText("Edit group");
        }
        else if (chatType.equals("pv")){
            groupMembers.setText("");
            if (haveBlocked){
                actionButton.setText("Unblock");
            }
            else {
                actionButton.setText("Block");
            }
        }
        for (int i = 0 ; i < lastMessages.size() ; i++){
            FXMLLoader fxmlLoader = new FXMLLoader(ChatScreen.class.getResource("/FXML/messageLayout.fxml"));
            Parent parent = null;
            try {
                parent = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MessageLayout message1 = fxmlLoader.getController();
            message1.setMessageLabel(lastMessages.get(i));
            messageVBox.getChildren().add(parent);
        }

    }

    private DateTimeFormatter Formatter1(){
        return DateTimeFormatter.ofPattern("HH:mm");
    }

    private void resetTable (String tableName) throws SQLException {
        int n = 1 ;
        int maxId = 0;
        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
        String sql = "SELECT * FROM " + tableName ;
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            if (resultSet.getInt("id") > n) {
                sql = "UPDATE " + tableName + " SET id = " + n + " WHERE id LIKE " + resultSet.getString("id");
                statement.executeUpdate(sql);
                sql = "UPDATE " + tableName + " SET replyTo = " + n + " WHERE replyTo LIKE " + resultSet.getString("id");
                statement.executeUpdate(sql);
            }
            n++;
        }
        sql = "SELECT MAX(id) FROM " + tableName;
        resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            maxId = resultSet.getInt(1);
        }
        sql = "ALTER TABLE " + tableName + " AUTO_INCREMENT = " + (maxId + 1) ;
        statement.executeUpdate(sql);
    }

}
