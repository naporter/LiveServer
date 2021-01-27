package server;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public Button startBtn;
    public TextArea serverText;
    public Button stopBtn;
    public Button clearText;

    private ServerSocket serverSocket;

    @FXML
    private void startServer() throws IOException {
        new ConnectionListener(this, serverSocket = new ServerSocket(3000)).start();
        stopBtn.setDisable(false);
        startBtn.setDisable(true);
    }

    @FXML
    private void stopServer() throws IOException {
        serverSocket.close();
        stopBtn.setDisable(true);
        startBtn.setDisable(false);
        ConnectionListener.disconnectAllUsers();
    }

    @FXML
    private void clearServerWindow(){
        serverText.clear();
    }

    public void appendServerText(String s){
        serverText.appendText(s + "\n");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stopBtn.setDisable(true);
    }

}
