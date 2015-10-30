/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import chess.Cell;
import chess.ChessBoard;
import client.Message;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import view.BoardPanel;
import view.BoardView;
import view.ChessFrame;

/**
 *
 * @author Pifko
 */
public class Controller implements Runnable{
    
    LoginView view;
    ChessFrame frame;
    BoardPanel boardpanel;
    ChessBoard board;
    GameListView gamelistview;
    Model model;
    public  ArrayBlockingQueue<Message> messageque;
    Integer clientid = -1;
    
    Cell source;
    Cell destiantion;
    
    public Controller() {

        messageque = new ArrayBlockingQueue<Message>(100);
        view = new LoginView();
        //model = new Model(messageque);
        setLoginViewListeners();
        setBoardView();
        
    }
    
    public void setBoardView(){
    
        this.frame = new ChessFrame();
        this.board = new ChessBoard();
        this.boardpanel = new BoardPanel(board);
        frame.add((Component)boardpanel);
        frame.setSize(450,600);
        frame.setResizable(false);
        frame.setVisible(true);
        boardpanel.display();
        
        boardpanel.addMouseListener(new MouseAdapter()
        {
          public void mouseClicked(MouseEvent e){
          
            if(e.getY() < 420 && e.getY() > 20 && e.getX() < 420 && e.getX() > 20){
                
                Cell c = new Cell((e.getY()-20) / 50, (e.getX()-20) / 50);
                if(source == null){
                    source = c;
                    boardpanel.selectedCell = c;
                    boardpanel.display();
                }
                else{
                   destiantion = c;  
                   boardpanel.startAnimation(source,destiantion);
                   boardpanel.selectedCell = destiantion = source = null;

                }
                
            }
          
          } 
        });
        
        

        
    }
    
    public void setLoginViewListeners(){
    
        /*loginview.getLoginButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                LoginAttempt();
                
            }
        });*/
        
        view.getLogin_bt().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                login();
                
            }
        });
    
    
    }
    
    public void setGameListListeners(){
    
        gamelistview.getGameJoinList_cbox().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                joinGame(gamelistview.getGameJoinList_cbox().getSelectedItem().toString());
                
            }
        });
        
        gamelistview.getLoadgame_btn().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                joinGame(gamelistview.getGameLoadList_cbox().getSelectedItem().toString());
                
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
               view.getMessage_lbl().setText(original.getMessage());
               break;
            }
            case "gamelist":{
                setGameList(original.getGamelist());
               break;
            }
            default: {
               break;
            }
           
            
            
        }
    
    }
    
   public void setClientId(Integer clientid){
   
       this.clientid = clientid;
       model.setCleitnid(clientid);
   
   }
   
   public void loginMessageHandler(String outcome){
   
       if(outcome.equals("success")){
       
           model.gameListMessage();
           gamelistview = new GameListView();
           setGameListListeners();
           view.changePanel(gamelistview);
       
       }
       else{
       
           view.getMessage_lbl().setText(" • Wrong name or password.");

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
   
   
}
