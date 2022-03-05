package client;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Chat
 * Created by Minh Sĩ Lê
 * Date 12/17/2021 - 11:42 AM
 * Description: Server peer
 */
public class ChatPeer2Peer {
    private String username = "";
    private final ServerSocket serverPeer;
    private final int port;
    private boolean isStop = false;

    /**
     * Constructor
     * @param name name
     * @throws Exception Exception
     */
    public ChatPeer2Peer(String name) throws Exception {
        username = name;
        port = Client.getPort();
        serverPeer = new ServerSocket(port);
        (new waitUserConnect()).start();
    }

    /**
     * Close server
     * @throws IOException Exception
     */
    public void exit() throws IOException {
        isStop = true;
        serverPeer.close();
    }

    /**
     * Wait another client connect to
     */
    class waitUserConnect extends Thread {
        Socket connection;
        ObjectInputStream getRequest;
        @Override
        public void run() {
            super.run();
            while (!isStop) {
                try {
                    connection = serverPeer.accept();
                    getRequest = new ObjectInputStream(connection.getInputStream());
                    String msg = (String) getRequest.readObject();
                    String[] msg_temp = msg.split(" ",2);
                    String name = msg_temp[1];
                    JFrame messageFrame = new JFrame();
                    int check = JOptionPane.showConfirmDialog(messageFrame, name + " want to connect with you!", null, JOptionPane.YES_NO_OPTION);
                    ObjectOutputStream send = new ObjectOutputStream(connection.getOutputStream());
                    if (check == 1) {
                        send.writeObject("RequestChatDeny");

                    } else if (check == 0) {
                        send.writeObject("RequestChatAccept");
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JFrame frameChat = new JFrame("Chat");
                        frameChat.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        frameChat.setBounds(100, 100,500,300);
                        frameChat.setResizable(false);
                        ChatFrame chat= new ChatFrame(username, name, connection, port);
                        frameChat.setContentPane(chat);
                        frameChat.setVisible(true);

                    }
                    send.flush();
                } catch (Exception e) {
                    break;
                }
            }
            try {
                serverPeer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
