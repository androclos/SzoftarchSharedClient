/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import client.Message;
/*import com.sun.net.ssl.internal.ssl.Provider;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Security;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;*/

import java.io.*;
import java.net.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.*;
import javax.net.ssl.*;
import com.sun.net.ssl.*;
import com.sun.net.ssl.internal.ssl.Provider;
import java.security.Security;
import java.util.List;

/**
 *
 * @author Pifko
 */
public class ServerListener implements Runnable{

    private SSLSocket s;
    public  ArrayBlockingQueue<Message> que;
    ObjectInputStream ins;
    ObjectOutputStream outs;
    
    public ServerListener(ArrayBlockingQueue<Message> que, final String host, final int port) {
        
        try{
            this.que = que;

            Security.addProvider(new Provider());

            System.setProperty("javax.net.ssl.trustStoreType","JCEKS");
            System.setProperty("javax.net.ssl.trustStore","C:/temp/keystore.ks");
            System.setProperty("javax.net.ssl.trustStorePassword","password");

            SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            s = (SSLSocket)sslsocketfactory.createSocket(host,port);

            if(s.isConnected())
             {
                 System.out.println("1000");
                outs = new ObjectOutputStream(this.s.getOutputStream());
                ins = new ObjectInputStream(this.s.getInputStream());
                 
             }
        } catch(IOException e){
            
               e.printStackTrace();
        
        }
        
        
    }

    @Override
    public void run() {
        
        try {
            
            
            
            Message newmsg = null;
            while (((newmsg = (Message)ins.readObject()) != null)) {
            
                if(newmsg != null){
                    que.add(newmsg);
                }

            }
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ObjectOutputStream getOuts() {
        return outs;
    }
    
    
    
    
}
