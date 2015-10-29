/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

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
        listener = new ServerListener(que, "localhost",2222);
        new Thread(listener).start();
    }

    public Integer getCleitnid() {
        return clientid;
    }

    public void setCleitnid(Integer cleitnid) {
        this.clientid = cleitnid;
    }
    
    
    
    
    public void login(String username, String password){
    
        try {
            
            String message = clientid +":"+ "auth:" + username + ":" + password;
            Message m = new Message(message);
            
            listener.getOuts().writeObject(m);
            
        } catch (IOException ex) {
            Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    
    }
    
}
