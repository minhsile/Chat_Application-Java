package server;

import struct.Account;
import struct.User;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * server
 * Created by Minh Sĩ Lê
 * Date 12/14/2021 - 12:39 PM
 * Description: Server core
 */
public class Server {
    private final ArrayList<User> listUser;
    private final ArrayList<Account> listAccount;
    private final ServerSocket server;
    private Socket connection;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private boolean isStop = false;

    /**
     * Start server
     * @param port port server
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port);
        connection = new Socket();
        listUser = new ArrayList<>();
        listAccount = new ArrayList<>();
        readDataAccount();
        (new WaitForConnect()).start();
    }

    /**
     * Read data account
     */
    private void readDataAccount(){
        File tempFile = new File("dataAccount.txt");
        boolean exists = tempFile.exists();
        if (exists) {
            try {
                BufferedReader br = new BufferedReader(new FileReader("dataAccount.txt"));
                while (true) {
                    String line = br.readLine();
                    if (line == null)
                        break;
                    String[] tmp = line.split(" ");
                    listAccount.add(new Account(tmp[0], tmp[1], tmp[2]));
                }
                br.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Save data account
     */
    private void saveDataAccount(){
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("dataAccount.txt"));
            for (Account i: listAccount) {
                bw.write(i.getUserName() + " " + i.getPw()+ " " + i.getName() + "\n");
            }
            bw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Check valid account
     * @param username username of account
     * @param pw password of account
     * @return boolean
     */
    private boolean checkValidAccount(String username, String pw){
        for (Account i: listAccount)
            if (i.getUserName().equals(username) && i.getPw().equals(pw))
                return true;
        return false;
    }

    /**
     * Check exist of user
     * @param username username of account
     * @return boolean
     */
    private boolean checkExistUser(String username){
        for (Account i: listAccount)
            if (i.getUserName().equals(username))
                return true;
        return false;
    }

    /**
     * Retrieval name of account
     * @param username username of account
     * @param pw password of account
     * @return name
     */
    private String getNameFromAcc(String username, String pw){
        for (Account i: listAccount)
            if (i.getUserName().equals(username) && i.getPw().equals(pw))
                return i.getName();
        return null;
    }

    /**
     * Remove user
     * @param nameUser username
     */
    private void removeUser(String nameUser){
        listUser.removeIf(user -> user.getName().equals(nameUser));
    }

    private void saveNewUser( String ip, int port, String name) {
        User newUser = new User(name, ip, port);
        listUser.add(newUser);
    }

    private void saveNewAccount( String username, String pw, String name) {
        Account newAccount = new Account(username, pw, name);
        listAccount.add(newAccount);
    }

    private ArrayList<String> getUser(String msg) {
        return new ArrayList<String>(Arrays.asList(msg.split(" ")));
    }

    /**
     * Server get request from client
     * @throws Exception exception
     */
    private void waitForConnection() throws Exception{
        connection = server.accept();
        is = new ObjectInputStream(connection.getInputStream());
        String msg = (String) is.readObject();
        System.out.println(msg);
        String[] msg_temp = msg.split(" ",2);
        switch (msg_temp[0]) {
            case ("Connect") -> {
                ArrayList<String> getData = getUser(msg_temp[1]);
                os = new ObjectOutputStream(connection.getOutputStream());
                if (checkValidAccount(getData.get(0), getData.get(1))) {
                    String nameUser = getNameFromAcc(getData.get(0), getData.get(1));
                    saveNewUser(getData.get(2), Integer.parseInt(getData.get(3)), nameUser);
                    ServerUI.updateNoti(nameUser + " connected");
                    os.writeObject("Success " + nameUser + "\n" + sendUserList());
                } else {
                    os.writeObject("Fail");
                }
                os.flush();
                os.close();
            }
            case ("RequestUser") -> {
                os = new ObjectOutputStream(connection.getOutputStream());
                os.writeObject(sendUserList());
                os.flush();
                os.close();
            }
            case ("Regis") -> {
                ArrayList<String> getData = getUser(msg_temp[1]);
                os = new ObjectOutputStream(connection.getOutputStream());
                if (!checkExistUser(getData.get(0))) {
                    saveNewAccount(getData.get(0), getData.get(1), getData.get(2));
                    ServerUI.updateNoti(getData.get(2) + " registered");
                    saveDataAccount();
                    os.writeObject("Success");
                } else {
                    os.writeObject("Fail");
                }
                os.flush();
                os.close();
            }
            case ("Exit") -> {
                removeUser(msg_temp[1]);
                ServerUI.updateNoti(msg_temp[1] + " disconnected");
            }
        }
    }

    /**
     * Wait client connect
     */
    public class WaitForConnect extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (!isStop) {
                    waitForConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send list user to client
     * @return string
     */
    private String sendUserList() {
        StringBuilder msg = new StringBuilder();
        for (User i: listUser){
            msg.append(i.getHost()).append(" ");
            msg.append(i.getPort()).append(" ");
            msg.append(i.getName());
            msg.append("/");
        }
        return msg.toString();
    }

    /**
     * Stop server
     * @throws IOException exception
     */
    public void stop() throws IOException {
        isStop = true;
        connection.close();
        server.close();
    }
}
