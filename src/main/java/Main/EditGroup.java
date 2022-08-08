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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static Main.Chat.*;

public class EditGroup implements Initializable {

    final String DB_url = "jdbc:mysql://localhost/users?serverTimezone=UTC";
    final String username = "root";
    final String Password = "Tondar.1400";



    @FXML
    GridPane myGridPane;
    @FXML
    TextField myTextField;
    @FXML
    ImageView myImageView;
    @FXML
    Label groupName;
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


    public void setImage(MouseEvent mouseEvent) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
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
            receiverPhoto = file.toURI().toString();
            myImageView.setImage(image);
            String sql = "UPDATE chatTable SET image = '" + receiverPhoto + "' WHERE name LIKE '" + chatName + "' AND type LIKE \"group\"";
            statement.executeUpdate(sql);
        }
    }

    public void finish(MouseEvent mouseEvent) throws IOException {
        Pane pane = null;
        pane = FXMLLoader.load(getClass().getResource("/FXML/chatScreen.fxml"));
        Main.scene.setRoot(pane);
    }

    public void addMember(MouseEvent mouseEvent) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_url, username, Password);
        Statement statement = conn.createStatement();
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
                String sql = "insert into groupTable (groupName , username , roll) values ('" + chatName + "' , '" + followers.getFollowerName() + "' , \"member\")";
                statement.executeUpdate(sql);
                addedMembers.refresh();
            }
        }
    }

    public void removeMember(MouseEvent mouseEvent) throws SQLException {
        if (roll.equals("member")){
            messageLabel.setText("Only the admin can remove members !");
        }
        else if (roll.equals("admin")){
            Connection conn = DriverManager.getConnection(DB_url, username, Password);
            Statement statement = conn.createStatement();
            ObservableList<Followers> selectedMember ;
            selectedMember = addedMembers.getSelectionModel().getSelectedItems();
            if (selectedMember.size() == 0){
                messageLabel.setText("You haven't select a member !");
            }
            else {
                Followers followers = new Followers(selectedMember.get(0).getFollowerName());
                ArrayList<Followers> removed = new ArrayList<>();
                for (Followers a :members) {
                    if (a.getFollowerName().equals(followers.getFollowerName())){
                        removed.add(a);
                        break;
                    }
                }
                members.removeAll(removed);
                showMembers.clear();
                showMembers.addAll(members);
                messageLabel.setText("Removed successfully");
                String sql = "DELETE FROM groupTable WHERE username LIKE '" + followers.getFollowerName() + "' AND groupName LIKE '" + chatName + "'";
                statement.executeUpdate(sql);
                addedMembers.refresh();
            }
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle){
        Image image = new Image(receiverPhoto);
        myImageView.setImage(image);
        groupName.setText(receiverName);
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
