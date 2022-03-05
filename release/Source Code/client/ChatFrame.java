package client;

import struct.DataFile;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

/**
 * client
 * Created by Minh Sĩ Lê
 * Date 12/16/2021 - 9:47 PM
 * Description: Chat Frame
 */
public class ChatFrame extends JFrame{
    //Frame
    private final JTextField txtMessage = new JTextField();
    private final JTextPane txtDisplayMessage = new JTextPane();
    private final JButton btnSendFile = new JButton("File");
    private final JButton btnSend = new JButton("Send");
    private final JProgressBar progressBar = new JProgressBar();

    Font myFontTitle = new Font("Tahoma", Font.PLAIN, 28);
    Font myFontContent = new Font("Serif", Font.BOLD, 18);
    private final ChatFrame frame = this;
    @Serial
    private static final long serialVersionUID = 1L;
    // Socket
    private static final String URL_DIR = System.getProperty("user.dir");
    private String nameGuest, nameFile = "";
    public boolean isStop = false, isSendFile = false, isReceiveFile = false;
    // ChatRoom
    private ChatRoom chat;

    /**
     * Constructor
     * @param nameUser user's name
     * @param nameGuest friend's name
     * @param socket socket
     * @param port port
     * @throws Exception Exception
     */
    public ChatFrame(String nameUser, String nameGuest, Socket socket, int port) throws Exception {
        JFrame.setDefaultLookAndFeelDecorated(true);
        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100,500,300);
        setContentPane(chatPanel(nameUser, nameGuest, socket, port));
        setResizable(false);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
            try {
                isStop = true;
                frame.dispose();
                chat.sendMessage("CloseChat");
                chat.stopChat();
                System.gc();
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }

