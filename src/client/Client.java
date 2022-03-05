package client;

import struct.User;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * client
 * Created by Minh Sĩ Lê
 * Date 12/14/2021 - 2:10 PM
 * Description: Client core
 */
public class Client {
    public static ArrayList<User> clientArray = null;
    private final ChatPeer2Peer server;
    private final String IPserver;
    private final int portServer;
    private String nameUser = "";
    private boolean isStop = false;
    private static int portClient;
    private final int timeOut = 5000;
    private Socket socketClient;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    /**
     * Constructor: Open socket client
     * @param ipS ip server
     * @param portS port server
     * @param portC port client
     * @param name name of user
     * @param dataUser online user list
     * @throws Exception Exception
     */
    public Client(String ipS,  int portS, int portC, String name, String dataUser) throws Exception {
        IPserver = ipS;
        nameUser = name;
        portServer = portS;
        portClient = portC;
        clientArray = getAllUser(dataUser);

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateFriendList();
            }
        }).start();
        server = new ChatPeer2Peer(nameUser);
        (new Request()).start();
    }

    /**
     * get client's port
     * @return port
     */
    public static int getPort() {
        return portClient;
    }

    /**
     * Request online user list
     * @throws Exception Exception
     */
    public void request() throws Exception {
        socketClient = new Socket();
        SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
        System.out.println(IPserver + "    " + portServer);
        socketClient.connect(addressServer);
        String msg = "RequestUser " + nameUser;
        os = new ObjectOutputStream(socketClient.getOutputStream());
        os.writeObject(msg);
        os.flush();
        is = new ObjectInputStream(socketClient.getInputStream());
        msg = (String) is.readObject();
        is.close();
        clientArray = getAllUser(msg);
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateFriendList();
            }
        }).start();
    }

    /**
     * Request chat with friend
     * @param host friend's ip
     * @param port friend's port
     * @param nameGuest friend's name
     * @throws Exception Exception
     */
    public void newChat(String host, int port, String nameGuest) throws Exception {
        final Socket connClient = new Socket(host, port);
        ObjectOutputStream sendRequestChat = new ObjectOutputStream(connClient.getOutputStream());
        String msgOut = "RequestChat " + nameUser;
        sendRequestChat.writeObject(msgOut);
        sendRequestChat.flush();
        ObjectInputStream receivedChat = new ObjectInputStream(connClient.getInputStream());
        String msgIn = (String) receivedChat.readObject();
        if (msgIn.equals("RequestChatDeny")) {
            JFrame messageFrame = new JFrame();
            JOptionPane.showMessageDialog(messageFrame, "Your friend denied connect with you");
            connClient.close();
        }
        else {
            new ChatFrame(nameUser, nameGuest, connClient, portClient);

        }
    }

    /**
     * Stop chat
     * @throws IOException Exception
     */
    public void exit() throws IOException {
        isStop = true;
        socketClient = new Socket();
        SocketAddress addressServer = new InetSocketAddress(IPserver, portServer);
        socketClient.connect(addressServer);
        String msg = "Exit " + nameUser;
        os = new ObjectOutputStream(socketClient.getOutputStream());
        os.writeObject(msg);
        os.flush();
        os.close();
        server.exit();
    }

    /**
     * Loop request online user
     */
    public class Request extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isStop) {
                try {
                    Thread.sleep(timeOut);
                    request();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update user online list
     */
    private void updateFriendList() {
        ClientMainFrame.resetList();
        for (User i: clientArray){
            if (!i.getName().equals(nameUser))
                ClientMainFrame.updateFriendMainFrame(i.getName());
        }
    }

    /**
     * Get user
     * @param dataUser data
     * @return user
     */
    private ArrayList<User> getAllUser(String dataUser) {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(dataUser.split("/")));
        ArrayList<User> user = new ArrayList<>();
        for (String s: list){
            String[] tmp = s.split(" ");
            System.out.println(Arrays.toString(tmp));
            user.add(new User(tmp[2], tmp[0],Integer.parseInt(tmp[1])));
        }
        return user;
    }
}
