package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    public Chat() throws SQLException {
    }

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

    public void showChat(MouseEvent mouseEvent) throws IOException {
        ObservableList<Chats> selectedChat ;
        selectedChat = chatsTable.getSelectionModel().getSelectedItems();
        receiverName = selectedChat.get(0).getReceiverName();
        receiverPhoto = selectedChat.get(0).getPhotoAddress();
        chatName = selectedChat.get(0).getChatName();
        chatType = selectedChat.get(0).getType();
        roll = selectedChat.get(0).getRoll();
        members.clear();
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
        Main.scene.setRoot(pane);
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