    /**
     * Chat Panel
     * @param nameUser user's name
     * @param nameGuest friend's name
     * @param socket socket
     * @param port port
     * @return JPanel
     * @throws Exception Exception
     */
    public JPanel chatPanel(String nameUser, String nameGuest, Socket socket, int port) throws Exception {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        chat = new ChatRoom(socket, nameUser, nameGuest);
        chat.start();

        JLabel titleLabel = new JLabel("Chat with " + nameGuest);
        titleLabel.setFont(myFontTitle);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        txtDisplayMessage.setEditable(false);
        txtDisplayMessage.setContentType("text/html");
        txtDisplayMessage.setBackground(Color.BLACK);
        txtDisplayMessage.setForeground(Color.WHITE);
        txtDisplayMessage.setFont(new Font("Courier New", Font.PLAIN, 18));
        appendToPane(txtDisplayMessage, "<div class='clear' style='background-color:white'></div>");
        JScrollPane scrollPane = new JScrollPane(txtDisplayMessage);
        mainPanel.add(scrollPane);
        mainPanel.add(progressBar);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));
        txtMessage.setFont(myFontContent);
        txtMessage.setColumns(30);
        txtMessage.setMaximumSize(txtMessage.getPreferredSize());
        bottomPanel.add(txtMessage);
        bottomPanel.add(btnSendFile);
        bottomPanel.add(btnSend);

        // Button send message
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = txtMessage.getText();
                if (msg.equals(""))
                    return;
                txtMessage.setText("");
                try {
                    updateChat_send(msg);
                    msg = "messageChat " + msg;
                    chat.sendMessage(msg);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        // Can press "Enter" to send
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnSend.doClick();
                }
            }
        });
        mainPanel.add(bottomPanel);

        // Button send file
        btnSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    isSendFile = true;
                    String path_send = (fileChooser.getSelectedFile().getAbsolutePath());
                    System.out.println(path_send);
                    nameFile = fileChooser.getSelectedFile().getName();
                    File file = fileChooser.getSelectedFile();
                    try {
                        chat.sendMessage("SendFile_Start " + nameFile);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("nameFile: " + nameFile);
                    try {
                        chat.sendFile(file);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        return mainPanel;
    }

    /**
     * Add message to field
     * @param tp text Pane
     * @param msg message
     */
    private void appendToPane(JTextPane tp, String msg) {
        HTMLDocument doc = (HTMLDocument) tp.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit) tp.getEditorKit();
        try {

            editorKit.insertHTML(doc, doc.getLength(), msg, 0, 0, null);
            tp.setCaretPosition(doc.getLength());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update chat from sender
     * @param msg message
     */
    public void updateChat_send(String msg) {
        appendToPane(txtDisplayMessage,
                "<table class='bang' style='color: white; clear:both; width: 100%;'>" + "<tr align='right'>"
                        + "<td style='width: 59%; '></td>" + "<td style='width: 40%; background-color: #0084ff;'>"
                        + msg + "</td> </tr>" + "</table>");
    }

    /**
     * Update chat from receiver
     * @param msg message
     */
    public void updateChat_receive(String msg) {
        appendToPane(txtDisplayMessage, "<div class='left' style='width: 40%; background-color: #f1f0f0;'>" + "    "
                + msg + "<br>" + "</div>");
    }

    /**
     * Function of chat
     */
    private class ChatRoom extends Thread {
        private final Socket connect;
        private boolean continueSendFile = true, finishReceive = false;
        private int sizeOfSend = 0, sizeOfData = 0, sizeFile = 0, sizeReceive = 0;
        private String nameFileReceive = "";
        private InputStream inFileSend;
        private DataFile dataFile;

        public ChatRoom(Socket connection, String name, String guest) {
            connect = connection;
            nameGuest = guest;
        }

        /**
         * Get request from friend client
         */
        @Override
        public void run() {
            super.run();
            OutputStream out = null;
            while (!isStop) {
                try {
                    ObjectInputStream inObj = new ObjectInputStream(connect.getInputStream());
                    Object obj = inObj.readObject();
                    if (obj instanceof String) {
                        String msg = obj.toString();
                        String[] msg_temp = msg.split(" ", 2);
                        switch (msg_temp[0]) {
                            // Message
                            case "messageChat" -> updateChat_receive(msg_temp[1]);
                            // Close chat
                            case "CloseChat" -> {
                                isStop = true;
                                JOptionPane.showMessageDialog(frame, nameGuest + " closed chat with you! This chat window will also be closed!","Notification", JOptionPane.INFORMATION_MESSAGE);
                                try {
                                    isStop = true;
                                    frame.dispose();
                                    chat.stopChat();
                                    System.gc();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                connect.close();
                            }
                            // Start send file
                            case "SendFile_Start" -> {
                                finishReceive = false;
                                nameFileReceive = msg_temp[1];
                                isReceiveFile = true;
                                File fileReceive = new File(URL_DIR + "/" + nameFileReceive);
                                if (!fileReceive.exists()) {
                                    fileReceive.createNewFile();
                                }
                                out = new FileOutputStream(URL_DIR +  "/" + nameFileReceive);
                            }
                            // End send file
                            case "SendFile_End" -> {
                                updateChat_send("You received file: " + nameFileReceive + " with size " + sizeReceive + " KB");
                                sizeReceive = 0;
                                out.flush();
                                out.close();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showSaveFile();
                                    }
                                }).start();
                                finishReceive = true;
                            }
                        }
                    }
                    // Package of file
                    else if (obj instanceof DataFile) {
                        DataFile dataF = (DataFile) obj;
                        ++sizeReceive;
                        out.write(dataF.data);
                    }
                } catch (Exception e) {
                    File fileTemp = new File(URL_DIR + nameFileReceive);
                    if (fileTemp.exists() && !finishReceive) {
                        fileTemp.delete();
                    }
                }
            }
        }

        /**
         * Choose path save file
         */
        private void showSaveFile() {
            while (true) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath() + "/" + nameFileReceive);
                    try {
                        file.createNewFile();
                        Thread.sleep(1000);
                        InputStream input = new FileInputStream(URL_DIR + "/" + nameFileReceive);
                        OutputStream output = new FileOutputStream(file.getAbsolutePath());
                        copyFileReceive(input, output, URL_DIR + "\\" + nameFileReceive);
                    } catch (Exception e) {
                        System.out.println("Error");
                    }
                    break;
                }
            }
        }

        /**
         * send object to friend
         */
        public synchronized void sendMessage(Object obj) throws Exception {
            ObjectOutputStream outObj = new ObjectOutputStream(connect.getOutputStream());
            if (obj instanceof String) {
                String message = obj.toString();
                outObj.writeObject(message);
                outObj.flush();
                if (isReceiveFile)
                    isReceiveFile = false;
            } else if (obj instanceof DataFile) {
                outObj.writeObject(obj);
                outObj.flush();
            }
        }

        /**
         * Get information of file
         * @param file File
         * @throws Exception Exception
         */
        private void getData(File file) throws Exception {
            File fileData = file;
            if (fileData.exists()) {
                sizeOfSend = 0;
                dataFile = new DataFile(1024);
                sizeFile = (int) fileData.length();
                sizeOfData = sizeFile % 1024 == 0 ? (int) (fileData.length() / 1024)
                        : (int) (fileData.length() / 1024) + 1;
                inFileSend = new FileInputStream(fileData);
            }
        }

        /**
         * Send file process
         * @param file File
         * @throws Exception Exception
         */
        public void sendFile(File file) throws Exception {
            btnSendFile.setEnabled(false);
            getData(file);
            progressBar.setVisible(true);
            progressBar.setValue(0);
            System.out.println("sizeOfData : " + sizeOfData);
            do {
                if (continueSendFile) {
                    System.out.println("sizeOfSend : " + sizeOfSend);
                    continueSendFile = false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                System.out.println("gui file");
                                inFileSend.read(dataFile.data);
                                sendMessage(dataFile);
                                sizeOfSend += 1;

                                if (sizeOfSend == sizeOfData - 1) {
                                    int size = sizeFile - sizeOfSend * 1024;
                                    dataFile = new DataFile(size);
                                }
                                progressBar.setValue(sizeOfSend * 100 / sizeOfData);
                                if (sizeOfSend >= sizeOfData) {
                                    inFileSend.close();
                                    isSendFile = true;
                                    sendMessage("SendFile_End");
                                    updateChat_send("You sent file: " + nameFile + " with size " + sizeOfData + " KB");
                                    progressBar.setVisible(false);
                                    isSendFile = false;
                                    btnSendFile.setEnabled(true);
                                    inFileSend.close();
                                }
                                continueSendFile = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            } while (sizeOfSend < sizeOfData);
        }

        /**
         * Close connection
         */
        public void stopChat() {
            try {
                connect.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Save file to chosen path
     * @param inputStr src file
     * @param outputStr des file
     * @param path des path
     * @throws IOException IOException
     */
    public void copyFileReceive(InputStream inputStr, OutputStream outputStr, String path) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStr.read(buffer)) > 0) {
            outputStr.write(buffer, 0, length);
        }
        inputStr.close();
        outputStr.close();
        File fileTemp = new File(path);
        fileTemp.delete();
    }
}
