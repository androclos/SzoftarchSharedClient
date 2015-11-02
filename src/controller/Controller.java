/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import chess.Cell;
import chess.ChessBoard;
import client.Message;
import java.awt.Button;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Model;
import view.GameListView;
import view.LoginView;
import java.lang.Character;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.ChessPiece;
import view.BoardPanel;
import view.ChessFrame;
import view.ChessFrame;

/**
 *
 * @author Pifko
 */
public class Controller implements Runnable{
    
    public enum State {LOGIN, GAMELIST, GAME, WAITING;}
    
    LoginView view;
    ChessFrame frames;
    BoardPanel boardpanel;
    JLabel gamemessage;
    ChessBoard board;
    GameListView gamelistview;
    Model model;
    
    ArrayBlockingQueue<Message> messageque;
    Integer clientid = -1;
    String color = null;
    
    Cell source;
    Cell destiantion;
    
    State state;
    String username = null;
    
    public Controller() {

        state = State.LOGIN;
        messageque = new ArrayBlockingQueue<Message>(100);
        view = new LoginView();
        model = new Model(messageque);
        setLoginViewListeners();
        //setBoardView(null);
        
        
        //setBoardView(processBoard(msgprocess("board:r00:n01:b02:q03:k04:b05:n06:r07:p10:p11:p12:p13:p14:p15:p16:p17:P60:P61:P62:P63:P64:P65:P66:P67:R70:N71:B72:Q73:K74:B75:N76:R77")));
        
    }
    
