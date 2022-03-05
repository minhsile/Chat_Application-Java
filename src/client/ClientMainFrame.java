package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * client
 * Created by Minh Sĩ Lê
 * Date 12/14/2021 - 2:29 PM
 * Description: Main frame
 */
public class ClientMainFrame extends JFrame {
    private static JList<String> listUser;
    static DefaultListModel<String> model = new DefaultListModel<>();
    private static Client client;
    ClientMainFrame frame = this;
    Font myFontTitle = new Font("Tahoma", Font.PLAIN, 28);
    Font myFontContent = new Font("Serif", Font.BOLD, 18);

    /**
     * Update user list
     * @param msg user
     */
    public static void updateFriendMainFrame(String msg) {
        model.addElement(msg);
    }

    /**
     * Clear user list
     */
    public static void resetList() {
        model.clear();
    }

    /**
     * Constructor create frame
     * @param ipS ip server
     * @param portS port server
     * @param portC port client
     * @param name name of user
     * @param dataUser online users list
     * @throws Exception Exception
     */
    public ClientMainFrame(String ipS, int portS, int portC, String name, String dataUser) throws Exception {
        JFrame.setDefaultLookAndFeelDecorated(true);
        setTitle("Chat client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 300);
        setContentPane(ClientJPanel(ipS, portS, portC, name, dataUser));
        setVisible(true);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                try {
                    frame.dispose();
                    client.exit();
                    System.gc();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * Create frame
     */
    public JPanel ClientJPanel(String ipS, int portS, int portC, String name, String dataUser) throws Exception {
        client = new Client(ipS, portS, portC, name, dataUser);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        JLabel Label = new JLabel("Welcome " + name);
        Label.setAlignmentX(CENTER_ALIGNMENT);
        Label.setFont(myFontTitle);
        mainPanel.add(Label);

        JPanel panelContent = new JPanel();
        panelContent.setLayout(new BoxLayout(panelContent, BoxLayout.LINE_AXIS));
        mainPanel.add(panelContent);

        //Show user list
        listUser = new JList<>(model);
        listUser.setBorder(new TitledBorder(null, "Online User", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        listUser.setBackground(Color.WHITE);
        listUser.setForeground(Color.RED);
        listUser.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listUser.setBounds(10, 20, 577, 332);
        JScrollPane listPane = new JScrollPane(listUser);
        panelContent.add(listPane);

        listUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                String nameGuest = listUser.getModel().getElementAt(listUser.locationToIndex(arg0.getPoint()));
                connectChat(nameGuest);
            }
        });
        return mainPanel;
    }

    /**
     * Chat with friend
     * @param nameGuest friend's name
     */
    private void connectChat(String nameGuest) {
        int check = JOptionPane.showConfirmDialog(this, "Do you want connect with " + nameGuest + " ?", "Connect", JOptionPane.YES_NO_OPTION);
        if (check == 0) {
            System.out.println(nameGuest);
            int size = Client.clientArray.size();
            for (int i = 0; i < size; i++) {
                if (nameGuest.equals(Client.clientArray.get(i).getName())) {
                    try {
                        System.out.println(Client.clientArray.get(i).getPort());
                        client.newChat(Client.clientArray.get(i).getHost(), Client.clientArray.get(i).getPort(), nameGuest);
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "User is not found");
        }
    }
}
