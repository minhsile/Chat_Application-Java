package server;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * server
 * Created by Minh Sĩ Lê
 * Date 12/14/2021 - 12:39 PM
 * Description: Server UI
 */
public class ServerUI extends JPanel {
    private final JTextField txtIP = new JTextField();
    private final JTextField txtPort = new JTextField();
    private static final JTextArea txtMessage = new JTextArea();
    private final JButton btnStart = new JButton("Start");
    private final JButton btnStop = new JButton("Stop");
    private int port;
    static Server server;

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
        JFrame frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100,500,300);
        frame.setResizable(false);

        ServerUI newContentPane = new ServerUI();
        frame.setContentPane(newContentPane);

        frame.setVisible(true);
    }

    /**
     * Update notification
     * @param msg
     */
    public static void updateNoti(String msg){
        txtMessage.append(msg + "\n");
    }

    /**
     * Create frame
     */
    public ServerUI(){
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel Label = new JLabel("Server Config");
        Label.setAlignmentX(CENTER_ALIGNMENT);
        Label.setFont(myFontTitle);
        add(Label);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.LINE_AXIS));
        add(panelContent);

        JPanel panelConfig = new JPanel();
        panelConfig.setLayout(new BoxLayout(panelConfig, BoxLayout.LINE_AXIS));
        panelContent.add(panelConfig);

        // Label configure
        panelConfig.add(Box.createRigidArea(new Dimension(10,10)));
        JPanel panelLabelConfig = new JPanel();
        panelLabelConfig.setLayout(new BoxLayout(panelLabelConfig, BoxLayout.PAGE_AXIS));
        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel ipLabel = new JLabel("IP Address");
        ipLabel.setFont(myFontContent);
        panelLabelConfig.add(ipLabel);
        panelLabelConfig.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel portLabel = new JLabel("Port");
        portLabel.setFont(myFontContent);
        panelLabelConfig.add(portLabel);
        panelConfig.add(panelLabelConfig);

        // Field configure
        JPanel panelTxtConfig = new JPanel();
        panelTxtConfig.setLayout(new BoxLayout(panelTxtConfig, BoxLayout.PAGE_AXIS));
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        txtIP.setText("127.0.0.1");
        txtIP.setFont(myFontContent);
        txtIP.setColumns(10);
        txtIP.setMaximumSize(txtIP.getPreferredSize());
        panelTxtConfig.add(txtIP);
        panelTxtConfig.add(Box.createRigidArea(new Dimension(0,10)));
        txtPort.setFont(myFontContent);
        txtPort.setMaximumSize(txtIP.getPreferredSize());
        txtPort.setText("1234");
        panelTxtConfig.add(txtPort);
        panelConfig.add(panelTxtConfig);

        //Button
        add(Box.createRigidArea(new Dimension(0,10)));
        JPanel panelBtn = new JPanel();
        panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.LINE_AXIS));
        panelBtn.add(btnStart);
        panelBtn.add(Box.createRigidArea(new Dimension(20,0)));
        panelBtn.add(btnStop);
        add(panelBtn);
        add(Box.createRigidArea(new Dimension(0,10)));

        //Show user list
        JPanel panelUser = new JPanel();
        panelUser.setBorder(new TitledBorder(null, "Notification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        txtMessage.setBackground(Color.BLACK);
        txtMessage.setForeground(Color.WHITE);
        txtMessage.setFont(myFontContent);
        panelUser.setLayout(new GridLayout(0, 1, 0, 0));
        JScrollPane scrollPane = new JScrollPane(txtMessage);
        panelUser.add(scrollPane);
        panelContent.add(panelUser);

        //Action
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               port = Integer.parseInt(txtPort.getText());
                try {
                    server = new Server(port);
                    btnStop.setEnabled(true);
                    btnStart.setEnabled(false);
                    txtIP.setEditable(false);
                    txtPort.setEditable(false);
                    txtMessage.append("Server started...\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    server.stop();
                    btnStop.setEnabled(false);
                    btnStart.setEnabled(true);
                    txtMessage.append("Stop server\n");
                    txtIP.setEditable(true);
                    txtPort.setEditable(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
