/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chessclient;

import controller.Controller;

/**
 *
 * @author Pifko
 */
public class ChessClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        new Thread(){
        
            public void run(){
        
                
                new Thread(new Controller()).start();
                
            }
        
        
        }.start();
        
        
        
    }
    
}
