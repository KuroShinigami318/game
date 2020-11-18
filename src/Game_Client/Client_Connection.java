/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game_Client;

import Networking.communication_transfer;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
/**
 *
 * @author Administrator
 */
//JOptionPane.showMessageDialog(null, "Lỗi kết nối tới Server", "ERROR", JOptionPane.ERROR_MESSAGE);
public class Client_Connection extends JFrame {
    private static String clientID;
    private static String nickName;
    private static String nickName_Opponent;
    private static ObjectInputStream ois;
    private static ObjectOutputStream oos;

    public static String getNickName_Opponent() {
        return nickName_Opponent;
    }

    public static void setNickName_Opponent(String nickName_Opponent) {
        Client_Connection.nickName_Opponent = nickName_Opponent;
    }

    public static ObjectInputStream getOis() {
        return ois;
    }

    public static void setOis(ObjectInputStream ois) {
        Client_Connection.ois = ois;
    }

    public static ObjectOutputStream getOos() {
        return oos;
    }

    public static void setOos(ObjectOutputStream oos) {
        Client_Connection.oos = oos;
    }

    public static String getNickName() {
        return nickName;
    }

    public static void setNickName(String nickName) {
        Client_Connection.nickName = nickName;
    }
    private static String clientID_Opponent;
    private static Socket client;

    public static String getClientID() {
        return clientID;
    }

    public static void setClientID(String clientID) {
        Client_Connection.clientID = clientID;
    }

    public static String getClientID_Opponent() {
        return clientID_Opponent;
    }

    public static void setClientID_Opponent(String clientID_Opponent) {
        Client_Connection.clientID_Opponent = clientID_Opponent;
    }

    public static Socket getClient() {
        return client;
    }

    public static void setClient(Socket client) {
        Client_Connection.client = client;
    }
    private InetAddress IPServer;
    private int port = 9099;
    public Client_Connection(){
        clientID = UUID.randomUUID().toString();
        connectInit();
    }
    
    private void connectInit(){
        setSize(400, 100);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        JLabel lblIpserver = new JLabel("IP Server:");
        JTextField ipserver = new JTextField("192.168.1.2");
        JLabel lblPort = new JLabel("Port:");
        JTextField txtport = new JTextField(Integer.toString(port));
        JLabel lblNickName = new JLabel("Your Nick Name:");
        JTextField nickname = new JTextField();
        
        JButton connect = new JButton("Connect");
        getRootPane().setDefaultButton(connect);
        connect.addActionListener(new java.awt.event.ActionListener(){
            @Override 
            public void actionPerformed(java.awt.event.ActionEvent e){
                if(nickname.getText().equals("")) {
                    nickname.requestFocus();
                    JOptionPane.showMessageDialog(null, "Nick Name Needed", "Request Information", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(ipserver.getText().equals("")){
                    ipserver.requestFocus();
                    JOptionPane.showMessageDialog(null, "IP Server Needed", "Request Information", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(txtport.getText().equals("")){
                    txtport.requestFocus();
                    JOptionPane.showMessageDialog(null, "Port Needed", "Request Information", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    
                    IPServer = InetAddress.getByName(ipserver.getText());                   
                
                    port = Integer.parseInt(txtport.getText());
                    boolean check = false;
                    nickName = nickname.getText();
                    client = new Socket(IPServer,port);
                    oos = new ObjectOutputStream(client.getOutputStream());
                    ois = new ObjectInputStream(client.getInputStream());
                    
                    while(!check){                                                        
                    
                    oos.writeObject(new communication_transfer(nickName, clientID, "server", "addToServer", "request to be added"));    
                    
                        communication_transfer pack_trans = (communication_transfer) ois.readObject();
                        if(pack_trans.getTo_ID()==null&&pack_trans.getMessage().equalsIgnoreCase("request_to_change_ID")){
                            clientID = UUID.randomUUID().toString();
                        }
                        else {
                        if(pack_trans.getMessage().equalsIgnoreCase("connected")&&pack_trans.getPurpose().equalsIgnoreCase("connected")) {
                            Client_Connection.setClientID_Opponent(pack_trans.getFrom_ID());
                            Client_Connection.setNickName_Opponent(pack_trans.getFrom_Name());
                            Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), "server", "get number", "request number"));
                        }
                            check = true;
                            new Client_Game_Launcher().setVisible(true);                            
                            dispose();
                        }
                        
                    }
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(null, "ERROR TO CONNECT TO SERVER! MAYBE YOU ENTERED INVALID IP ADDRESS", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "SERVER HAS BEEN CLOSED!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "PLEASE ENTERED VALID PORT!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        add(lblIpserver,new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(ipserver,new GridBagConstraints(1, 0, 1, 1, 1.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(lblPort,new GridBagConstraints(0, 1, 1, 1, 0.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(txtport,new GridBagConstraints(1, 1, 1, 1, 1.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(lblNickName,new GridBagConstraints(0, 2, 1, 1, 0.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(nickname,new GridBagConstraints(1, 2, 1, 1, 1.5, 0.5, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        add(connect,new GridBagConstraints(0, 3, 2, 1, 0.5, 0.5, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        setVisible(true);
        nickname.requestFocusInWindow();
    }
}