    public void setBoardView(List<ChessPiece> piecelist){
    
        state = State.GAME;
        this.view.setVisible(false);
        
        if(piecelist != null){
           this.board = new ChessBoard(piecelist);
        }
        else
           this.board = new ChessBoard();
        
        this.frames = new ChessFrame();
        this.boardpanel = new BoardPanel(board);
        frames.getContentPane().add(boardpanel);
        
        
        JLabel websitelink = new JLabel();
        gamemessage = new JLabel("_");
        gamemessage.setAlignmentY(Component.CENTER_ALIGNMENT);
        frames.getContentPane().add(gamemessage);
        frames.getContentPane().add(websitelink);
        JButton leavegame_btn = new JButton("Leave Game");
        frames.getContentPane().add(leavegame_btn);
        
        
        frames.setSize(450,600);
        frames.setResizable(false);
        frames.setVisible(true);
        boardpanel.display();
        
        frames.setTitle("Chess - Your color is: "+color +" "+ username);
        
        leavegame_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                leavegame();
                destroyGameView();
                setGameListView();
                
            }
        });
        
        boardpanel.addMouseListener(new MouseAdapter()
        {
          public void mouseClicked(MouseEvent e){ //kattintasra kuldi a move uzenetet
          
            if(e.getY() < 420 && e.getY() > 20 && e.getX() < 420 && e.getX() > 20){
                
                Cell c = new Cell((e.getY()-20) / 50, (e.getX()-20) / 50);
                if(source == null){ //elso jattintas
                    source = c;
                    boardpanel.selectedCell = c;
                    boardpanel.display();
                }
                else{  //masodik kattintas
                   destiantion = c;  
                   model.moveMessage(source, destiantion);
                   boardpanel.selectedCell = destiantion = source = null;

                }
                
            }
          
          } 
        });
        
        frames.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            if (JOptionPane.showConfirmDialog(frames, 
                "Are you sure to close this window?", "Really Closing?", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                
                    leavegame();
                    //destroyGameView();
                    //setGameListView();
                    
                    System.exit(-1);

            }
        }
        });
        
        
        
        websitelink.setText("<html><a href=\"\">"+"Game Statistics"+"</a></html>");
        websitelink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        websitelink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    try {
                            Desktop.getDesktop().browse(new URI("https://pifko-pc:8181/ServletTest/Statistics"));
                    } catch (IOException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (URISyntaxException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        
    }
    
    public void setLoginViewListeners(){
    
        
        view.getLogin_bt().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                login();
                
            }
        });

    }
    
    public void setGameListListeners(){
    
        gamelistview.getLeavegame_btn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                

                
                
            }
        });
        
        gamelistview.getJoingame_btn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                joinGame(gamelistview.getGameJoinList_cbox().getSelectedItem().toString());
                
            }
        });
        
        gamelistview.getLoadgame_btn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                loadGame(gamelistview.getGameLoadList_cbox().getSelectedItem().toString());
                
            }
        });
        
        gamelistview.getStartnewgame_btn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                startNewGame();
                
            }
        });
    
        gamelistview.getRefreshlist_btn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                model.gameListMessage();
                
            }
        });
    
    }
    
    public void login(){
    
        String name = view.getUsername_tf().getText();
        String pass = new String (view.getPassword_tf().getPassword());
        
        username = name;
        
        model.loginMessage(name, pass);
    
    }

    @Override
    public void run() {
        
        try {

            while(true){
                Message newmsg = this.messageque.take();
                List<String> list = msgprocess(newmsg.getMessage());
                msghandler(list, newmsg);
            }
  
        } catch (InterruptedException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public List<String> msgprocess(String s){
    
        StringTokenizer st = new StringTokenizer(s,":"); //a:b:b:c uzenete felosztasa
        List<String> msgparts = new ArrayList<String>();      
        int partnum = st.countTokens();
                
        for(int i = 0;i<partnum;i++){
            msgparts.add(st.nextToken());
        }
    
        return msgparts;
    }  
    
    public void msghandler(List<String> message, Message original){
    
        System.out.println(original.getMessage());
        
        String messageoperation = message.get(0);
        
        switch(messageoperation){
        
            case "name":{
                setClientId(Integer.valueOf(message.get(1)));
                break;
            }
            case "login":{
                loginMessageHandler(message.get(1));
                break;
           }
            case "message":{
               //view.getMessage_lbl().setText(original.getMessage());
                setMessage(original.getMessage());
               break;
            }
            case "gamelist":{
                setGameList(original.getGamelist());
                break;
            }
            case "move":{
                makeMove(new Cell(Integer.valueOf(message.get(1)), Integer.valueOf(message.get(2))), new Cell(Integer.valueOf(message.get(3)), Integer.valueOf(message.get(4))));
                break;
            }
            case "boardstate":{
                setBoardView(processBoard(message));
                break;
            }
            case "color":{
                color = message.get(1);
                break;
            }
            case "gamestart":{
                setBoardView(null);
                break;
            }
            default: {
               break;
            }  
            
        }
    
    }
    
    public void setMessage(String msg){
    
        if(state == State.LOGIN)
            view.getMessage_lbl().setText(msg);
        else if(state == State.GAMELIST)
            gamelistview.getGamelistmessage_lbl().setText(msg);
        else if(state == State.GAME)
            gamemessage.setText(msg);
    
    }
    
    public void makeMove(Cell src, Cell dest){
        
        boardpanel.startAnimation(src, dest);

    }
    
    public void setClientId(Integer clientid){
   
       this.clientid = clientid;
       model.setCleitnid(clientid);
   
    }
   
    public void loginMessageHandler(String outcome){
   
       if(outcome.equals("success")){
       
           state = State.GAMELIST;
           model.gameListMessage();
           setGameListView();
       
       }
       else{
       
           view.getMessage_lbl().setText(" • Wrong name or password.");
           username = null;

       }
    }
   
   
    public void setGameList(List<String> games){
   
       gamelistview.getGameLoadList_cbox().removeAllItems();
       gamelistview.getGameJoinList_cbox().removeAllItems();
       for(String s : games){
            if(s.contains("loadgame"))
                gamelistview.getGameLoadList_cbox().addItem(s);
            else
                gamelistview.getGameJoinList_cbox().addItem(s);
        }
       
       gamelistview.revalidate();
       gamelistview.repaint();
    }
   
    public void joinGame(String s){
   
       Integer gameid = Character.getNumericValue(s.charAt(0));
       model.joinGameMessage(gameid);
   
    }
    public void loadGame(String s){
   
       Integer gameid = Character.getNumericValue(s.charAt(0));
       model.loadGameMessage(gameid);
   
    }
    public void startNewGame(){
   
       model.startNewGameMessage();
   
    }
   
    public List<ChessPiece> processBoard(List<String> piecelist){
    
        piecelist.remove("boardstate");
        List<ChessPiece> list = new ArrayList<ChessPiece>();
        
        for(String s : piecelist){
        
            list.add( new ChessPiece(s.substring(0, 1), Integer.valueOf(s.substring(1, 2)), Integer.valueOf(s.substring(2))));
        
        }
        
        return list;
    
    }
    
    public void leavegame(){
    
        model.leaveGameMessage();
  
    }
  
    public void setGameListView(){
    
        state = State.GAMELIST;
        gamelistview = new GameListView();
        model.gameListMessage();
        setGameListListeners();
        view.changePanel(gamelistview);
        view.setVisible(true);
    
    }
    
    public void destroyGameView(){
    
        frames.setVisible(false);
        frames.removeAll();
        boardpanel.removeAll();
        boardpanel = null;
        gamemessage = null;
        board = null;
    
    }
}
