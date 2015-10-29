/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import client.Message;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Model;
import view.LoginView;

/**
 *
 * @author Pifko
 */
public class Controller implements Runnable{
    
    LoginView view;
    Model model;
    public  ArrayBlockingQueue<Message> messageque;
    Integer clientid = -1;
    
    public Controller() {
        

        messageque = new ArrayBlockingQueue<Message>(100);
        view = new LoginView();
        model = new Model(messageque);
        setListeners();
        
    }
    
    public void setListeners(){
    
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
    
    public void login(){
    
        String name = view.getUsername_tf().getText();
        String pass = new String (view.getPassword_tf().getPassword());
        
        model.login(name, pass);
    
    }

    @Override
    public void run() {
        
        try {

            while(true){
                Message newmsg = this.messageque.take();

                System.out.println(newmsg.getMessage());
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
    
        System.out.println(message.get(0));
        System.out.println(message.get(1));

        String messageoperation = message.get(0);
        
        switch(messageoperation){
        
            case "name":{
                setClientId(Integer.valueOf(message.get(1)));
                break;
           }
           case "message":{
               view.getMessage_lbl().setText(original.getMessage());
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
   
   
}
