/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import chess.Cell;
import client.Message;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pifko
 */
public class Model {
    
    public  ArrayBlockingQueue<Message> que;
    public ServerListener listener;
    Integer clientid = -1;

    public Model(ArrayBlockingQueue<Message> que) {
        this.que = que;
        listener = new ServerListener(que);
        new Thread(listener).start();
    }

    public Integer getCleitnid() {
        return clientid;
    }

    public void setCleitnid(Integer cleitnid) {
        this.clientid = cleitnid;
    }
    
    public void loginMessage(String username, String password){
    
        try {
            
            String message = clientid +":"+ "auth:" + username + ":" + password;
            Message m = new Message(message);
            
            listener.getOuts().writeObject(m);
            
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void gameListMessage(){
    
        try {
            String message = clientid + ":gamelist";
            Message m = new Message(message);
            
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
 
    }
    
    public void joinGameMessage(Integer gameid){
        
        try {
            Message m = new Message(clientid + ":joingame:" +gameid);
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    public void loadGameMessage(Integer gameid){
    
        try {
            Message m = new Message(clientid + ":loadgame:" +gameid);
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    public void startNewGameMessage(){
        
        try {
            Message m = new Message(clientid + ":newgame");
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void moveMessage(Cell src, Cell dst){
    
        try {
            Message m = new Message(clientid + ":move" + ":" + src.i + ":" + src.j + ":"+dst.i + ":" + dst.j);
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    public void leaveGameMessage(){
    
        try {
            Message m = new Message(clientid + ":leavegame");
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void logoutMessage(){
    
        try {
            Message m = new Message(clientid + ":logout");
            listener.getOuts().writeObject(m);
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
}
