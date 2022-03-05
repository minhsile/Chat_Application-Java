package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

/**
 * client
 * Created by Minh Sĩ Lê
 * Date 12/14/2021 - 2:10 PM
 * Description: Connect frame include configure, register and login
 */
public class ConnectFrame extends JPanel {
    private static JFrame frame;
    private final JTextField IPServer = new JTextField();
    private final JTextField PortServer = new JTextField();
    private final JTextField PortClient = new JTextField();
    private final JTextField name = new JTextField();
    private final JButton btnConnect = new JButton("Connect");

    Font myFontTitle = new Font("Tahoma", Font.PLAIN, 28);
    Font myFontContent = new Font("Serif", Font.BOLD, 18);

    /**
     * Main function
     * @param args
     */
    public static void main(String[]args){
        createAndShowGUI();
    }

    /**
     * Create and show GUI
     */
    private static void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100,470,270);
        frame.setResizable(false);

        ConnectFrame newContentPane = new ConnectFrame();
        frame.setContentPane(newContentPane);

        frame.setVisible(true);
    }

    /**
     * Create frame
     */
    public ConnectFrame(){
        JTabbedPane tabbedPane = new JTabbedPane();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JPanel panelLogin = new JPanel();
        panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.PAGE_AXIS));
        tabbedPane.add("Configure Server",configPanel());
        tabbedPane.add("Register",registerPanel());
        tabbedPane.add("Login",panelLogin);
        add(tabbedPane);

        // Login panel
        JLabel Label = new JLabel("Login");
        Label.setAlignmentX(CENTER_ALIGNMENT);
        Label.setFont(myFontTitle);
        panelLogin.add(Label);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.LINE_AXIS));
        panelLogin.add(panelContent);

        JPanel panelConfig = new JPanel();
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.LINE_AXIS));
        panelContent.add(panelConfig);

        // Label configure
        panelConfig.add(Box.createRigidArea(new Dimension(10,10)));
        JPanel panelLabelConfig = new JPanel();
        panelLabelConfig.setLayout(new BoxLayout(panelLabelConfig, BoxLayout.PAGE_AXIS));
        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelUsername = new JLabel("Username");
        labelUsername.setFont(myFontContent);
        panelLabelConfig.add(labelUsername);

        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelPw = new JLabel("Password");
        labelPw.setFont(myFontContent);
        panelLabelConfig.add(labelPw);
        panelConfig.add(panelLabelConfig);

        // Field configure
        JPanel panelTxtConfig = new JPanel();
        panelTxtConfig.setLayout(new BoxLayout(panelTxtConfig, BoxLayout.PAGE_AXIS));
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JTextField txtUsername = new JTextField();
        txtUsername.setFont(myFontContent);
        txtUsername.setColumns(10);
        txtUsername.setMaximumSize(txtUsername.getPreferredSize());
        panelTxtConfig.add(txtUsername);

        panelConfig.add(Box.createRigidArea(new Dimension(10,0)));
        JPasswordField txtPw = new JPasswordField();
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        txtPw.setFont(myFontContent);
        txtPw.setColumns(10);
        txtPw.setMaximumSize(txtPw.getPreferredSize());
        panelTxtConfig.add(txtPw);
        panelConfig.add(panelTxtConfig);

        //Button
        panelLogin.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel panelBtn = new JPanel();
        panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.LINE_AXIS));
        panelBtn.add(btnConnect);
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket client = new Socket(IPServer.getText(), Integer.parseInt(PortServer.getText()));
                    String msg = "Connect " + txtUsername.getText() +" "+ txtPw.getText() + " " + IPServer.getText() +" "+ Integer.parseInt(PortClient.getText());
                    ObjectOutputStream serverOutputStream = new ObjectOutputStream(client.getOutputStream());
                    serverOutputStream.writeObject(msg);
                    serverOutputStream.flush();
                    ObjectInputStream serverInputStream = new ObjectInputStream(client.getInputStream());
                    msg = (String) serverInputStream.readObject();
                    String tmp[] = msg.split("\n");
                    String account[] = tmp[0].split(" ");
                    System.out.println(msg);
                    client.close();

                    // Login success -> Show main frame
                    if (account[0].equals("Success")) {
                        new ClientMainFrame(IPServer.getText(), Integer.parseInt(PortServer.getText()), Integer.parseInt(PortClient.getText()), account[1], tmp[1]);
                        frame.dispose();
                    } else if (account[0].equals("AlreadyLogin")) {
                        JOptionPane.showMessageDialog(null, "Login fail! The account is already logged in!", "Notification", JOptionPane.INFORMATION_MESSAGE);
                    } else JOptionPane.showMessageDialog(null, "Login fail! Please register if you don't have an account!", "Notification", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        panelLogin.add(panelBtn);
        panelLogin.add(Box.createRigidArea(new Dimension(0,10)));

    }

    /*
    Configure panel
     */
    private JPanel configPanel(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel Label = new JLabel("Configure Server");
        Label.setAlignmentX(CENTER_ALIGNMENT);
        Label.setFont(myFontTitle);
        panel.add(Label);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.LINE_AXIS));
        panel.add(panelContent);

        JPanel panelConfig = new JPanel();
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.LINE_AXIS));
        panelContent.add(panelConfig);

        // Label configure
        panelConfig.add(Box.createRigidArea(new Dimension(10,10)));
        JPanel panelLabelConfig = new JPanel();
        panelLabelConfig.setLayout(new BoxLayout(panelLabelConfig, BoxLayout.PAGE_AXIS));
        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelIP = new JLabel("IP Server");
        labelIP.setFont(myFontContent);
        panelLabelConfig.add(labelIP);

        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelPortS = new JLabel("Port Server");
        labelPortS.setFont(myFontContent);
        panelLabelConfig.add(labelPortS);
        panelConfig.add(panelLabelConfig);

        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelPortC = new JLabel("Port Client");
        labelPortC.setFont(myFontContent);
        panelLabelConfig.add(labelPortC);
        panelConfig.add(panelLabelConfig);

        panelConfig.add(Box.createRigidArea(new Dimension(10,0)));
        // Field configure
        JPanel panelTxtConfig = new JPanel();
        panelTxtConfig.setLayout(new BoxLayout(panelTxtConfig, BoxLayout.PAGE_AXIS));
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        IPServer.setFont(myFontContent);
        IPServer.setColumns(10);
        IPServer.setMaximumSize(IPServer.getPreferredSize());
        IPServer.setText("127.0.0.1");
        panelTxtConfig.add(IPServer);

        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        PortServer.setFont(myFontContent);
        PortServer.setMaximumSize(IPServer.getPreferredSize() );
        PortServer.setText("1234");
        panelTxtConfig.add(PortServer);


        Random rd = new Random();
        int portPeer = 10000 + rd.nextInt() % 1000;
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        PortClient.setFont(myFontContent);
        PortClient.setMaximumSize(IPServer.getPreferredSize() );
        PortClient.setText(String.valueOf(portPeer));
        panelTxtConfig.add(PortClient);
        panelConfig.add(panelTxtConfig);

        //Button
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel panelBtn = new JPanel();
        panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.LINE_AXIS));
        JButton btnSave = new JButton("Save");
        JButton btnEdit = new JButton("Edit");
        panelBtn.add(btnSave);
        panelBtn.add(Box.createRigidArea(new Dimension(10,0)));
        panelBtn.add(btnEdit);
        btnEdit.setEnabled(false);
        IPServer.setEditable(false);
        PortServer.setEditable(false);
        PortClient.setEditable(false);
        btnSave.setEnabled(false);
        btnEdit.setEnabled(true);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    IPServer.setEditable(false);
                    PortServer.setEditable(false);
                    PortClient.setEditable(false);
                    btnSave.setEnabled(false);
                    btnEdit.setEnabled(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    IPServer.setEditable(true);
                    PortServer.setEditable(true);
                    PortClient.setEditable(true);
                    btnSave.setEnabled(true);
                    btnEdit.setEnabled(false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        panel.add(panelBtn);
        panel.add(Box.createRigidArea(new Dimension(0,10)));

        return panel;
    }

    /**
     * Register panel
     * @return JPanel
     */
    private JPanel registerPanel(){
        JPanel panelRegister = new JPanel();
        panelRegister.setLayout(new BoxLayout(panelRegister, BoxLayout.PAGE_AXIS));
        JLabel Label = new JLabel("Register");
        Label.setAlignmentX(CENTER_ALIGNMENT);
        Label.setFont(myFontTitle);
        panelRegister.add(Label);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.LINE_AXIS));
        panelRegister.add(panelContent);

        JPanel panelConfig = new JPanel();
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.LINE_AXIS));
        panelContent.add(panelConfig);

        // Label configure
        panelConfig.add(Box.createRigidArea(new Dimension(10,10)));
        JPanel panelLabelConfig = new JPanel();
        panelLabelConfig.setLayout(new BoxLayout(panelLabelConfig, BoxLayout.PAGE_AXIS));
        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelUsername = new JLabel("Username");
        labelUsername.setFont(myFontContent);
        panelLabelConfig.add(labelUsername);

        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelPwd = new JLabel("Password");
        labelPwd.setFont(myFontContent);
        panelLabelConfig.add(labelPwd);
        panelConfig.add(panelLabelConfig);

        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel labelYourName = new JLabel("Your name");
        labelYourName.setFont(myFontContent);
        panelLabelConfig.add(labelYourName);
        panelConfig.add(panelLabelConfig);

        panelConfig.add(Box.createRigidArea(new Dimension(10,0)));
        // Field configure
        JPanel panelTxtConfig = new JPanel();
        panelTxtConfig.setLayout(new BoxLayout(panelTxtConfig, BoxLayout.PAGE_AXIS));
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JTextField txtUsername = new JTextField();
        txtUsername.setFont(myFontContent);
        txtUsername.setColumns(10);
        txtUsername.setMaximumSize(txtUsername.getPreferredSize() );
        panelTxtConfig.add(txtUsername);

        JPasswordField txtPwd = new JPasswordField();
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        txtPwd.setFont(myFontContent);
        txtPwd.setColumns(10);
        txtPwd.setMaximumSize(txtPwd.getPreferredSize() );
        panelTxtConfig.add(txtPwd);
        panelConfig.add(panelTxtConfig);

        JTextField txtName = new JTextField();
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        txtName.setFont(myFontContent);
        txtName.setColumns(10);
        txtName.setMaximumSize(txtName.getPreferredSize() );
        panelTxtConfig.add(txtName);
        panelConfig.add(panelTxtConfig);

        //Button
        panelRegister.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel panelBtn = new JPanel();
        panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.LINE_AXIS));
        JButton btnRegis = new JButton("Register");
        panelBtn.add(btnRegis);
        btnRegis.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Socket client = new Socket(IPServer.getText(), Integer.parseInt(PortServer.getText()));
                    String msg = "Regis " + txtUsername.getText() +" "+ txtPwd.getText() + " " + txtName.getText();
                    ObjectOutputStream serverOutputStream = new ObjectOutputStream(client.getOutputStream());
                    serverOutputStream.writeObject(msg);
                    serverOutputStream.flush();
                    ObjectInputStream serverInputStream = new ObjectInputStream(client.getInputStream());
                    msg = (String) serverInputStream.readObject();
                    if (msg.equals("Success")) {
                        JOptionPane.showMessageDialog(null, "Register successfully!", "Notification", JOptionPane.INFORMATION_MESSAGE);
                        txtUsername.setText("");
                        txtPwd.setText("");
                        txtName.setText("");
                    }
                    else JOptionPane.showMessageDialog(null, "Register failed! Account already exist!", "Notification", JOptionPane.INFORMATION_MESSAGE);
                    client.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        panelRegister.add(panelBtn);
        panelRegister.add(Box.createRigidArea(new Dimension(0,10)));

        return panelRegister;
    }
}
