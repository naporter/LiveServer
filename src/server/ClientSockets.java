package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class ClientSockets extends Thread{

    private ConnectionListener connectionListener;
    private Controller serverController;
    private Socket client;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String userName;
    private String clientId;
    private boolean isActive;
    private Random rand = new Random();

    ClientSockets(Socket client, ConnectionListener connectionListener, Controller serverController) throws IOException {
        this.serverController = serverController;
        this.connectionListener = connectionListener;
        this.setDaemon(true);
        this.setActive(true);
        this.setClient(client);
        this.setDos(new DataOutputStream(client.getOutputStream()));
        this.setDis(new DataInputStream(client.getInputStream()));
        this.setUserName(this.dis.readUTF());
        this.setClientId(this.getUserName() + " " + makeRandomId());
        this.serverController.appendServerText(this.getUserName() + " is now connected to the server.");
        this.dos.writeUTF("Server");
        this.dos.writeUTF("Welcome to the chat, " + this.getUserName() + "!\nYou can exit the chat by simply closing the window or sending '.exit' to any user.");
        this.start();
    }

    //setters
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setClientId(String clientId){
        this.clientId = clientId;
    }
    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }
    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }
    public void setClient(Socket client) {
        this.client = client;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    //getters
    public String getUserName(){
        return this.userName;
    }
    public String getClientId(){
        return this.clientId;
    }
    public DataOutputStream getDos() {
        return dos;
    }
    public DataInputStream getDis() {
        return dis;
    }
    public Socket getClient() {
        return client;
    }
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void run() { //starts a new thread for each client connection
            try {
                while (!client.isClosed()) {
                    String toWhoString = dis.readUTF();
                    if (toWhoString.equals(".exit")) {
                        this.isActive = false;
                        try {
                            this.getDos().close();
                            this.getDis().close();
                            this.client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (toWhoString.equals("getActiveClients")) {
                        serverController.appendServerText(this.getUserName() + " has requested active clients.");
                        getDos().writeUTF("sendingUsers"); //lets client know that there are incoming users names
                        getDos().write(connectionListener.getAllConnected().length);
                        for (int i = 0; i < connectionListener.getAllConnected().length; i++) {
                            this.getDos().writeUTF(connectionListener.getAllConnected()[i]);
                        }
                    } else {
                        String incomingString = dis.readUTF();
                        serverController.appendServerText(this.getClientId() + ": " + incomingString);
                        DataOutputStream receiver = connectionListener.getClient(toWhoString);
                        if (receiver != null){
                            receiver.writeUTF(this.getClientId());
                            receiver.writeUTF(incomingString);
                        } else {
                            this.getDos().writeUTF("Server"); //clients first read who the message is from
                            this.getDos().writeUTF(toWhoString + " is no longer active.");
                        }
                    }
                }
            } catch (IOException e) {
                serverController.appendServerText(this.getUserName() + " has left the chat.");
                connectionListener.dropClient(this.getClientId());
        }
    }

    private int makeRandomId(){ //creates a random id that is appended onto the end of the username to create the client id
        int id = rand.nextInt(1000);
        return id;
    }

}
