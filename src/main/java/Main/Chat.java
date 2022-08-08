package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import java.sql.*;

import static Main.ChatScreen.lastMessages;


public class Chat implements Initializable {

    final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    final String username = "root";
    final String Password = "Tondar.1400";
    static ArrayList<Chats> privateChats = new ArrayList<>();
    static ArrayList<Chats> groupChats = new ArrayList<>();
    static ObservableList<Chats> chatsList = FXCollections.observableArrayList();
    static String senderName = "Mohammad";
    static String receiverName = "" ;
    static String receiverPhoto = "" ;
    static String chatName = "" ;
    static String chatType = "" ;
    static String roll = "" ;
    static ArrayList<Followers> myFollowersList = new ArrayList<>();
    static ArrayList<Followers> members = new ArrayList<>();
    static ObservableList<Followers> showMembers = FXCollections.observableArrayList();
    static boolean haveBlocked;
    static boolean isBlocked;


    @FXML
    TableView chatsTable ;
    @FXML
    TableColumn chatPhoto ;
    @FXML
    TableColumn myChatName ;
    @FXML
    TableColumn chatLastMessage ;
    @FXML
    TableColumn chatTime ;
    @FXML
    Label errorLabel;


    public void followingsPost(MouseEvent mouseEvent) {
    }

    public void viewProfile(MouseEvent mouseEvent) {
    }

    public void explorePost(MouseEvent mouseEvent) {
    }

    public void exploreUser(MouseEvent mouseEvent) {
    }

    public void createPost(MouseEvent mouseEvent) {
    }

    public void recommendedUser(MouseEvent mouseEvent) {
    }

    public void recommendedPost(MouseEvent mouseEvent) {
    }

    public void chat(MouseEvent mouseEvent) throws IOException, SQLException {

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
    }

    public void logout(MouseEvent mouseEvent) {
    }

    public void showChat(MouseEvent mouseEvent) throws IOException, SQLException {
        ObservableList<Chats> selectedChat ;
        selectedChat = chatsTable.getSelectionModel().getSelectedItems();
        if (selectedChat.size() == 0){
            errorLabel.setText("Select a chat first !");
        }
        else {
            receiverName = selectedChat.get(0).getReceiverName();
            receiverPhoto = selectedChat.get(0).getPhotoAddress();
            chatName = selectedChat.get(0).getChatName();
            chatType = selectedChat.get(0).getType();
            roll = selectedChat.get(0).getRoll();
            members.clear();
            Pane pane = null;
            pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
            Main.scene.setRoot(pane);

            haveBlocked = false;
            isBlocked = false;
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            Statement statement1 = conn.createStatement();
            String sql = "SELECT " + senderName + " FROM blockTable";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public void newGroup(MouseEvent mouseEvent) throws IOException, SQLException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/newGroupChat.fxml"));
        Main.scene.setRoot(pane);

        members.clear();
        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
        String sql = "SELECT " + senderName + " FROM followers";
        ResultSet resultSet = statement.executeQuery(sql);
        myFollowersList.clear();
        while (resultSet.next()) {
            if (resultSet.getString(senderName) != null) {
                Followers follower = new Followers(resultSet.getString(senderName));
                myFollowersList.add(follower);
            }
        }
    }

    public void newPV(MouseEvent mouseEvent) throws IOException, SQLException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/newPVChat.fxml"));
        Main.scene.setRoot(pane);

        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
        String sql = "SELECT " + senderName + " FROM followers";
        ResultSet resultSet = statement.executeQuery(sql);
        myFollowersList.clear();
        while (resultSet.next()) {
            if (resultSet.getString(senderName) != null) {
                Followers follower = new Followers(resultSet.getString(senderName));
                myFollowersList.add(follower);
            }
        }
    }

    public void allChats(MouseEvent mouseEvent) {
        chatsList.clear();
        chatsList.addAll(privateChats);
        chatsList.addAll(groupChats);
    }

    public void pvChats(MouseEvent mouseEvent) {
        chatsList.clear();
        chatsList.addAll(privateChats);
    }

    public void groupChats(MouseEvent mouseEvent) {
        chatsList.clear();
        chatsList.addAll(groupChats);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        chatPhoto.setCellValueFactory(new PropertyValueFactory<>("photoAddress"));
        myChatName.setCellValueFactory(new PropertyValueFactory<>("receiverName"));
        chatTime.setCellValueFactory(new PropertyValueFactory<>("lastTime"));
        chatLastMessage.setCellValueFactory(new PropertyValueFactory<>("lastMessage"));
        chatsTable.setItems(chatsList);
        chatTime.setSortType(TableColumn.SortType.DESCENDING);
        chatsTable.getSortOrder().add(chatTime);
        chatsTable.sort();

    }

}
