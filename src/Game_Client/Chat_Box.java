/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game_Client;

import Networking.communication_transfer;
import java.awt.AlphaComposite;
import javax.swing.text.Style;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.border.Border;
import javax.swing.text.StyledDocument;
/**
 *
 * @author Administrator
 */
public class Chat_Box extends JPanel {
    private JPanel chatArea;
    private int heightArea = 5;
    private JTextField text;
    private final JFrame frame;
    private final int width;
    private final int height;
    private JLabel lblname_opponent;
    private int __max = 52428800;
    
    
    public int getHeightArea() {
        return heightArea;
    }

    public void setHeightArea(int heightArea) {
        this.heightArea = heightArea;
    }    

    public JPanel getChatArea() {
        return chatArea;
    }

    public void setChatArea(JPanel chatArea) {
        this.chatArea = chatArea;
    }
    
    public Chat_Box(JFrame frame,int width, int height){
        this.frame = frame;
        this.width = width;
        this.height = height;
        init();        
    }
    
    public class uploadFile {
        private int __max;
        private ObjectOutputStream oos;
        private String url;
        private String fileName;
        
        @Override
        protected void finalize(){
            System.out.println("garbage collected");
        }
        
        public uploadFile(ObjectOutputStream oos, String url, String fileName, int __max){
            this.oos = oos;
            this.url = url;
            this.fileName = fileName;
            this.__max = __max;
            BufferedInputStream bis = null;
                    try {
                        File fileUpload = new File(url);
                        if((int) fileUpload.length()<=__max) {
                        byte[] partFile = new byte[(int) fileUpload.length()];
                        bis = new BufferedInputStream(new FileInputStream(url),65536);
                        bis.read(partFile, 0, partFile.length); 
                        oos.writeObject(new communication_transfer(Client_Connection.getNickName(), partFile, Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), fileName, "file"));
                        } else {
                            ArrayList<byte[]> partFiles = new ArrayList();
                            bis = new BufferedInputStream(new FileInputStream(url),65536);
                            int max = (int) fileUpload.length()/__max;
                            int i = 0;
                            int binaryRead = 0;
                            byte[] partFile;
                            do {
                                partFile = new byte[__max];
                                binaryRead = bis.read(partFile, 0, __max);
                                partFiles.add(partFile);
                                i++;
                                partFile = null;
                                System.gc();
                            } while(i<max);
                            if(binaryRead!=-1) {
                                partFile = new byte[(int) fileUpload.length()%__max];
                                bis.read(partFile, 0, (int) fileUpload.length()%__max);
                                partFiles.add(partFile);
                                partFile = null;
                                System.gc();
                            }                           
                            oos.writeObject(new communication_transfer(Client_Connection.getNickName(), partFiles, Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), fileName, "file"));
                        }
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
                            } catch(OutOfMemoryError ex) {
                                JOptionPane.showMessageDialog(null, "CANT'T UPLOAD DUE TO FILE IS TOO LARGE!");
                                ex.printStackTrace();
                            } finally {
                                try {
                                    if(bis!=null) bis.close();
                                } catch (IOException ex) {
                                    Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
                                }                        
                    }               
        }
    }
    
    private void init(){
        chatArea = new JPanel(null);
        chatArea.setPreferredSize(new Dimension(width+50, height));
        
        setLayout(new BorderLayout());
        
        JPanel chatText = new JPanel(new BorderLayout());
        text = new JTextField();
        JButton send = new JButton("send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                sendMessage();
            }
        });
        text.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode()==KeyEvent.VK_ENTER) send.doClick();
            }
        });
        JButton file = new JButton(new ImageIcon(new ImageIcon(this.getClass().getResource("/icons/icons8_opened_folder_20px.png")).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        file.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                FileDialog fd = new FileDialog(new JFrame(), "Share File", FileDialog.LOAD);
                fd.setMultipleMode(false);
                fd.setVisible(true);
                String url = fd.getDirectory() + fd.getFile();
                if(url.equals("nullnull")){
                    return;
                } else {
                     uploadFile up = new uploadFile(Client_Connection.getOos(), url, fd.getFile(), __max);
                     up = null;
                     System.gc();
                  }
               }     
        });
        
        chatText.add(send,BorderLayout.EAST);
        chatText.add(text,BorderLayout.CENTER);
        chatText.add(file,BorderLayout.WEST);
        
        JScrollPane scroll = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setPreferredSize(new Dimension(chatArea.getPreferredSize().width,chatArea.getPreferredSize().height+50));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel lblname = new JLabel("You: "+Client_Connection.getNickName());
        lblname_opponent = new JLabel("Your Opponent: "+Client_Connection.getNickName_Opponent());
        namePanel.add(lblname,BorderLayout.WEST);
        namePanel.add(lblname_opponent,BorderLayout.EAST);
        
        add(namePanel,BorderLayout.NORTH);
        add(scroll,BorderLayout.CENTER);
        add(chatText,BorderLayout.SOUTH);
        
        new java.util.Timer().scheduleAtFixedRate(new java.util.TimerTask(){
            @Override
            public void run(){
                    if(Client_Connection.getNickName_Opponent()!=null){
                        lblname_opponent.setText("Your Opponent: "+Client_Connection.getNickName_Opponent());
                    } else if(Client_Connection.getNickName_Opponent()==null) {                        
                        clearedChat();                       
                    }
                    if(chatArea.getSize().width>width) {
                    for(int i=0; i<chatArea.getComponentCount(); i++){
                        if(chatArea.getComponent(i).getX()!=2) {                            
                            customTextPane chatting = (customTextPane) chatArea.getComponent(i);
                            int countLines = countLines(chatting);           
                            if(countLines>1) {                                   
                                chatting.setPreferredSize(new Dimension(chatArea.getSize().width-50, chatting.getPreferredSize().height));               
                            }                            
                            chatting.setBounds(chatArea.getSize().width-chatting.getPreferredSize().width-2, chatting.getY(), chatting.getPreferredSize().width, chatting.getPreferredSize().height);
                        } else {
                            customTextPane chatting = (customTextPane) chatArea.getComponent(i);
                            int countLines = countLines(chatting);           
                            if(countLines>1) {                                   
                                chatting.setPreferredSize(new Dimension(chatArea.getSize().width-50, chatting.getPreferredSize().height));               
                            }                           
                            chatting.setBounds(2, chatting.getY(), chatting.getPreferredSize().width, chatting.getPreferredSize().height);
                        }
                    }                    
                    }
                }                
            
        }, 0, 100);
    }
    
    private void sendMessage(){
        try {
            if(text.getText().equals("")) {
                text.requestFocus();
                return;
            }
            Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), Client_Connection.getClientID_Opponent(), text.getText(), "chat"));
            if(text.getText().matches("^\\\\f:<<([a-z]|[A-Z]|[0-9]|\\.|_|-|(|)| )+>>$")) message(text.getText().replaceAll("(\\\\f:<<)|(>>)", ""), true, false, null);
            else message(text.getText(), false, false, null);
            text.setText("");
        } catch (IOException ex) {
            Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Error to connect to Server!");
        }
    }
     
    
    public void message(String message, final boolean isFile, final boolean isOpponent, byte[] image){
        try {
            customTextPane chatting;
            if(isOpponent) {
                chatting = new customTextPane(true, 20, Color.blue);
            }
            else {
                chatting = new customTextPane(true, 20, Color.BLACK);
            }
            chatting.setEditable(false);
            //Set background and foreground here (Style for chat)
            chatting.setBackground(chatArea.getBackground());
            //Style            
            StyledDocument doc = chatting.getStyledDocument();
            Style style = chatting.addStyle("Styled", null);
            if(isFile) {
                StyleConstants.setUnderline(style, true);
                chatting.setCursor(new Cursor(Cursor.HAND_CURSOR));
                String[] isImage = message.split("\\.");
                if(image!=null&&isImage[isImage.length-1].matches("jpg|jpeg|png")) {
                ByteArrayInputStream bais = new ByteArrayInputStream(image);
                BufferedImage bImage = ImageIO.read(bais);
                StyleConstants.setComponent(style, new JLabel(new ImageIcon(bImage.getScaledInstance(150, 180, Image.SCALE_SMOOTH))));
                chatting.setPreferredSize(new Dimension(170, 200));                
                }
                chatting.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e){
                        try {
                            Client_Connection.getOos().writeObject(new communication_transfer(Client_Connection.getNickName(), Client_Connection.getClientID(), "server", message, "request download"));
                        } catch (IOException ex) {
                            Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            }
            StyleConstants.setForeground(style, Color.black);
            doc.insertString(doc.getLength(), message, style);
            SimpleAttributeSet attrib = new SimpleAttributeSet();
            StyleConstants.setAlignment(attrib, StyleConstants.ALIGN_JUSTIFIED);           
            chatting.setParagraphAttributes(attrib, true);            
            //
          
            chatting.setSize(chatArea.getPreferredSize().width-50, chatting.getPreferredSize().height);
            int countLines = countLines(chatting);
            if(countLines>1) {
                chatting.setPreferredSize(new Dimension(chatArea.getPreferredSize().width-50, chatting.getPreferredSize().height));               
            }
            if(isOpponent) chatting.setBounds(chatArea.getSize().width-chatting.getPreferredSize().width-2, heightArea, chatting.getPreferredSize().width, chatting.getPreferredSize().height);
            else chatting.setBounds(2, heightArea, chatting.getPreferredSize().width, chatting.getPreferredSize().height);
            chatArea.add(chatting);
            int height = chatting.getPreferredSize().height + 10;            
            heightArea+=height;            
            checkHeightArea();
            this.repaint();
            this.revalidate();
            System.out.println(chatting.getText());
        } catch (BadLocationException ex) {
            Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Chat_Box.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void clearedChat(){
        chatArea.removeAll();
        chatArea.repaint();
        lblname_opponent.setText("");
        heightArea = 5;
    }
        
    
    public void checkHeightArea(){
        if(heightArea>chatArea.getSize().height) {
            chatArea.setPreferredSize(new Dimension(chatArea.getPreferredSize().width, heightArea));            
        }
    }
    
    public int countLines(JTextPane textPane){
        int totalCharacters = textPane.getText().length();
        int lineCount = (totalCharacters == 0) ? 1 : 0;
        try {
            int offset = totalCharacters;
            while(offset>0){
                offset = Utilities.getRowStart(textPane, offset) -1;
                lineCount++;
            }
        } catch(BadLocationException e){
            e.printStackTrace();
        } catch(NullPointerException e) {
            //return -1;
        }
        return lineCount;
    }
    
    public int countLines(JTextArea textArea){
        AttributedString text = new AttributedString(textArea.getText());
        FontRenderContext frc = textArea.getFontMetrics(textArea.getFont()).getFontRenderContext();
        AttributedCharacterIterator charIt = text.getIterator();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(charIt, frc);
        float formatWidth = (float) textArea.getPreferredSize().width;
        lineMeasurer.setPosition(charIt.getBeginIndex());        
        int noLine = 0;
        while(lineMeasurer.getPosition()<charIt.getEndIndex()){
            lineMeasurer.nextLayout(formatWidth);
            noLine++;
        }
        return noLine;
    }
    
    public static class customTextPane extends JTextPane {
        private boolean lineWrap;        
        private int BorderCorner = 15;
        private Color color = getBackground();
        
        public customTextPane(final boolean lineWrap, int borderorner, Color color){
            this.lineWrap = lineWrap;
            this.BorderCorner = borderorner;
            this.color = color;
            setOpaque(false);
            setBorder(new customBorder());
            if(lineWrap) setEditorKit(new WrapEditorKit());
        }
        
        public customTextPane(final boolean lineWrap, int borderorner){
            this.lineWrap = lineWrap;
            this.BorderCorner = borderorner;
            setOpaque(false);
            setBorder(new customBorder());
            if(lineWrap) setEditorKit(new WrapEditorKit());
        }
        
        public customTextPane(final boolean lineWrap){
            this.lineWrap = lineWrap;
            setOpaque(false);
            setBorder(new customBorder());
            if(lineWrap) setEditorKit(new WrapEditorKit());
        }
        
        @Override
        public boolean getScrollableTracksViewportWidth(){
            if(lineWrap) return super.getScrollableTracksViewportWidth();
            else return getParent()==null||getUI().getPreferredSize(this).width <= getParent().getSize().width;
        }
        
        private class customBorder implements Border{
            @Override
            public void paintBorder(Component cmpnt, Graphics g, int i, int i1, int i2, int i3) {
                Graphics2D graphics = (Graphics2D) g.create();
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setComposite(AlphaComposite.SrcOver.derive(0.3f));
                int width = getPreferredSize().width;
                int height = getPreferredSize().height;
                graphics.setColor(color);
                graphics.fillRoundRect(0, 0, width-1, height-1, BorderCorner, BorderCorner);
                /*graphics.setColor(myColor);
                graphics.drawRoundRect(0, 0, width-1, height-1, BorderCorner, BorderCorner);*/
            }

            @Override
            public Insets getBorderInsets(Component cmpnt) {
                int value = BorderCorner/2;
                return new Insets(value,value,value,value);
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        
        }
                
    }
    
    private static class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory = new WrapColumnFactory();
        
        @Override
        public ViewFactory getViewFactory(){
            return defaultFactory;
        }
    }
    
    private static class WrapColumnFactory implements ViewFactory {

        @Override
        public View create(final Element elmnt) {
            final String kind = elmnt.getName();
            if(kind!=null) {
                switch(kind) {
                    case AbstractDocument.ContentElementName: return new WrapLabelView(elmnt);
                    case AbstractDocument.ParagraphElementName: return new ParagraphView(elmnt);
                    case AbstractDocument.SectionElementName: return new BoxView(elmnt, View.Y_AXIS);
                    case StyleConstants.ComponentElementName: return new ComponentView(elmnt);
                    case StyleConstants.IconElementName: return new IconView(elmnt);
                }
            }
            return new LabelView(elmnt);
        }
        
    }
    
    private static class WrapLabelView extends LabelView {
        public WrapLabelView(final Element elm) {
            super(elm);
        }
        
        @Override
        public float getMinimumSpan(final int axis){
            switch(axis){
                case View.X_AXIS : return 0;
                case View.Y_AXIS : return super.getMinimumSpan(axis);
                default: throw new IllegalArgumentException("Invalid axis: "+axis);
            }
        }
    }    
}
