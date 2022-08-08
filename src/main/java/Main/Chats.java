package Main;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Chats {
    private String chatName = "";
    private String type = "";
    private String lastMessage = "";
    private String lastTime = "";
    private String photoAddress = "";
    private String receiverName = "";
    private String roll = "" ;
    private ImageView photo = new ImageView() ;

    public Chats(){
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setPhotoAddress(String photoAddress) {
        this.photoAddress = photoAddress;
        this.photo.setImage(new Image(photoAddress));
    }

    public String getPhotoAddress() {
        return photoAddress;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getRoll() {
        return roll;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public ImageView getPhoto() {
        return photo;
    }
}
