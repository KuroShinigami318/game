/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game_Client;

import Networking.communication_transfer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class game_play extends MouseAdapter{
    private Component square;
    private final Color backGround;
    
    public game_play(Component square){
        this.square = square;
        this.backGround = square.getBackground();
    }
    
    @Override
    public void mousePressed(MouseEvent e){
        JLabel square = (JLabel) this.square;
        if(Client_Game_Launcher.your_turn&&square.getName().split(",")[2].equals("null")) {
            try {                
                Client_Game_Launcher.your_turn = false;
                square.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                String[] arr = square.getName().split(",");
                arr[2] = Client_Game_Launcher.player;
                square.setName(String.join(",", arr));
                square.setText(Client_Game_Launcher.player);
                Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), square.getName(), "gameplay"));                                
                //square.setBackground(backGround);
                if(square.getText().equalsIgnoreCase("o")) square.setForeground(Color.green);
                else square.setForeground(Color.red);
                Client_Game_Launcher.setBackground();
                square.setBackground(Color.darkGray);
                checkGamePlay();                
            } catch (IOException ex) {
                Logger.getLogger(game_play.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Error to connect to Server!");
            }
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e){
        JLabel square = (JLabel) this.square;
        if(Client_Game_Launcher.your_turn&&square.getName().split(",")[2].equals("null")) {
            square.setCursor(new Cursor(Cursor.HAND_CURSOR));
            square.setBackground(Color.WHITE);
        } else square.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    @Override
    public void mouseExited(MouseEvent e){
        JLabel square = (JLabel) this.square;
        if(Client_Game_Launcher.your_turn&&square.getName().split(",")[2].equals("null")) square.setBackground(backGround);
    }    
    
    private void checkGamePlay(){
        int d = 0;
        int position = 0;
        String last = null;
        for(int i=0;i<Client_Game_Launcher.rows&&d<5;i++){
            for(int j=0;j<Client_Game_Launcher.columns&&d<5;j++){                
              if(Client_Game_Launcher.squares[i][j].getName().split(",")[2].equalsIgnoreCase(Client_Game_Launcher.player)) {
                  d++;
              } else d=0;
              if(d==5) {
                  position = 1;
                  last = i+","+j;
                  break;
              }             
            }
            if(d<5) d=0;
            else break;
            for(int j=i,k=0;j<Client_Game_Launcher.rows&&k<Client_Game_Launcher.columns;j++,k++){
                if(Client_Game_Launcher.squares[j][k].getName().split(",")[2].equalsIgnoreCase(Client_Game_Launcher.player)) {
                  d++;
                } else d=0;
                if(d==5) {
                    position = 3;
                    last = j+","+k;
                    break;
                }
            }
            if(d<5) d=0;
            else break;
            for(int j=i,k=Client_Game_Launcher.columns-1;k>=0&&j<Client_Game_Launcher.rows;j++,k--){
                if(Client_Game_Launcher.squares[j][k].getName().split(",")[2].equalsIgnoreCase(Client_Game_Launcher.player)) {
                  d++;
                } else d=0;
                if(d==5) {
                    position = 4;
                    last = j+","+k;
                    break;
                }
            }
            if(d==5) break;
            else d=0;
        }
        for(int j=0;j<Client_Game_Launcher.columns&&d<5;j++){
            for(int i=0;i<Client_Game_Launcher.rows;i++){
              if(Client_Game_Launcher.squares[i][j].getName().split(",")[2].equalsIgnoreCase(Client_Game_Launcher.player)) {
                  d++;
              } else d=0;
              if(d==5) {
                  position = 2;
                  last = i+","+j;
                  break;
              }
            }
            if(d<5) d=0;
            else break;
            for(int i=j,k=0;k<Client_Game_Launcher.rows&&i<Client_Game_Launcher.columns;i++,k++){
                if(Client_Game_Launcher.squares[k][i].getName().split(",")[2].equalsIgnoreCase(Client_Game_Launcher.player)) {
                  d++;
                } else d=0;
                if(d==5) {
                    position = 3;
                    last = k+","+i;
                    break;
                }
            }
            if(d<5) d=0;
            else break;
            for(int i=j,k=0;k<Client_Game_Launcher.rows&&i>=0;i--,k++){
                if(Client_Game_Launcher.squares[k][i].getName().split(",")[2].equalsIgnoreCase(Client_Game_Launcher.player)) {
                  d++;
                } else d=0;
                if(d==5) {
                    position = 4;
                    last = k+","+i;
                    break;
                }
            }
            if(d==5) break;
            else d=0;
        }
        if(d==5) {
            try {
                Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), position+"/"+last, "gameover"));
                Client_Game_Launcher.lblgameover.setText("YOU WIN");
                Client_Game_Launcher.lblgameover.setName("YOU WIN");
                Client_Game_Launcher.your_turn = false;
                endGame(position, last);
                JOptionPane.showMessageDialog(null, "YOU WIN");
                //Client_Game_Launcher.cardGame.show(Client_Game_Launcher.maingame, "game over");
            } catch (IOException ex) {
                Logger.getLogger(game_play.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void endGame(int position, String last){
        switch(position){
                    case 1:
                        int i1=Integer.parseInt(last.split(",")[0]);
                        int j1=Integer.parseInt(last.split(",")[1])-4; 
                        for(int dem=0;dem<5;j1++,dem++) Client_Game_Launcher.squares[i1][j1].setBackground(Color.darkGray);
                        break;
                    case 2:
                        int i2=Integer.parseInt(last.split(",")[0])-4;
                        int j2=Integer.parseInt(last.split(",")[1]);
                        for(int dem=0;dem<5;i2++,dem++) Client_Game_Launcher.squares[i2][j2].setBackground(Color.darkGray);
                        break;
                    case 3:
                        int i3=Integer.parseInt(last.split(",")[0])-4;
                        int j3=Integer.parseInt(last.split(",")[1])-4;
                        for(int dem=0;dem<5;i3++,j3++,dem++) Client_Game_Launcher.squares[i3][j3].setBackground(Color.darkGray);
                        break;
                    case 4:
                        int i4=Integer.parseInt(last.split(",")[0])-4;
                        int j4=Integer.parseInt(last.split(",")[1])+4;
                        for(int dem=0;dem<5;i4++,j4--,dem++) Client_Game_Launcher.squares[i4][j4].setBackground(Color.darkGray);
                        break;
                    default: break;
                }
    }
}
