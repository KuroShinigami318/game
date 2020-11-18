/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game_Client;

import Networking.communication_transfer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Administrator
 */
/*new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask(){
@Override
public void run(){
System.out.println(Client_Connection.getNickName_Opponent());
}
}, 0, 2000);*/
public class Client_Game_Launcher extends JFrame{
    public static String player;
    public static String player_Opponent;
    public static final int rows = 22;
    public static final int columns = 15;
    public static boolean your_turn = false;
    public static JLabel[][] squares = new JLabel[rows][columns];
    public static JLabel lblgameover;
    public static CardLayout cardGame;
    public static JPanel maingame;
    private JPanel game;
    private Chat_Box chatBox;
    private CardLayout card;
    JPanel body;
    
    public Client_Game_Launcher(){
        
        init();
    }
    
    private void init(){
        setUndecorated(true);
        setSize(1000, 700);
        //setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        titleCustom.BorderPanel title = new titleCustom.BorderPanel(this);
        JPanel border = title.getCustomPanel();
        
        maingame = new JPanel(new CardLayout());
        // jpanel for game 
        game = new JPanel(new GridLayout(rows, columns));
        createNewGame(game);
        
        //jpanel for announce game over
        JPanel gameover = new JPanel(new BorderLayout());
        lblgameover = new JLabel("",JLabel.CENTER);
        gameover.add(lblgameover,BorderLayout.CENTER);
        
        maingame.add(game,"maingame");
        maingame.add(lblgameover,"game over");
        //show maingame
        cardGame = (CardLayout) maingame.getLayout();
        cardGame.show(maingame,"maingame");
        
        chatBox = new Chat_Box(this,300,getSize().height-200);
        JPanel menu = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton newGame = new JButton("New Game");
        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                requestToMakeNewGame();
            }
        });
        menu.add(newGame);
        
        JPanel main = new JPanel(new GridBagLayout());
        main.add(menu,new GridBagConstraints(0, 0, 2, 1, 1, 0.01, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        main.add(maingame,new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(-5,0,0,0), 0, 0));
        main.add(chatBox,new GridBagConstraints(1, 1, 1, 1, 0.5, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
        
        // announce for wating for opponent
        JPanel alert = new JPanel(new BorderLayout());
        JLabel announce = new JLabel("Searching for opponent!",JLabel.CENTER);
        alert.add(announce, BorderLayout.CENTER);
        
        body = new JPanel(new CardLayout());
        body.add(alert,"announcement");
        body.add(main,"connected");
        card = (CardLayout) body.getLayout();
        if(Client_Connection.getClientID_Opponent()!=null){            
            card.show(body, "connected");
        } else {
            card.show(body, "announcement");
        }
        
        //add to jframe
        add(border,new GridBagConstraints(0, 0, 1, 1, 1, 0.01, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,0,2), 0, 0));
        add(body,new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0,2,2,2), 0, 0));
        
        new Thread(new Runnable() {
            @Override
            public void run() {                
                    thread_run();                              
            }
        }).start();
    }
    
    private void thread_run(){
        while(true) {
                    try {    
                    communication_transfer pack_mess = (communication_transfer) Client_Connection.getOis().readObject();
                    if(pack_mess.getMessage().equalsIgnoreCase("connected")&&pack_mess.getPurpose().equalsIgnoreCase("connected")) {
                        Client_Connection.setClientID_Opponent(pack_mess.getFrom_ID());
                        Client_Connection.setNickName_Opponent(pack_mess.getFrom_Name());
                        Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), "server", "get number", "request number"));
                        chatBox.clearedChat();
                        card.show(body, "connected");
                        makeNewGame(game);
                    } else if(pack_mess.getMessage().equalsIgnoreCase("waiting for opponent")&&pack_mess.getPurpose()==null&&pack_mess.getFrom_ID().equalsIgnoreCase("server")){
                        card.show(body, "announcement");
                        Client_Connection.setClientID_Opponent(null);
                        Client_Connection.setNickName_Opponent(null);
                        chatBox.clearedChat();
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase("server")&&pack_mess.getPurpose().equalsIgnoreCase("your number is")){
                        if(Integer.parseInt(pack_mess.getMessage())%2==0){
                            player = "X";
                            player_Opponent = "O";
                            your_turn = true;                            
                        } else {
                            player = "O";
                            player_Opponent = "X";
                        }
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("chat")){
                        chatBox.message(pack_mess.getMessage(), false, true, null);
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("file")){
                        chatBox.message(pack_mess.getMessage(), true, true, (byte[]) pack_mess.getObject());
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("gameplay")){
                        int i = Integer.parseInt(pack_mess.getMessage().split(",")[0]);
                        int j= Integer.parseInt(pack_mess.getMessage().split(",")[1]);
                        squares[i][j].setName(pack_mess.getMessage());
                        squares[i][j].setText(player_Opponent);
                        if(squares[i][j].getText().equalsIgnoreCase("o")) squares[i][j].setForeground(Color.green);
                        else squares[i][j].setForeground(Color.red);
                        setBackground();
                        squares[i][j].setBackground(Color.darkGray);
                        your_turn = true;
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("gameover")){                        
                            lblgameover.setText("YOU LOSE");
                            lblgameover.setName("YOU LOSE");
                            your_turn = false;
                            int position = Integer.parseInt(pack_mess.getMessage().split("/")[0]);
                            String last = pack_mess.getMessage().split("/")[1];
                            game_play.endGame(position, last);
                            JOptionPane.showMessageDialog(null, "YOU LOSE");
                            //cardGame.show(maingame, "game over");
                        
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("requestNewGame")){
                        if(pack_mess.getMessage().equalsIgnoreCase("request to make new game")) {
                            if(JOptionPane.showConfirmDialog(null, "Opponent request to make a new game!")==JOptionPane.YES_OPTION) {
                                Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), "new game accepted", "accepted"));
                                makeNewGame(game);
                                if(lblgameover.getName()!=null){
                                    if(lblgameover.getName().equalsIgnoreCase("you win")) {
                                        player = "O";
                                        player_Opponent = "X";
                                        your_turn = false;
                                    } else {
                                        player = "X";
                                        player_Opponent = "O";
                                        your_turn = true;
                                    }
                                } else {
                                if(player.equalsIgnoreCase("x")) your_turn = true;
                                else your_turn = false;
                                }
                            } else {
                                Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), "new game denied", "denied"));
                            }
                        }
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("accepted")){
                        if(pack_mess.getMessage().equalsIgnoreCase("new game accepted")) {
                            JOptionPane.showMessageDialog(null, "Opponent has accepted to make a new game!");
                            makeNewGame(game);
                            if(lblgameover.getName()!=null){
                                    if(lblgameover.getName().equalsIgnoreCase("you win")) {
                                        player = "O";
                                        player_Opponent = "X";
                                        your_turn = false;
                                    } else {
                                        player = "X";
                                        player_Opponent = "O";
                                        your_turn = true;
                                    }
                                } else {
                                if(player.equalsIgnoreCase("x")) your_turn = true;
                                else your_turn = false;
                                }
                        }
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase(Client_Connection.getClientID_Opponent())&&pack_mess.getPurpose().equalsIgnoreCase("denied")){
                        if(pack_mess.getMessage().equalsIgnoreCase("new game denied")) {
                            JOptionPane.showMessageDialog(null, "Opponent has denied to make a new game!");
                        }
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase("server")&&pack_mess.getPurpose().equalsIgnoreCase("download file")){
                        FileDialog fd = new FileDialog(new JFrame(), "SAVE FILE", FileDialog.SAVE);
                        fd.setFile(pack_mess.getMessage());
                        fd.setVisible(true);
                        String url = fd.getDirectory() + fd.getFile();
                        if(url.equals("nullnull")) continue;
                        else {
                            byte[] byteFile = (byte[]) pack_mess.getObject();
                            BufferedOutputStream bos = null;
                            bos = new BufferedOutputStream(new FileOutputStream(url), 65536);
                            bos.write(byteFile, 0, byteFile.length);
                            if(bos!=null) bos.close();
                            JOptionPane.showMessageDialog(null, "Download "+fd.getFile()+" successful! ");
                        }
                    } else if(pack_mess.getFrom_ID().equalsIgnoreCase("server")&&pack_mess.getPurpose().equalsIgnoreCase("upload successful")){
                        System.out.println("upload successful");
                        chatBox.message(pack_mess.getMessage(), true, false, (byte[]) pack_mess.getObject());
                    }
                     Thread.sleep(200);                                       
                    } catch (IOException ex) {
                        //thread_run();
                    } catch (ClassNotFoundException ex) {
                        //thread_run();
                    } catch (ClassCastException ex){
                        //thread_run();
                    } catch(NullPointerException ex){
                        //thread_run();
                    } catch(OutOfMemoryError ex){
                        
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client_Game_Launcher.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
    }
    
    
    public static void setBackground(){
        for(int i=0;i<rows;i++)
            for(int j=0;j<columns;j++){
                if(!squares[i][j].getName().split(",")[2].equals("null")){
                    squares[i][j].setBackground(Color.white);
                }
            }
    }
    
    private void makeNewGame(JPanel game){
        game.removeAll();
        createNewGame(game);
        cardGame.show(maingame, "maingame");
        game.revalidate();
    }
    
    private void requestToMakeNewGame(){
        try {
            JOptionPane.showMessageDialog(null, "waiting for opponent accepts a new game!");
            Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), "request to make new game", "requestNewGame"));
        } catch (IOException ex) {
            Logger.getLogger(Client_Game_Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createNewGame(JPanel game){
        for(int i=0;i<squares.length;i++){
            for(int j=0;j<squares[i].length;j++){
                squares[i][j] = new JLabel(){
                    @Override
                    public Dimension getPreferredSize(){                        
                        return new Dimension(10,10);
                    }
                    @Override
                    public Dimension getMinimumSize(){
                        return getPreferredSize();
                    }
                    @Override
                    public Dimension getMaximumSize(){
                        return getPreferredSize();
                    }
                };
                squares[i][j].setName(i+","+j+",null");                
                squares[i][j].setHorizontalAlignment(JLabel.CENTER);
                squares[i][j].setOpaque(true);
                squares[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
                squares[i][j].setBackground(Color.lightGray);
                squares[i][j].addMouseListener(new game_play(squares[i][j]));
                game.add(squares[i][j]);
            }
        }
    }
    
    public static void main(String[] args){
        new Client_Game_Launcher().setVisible(true);
    }
}
