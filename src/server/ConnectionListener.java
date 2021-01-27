package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionListener extends Thread{

    private Controller serverController;
    public static ArrayList<ClientSockets> activeClients = new ArrayList<>();
    private Socket socket;
    private ServerSocket serverSocket;


    ConnectionListener(){

    }

    ConnectionListener(Controller serverController, ServerSocket serverSocket) {
        this.serverController = serverController;
        this.setDaemon(true);
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        serverController.appendServerText("Server started.");
        try {
            while (!serverSocket.isClosed()){
                serverController.appendServerText("Awaiting client connection...");
                socket = serverSocket.accept();
                activeClients.add(new ClientSockets(socket, this, this.serverController));
            }
        } catch (IOException e) {
            serverController.appendServerText("Server connection closed.");
        }
    }

    public DataOutputStream getClient(String toWho){
        for (ClientSockets client: activeClients){
            if(client.getClientId().equals(toWho)){
                return client.getDos();
            }
        }
        return null;
    }
    public String[] getAllConnected(){ //returns the user names for all connected clients
        String[] activeUsers = new String[activeClients.size()];
        int i = 0;
        for (ClientSockets client: activeClients){
            activeUsers[i] = client.getClientId();
            i++;
        }
        return activeUsers;
    }

    public void dropClient(String clientId){
        for(ClientSockets client : activeClients){
            if(client.getClientId().equals(clientId)){
                activeClients.remove(client);
                break;
            }
        }
    }

    public static void disconnectAllUsers(){ //removes all client connections
        activeClients.clear();
        System.out.println("Cleared active clients.");
    }
}
