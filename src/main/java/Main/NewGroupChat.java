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

import static Main.Chat.senderName;

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


    public void back(MouseEvent mouseEvent) throws IOException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chat.fxml"));
        Main.scene.setRoot(pane);
    }

    public void addMember(MouseEvent mouseEvent) {
        ObservableList<Followers> selectedMember ;
        selectedMember = followersTable.getSelectionModel().getSelectedItems();



    }

    public void createGroup(MouseEvent mouseEvent) throws SQLException {
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


            ArrayList<Followers> followersList = new ArrayList<>();
            try{
                Connection conn = DriverManager.getConnection(DB_url, username, Password);
                Statement statement2 = conn.createStatement();
                String sql2 = "SELECT " + senderName + " FROM followers";


                ResultSet resultSet2 = statement2.executeQuery(sql2);
                while (resultSet2.next()) {
                    if (resultSet2.getString(senderName) != null) {
                        Followers follower = new Followers(resultSet2.getString(senderName));
                        followersList.add(follower);
                        Followers.followersList.add(resultSet2.getString(senderName));
                        System.out.println(resultSet2.getString(senderName));
                    }
                }
                followersTable.setItems(FollowersList(followersList));
            }
            catch (Exception e){

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
        followersColumn.setCellValueFactory(new PropertyValueFactory<>("followerName"));
        //followersTable.setItems(FollowersList(selectedMember));
    }


    private ObservableList<Followers> FollowersList (ArrayList<Followers> temp) {
        ObservableList<Followers> followers = FXCollections.observableArrayList();
        followers.addAll(temp);
        return followers;
    }

    private ObservableList<Followings> FollowingsList (ArrayList<Followings> temp) {
        ObservableList<Followings> followings = FXCollections.observableArrayList();
        followings.addAll(temp);
        return followings;
    }

}
