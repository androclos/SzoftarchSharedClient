/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import chess.Cell;
import chess.ChessBoard;
import chess.Piece;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pifko
 */
public class BoardPanel extends Panel{
    
    private char letters[] = {'a','b','c','d','e','f','g','h'};
    String message = null;
    ChessBoard board;
    HashMap pieceImages = new HashMap();
    public Cell selectedCell = null;
    
    boolean ready = true;
    Image animatedPieceImage = null;
    public Cell animPieceCoord;
    Mover m = null;
    Image backBuffer = null;
    
    
    public BoardPanel(ChessBoard board)
    {
        this.board = board;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        MediaTracker mediaTracker = new MediaTracker(this);
        int id = 0;
       
        
        //Image wKingImage = toolkit.getImage("wking.png");
        Image wKingImage = toolkit.getImage(getClass().getResource("wking.png"));
        mediaTracker.addImage(wKingImage, id); id++;
        pieceImages.put("K", wKingImage);


        //Image wQueenImage = toolkit.getImage("wqueen.png");
        Image wQueenImage = toolkit.getImage(getClass().getResource("wqueen.png"));
        mediaTracker.addImage(wQueenImage, id); id++;
        pieceImages.put("Q", wQueenImage);

        //Image wKnightImage = toolkit.getImage("wknight.png");
        Image wKnightImage = toolkit.getImage(getClass().getResource("wknight.png"));
        mediaTracker.addImage(wKnightImage, id); id++;
        pieceImages.put("N", wKnightImage);
        
        //Image wRookImage = toolkit.getImage("wrook.png");
        Image wRookImage = toolkit.getImage(getClass().getResource("wrook.png"));
        mediaTracker.addImage(wRookImage, id); id++;
        pieceImages.put("R", wRookImage);
        
        //Image wBishopImage = toolkit.getImage("wbishop.png");
        Image wBishopImage = toolkit.getImage(getClass().getResource("wbishop.png"));
        mediaTracker.addImage(wBishopImage, id); id++;
        pieceImages.put("B", wBishopImage);

        //Image wPawnImage = toolkit.getImage("wpawn.png");
        Image wPawnImage = toolkit.getImage(getClass().getResource("wpawn.png"));
        mediaTracker.addImage(wPawnImage, id); id++;
        pieceImages.put("P", wPawnImage);
        
        //Image bKingImage = toolkit.getImage("bking.png");
        Image bKingImage = toolkit.getImage(getClass().getResource("bking.png"));
        mediaTracker.addImage(bKingImage, id); id++;
        pieceImages.put("k", bKingImage);

        //Image bQueenImage = toolkit.getImage("bqueen.png");
        Image bQueenImage = toolkit.getImage(getClass().getResource("bqueen.png"));
        mediaTracker.addImage(bQueenImage, id); id++;
        pieceImages.put("q", bQueenImage);

        //Image bKnightImage = toolkit.getImage("bknight.png");
        Image bKnightImage = toolkit.getImage(getClass().getResource("bknight.png"));
        mediaTracker.addImage(bKnightImage, id); id++;
        pieceImages.put("n", bKnightImage);
        
        //Image bRookImage = toolkit.getImage("brook.png");
        Image bRookImage = toolkit.getImage(getClass().getResource("brook.png"));
        mediaTracker.addImage(bRookImage, id); id++;
        pieceImages.put("r", bRookImage);
        
        //Image bBishopImage = toolkit.getImage("bbishop.png");
        Image bBishopImage = toolkit.getImage(getClass().getResource("bbishop.png"));
        mediaTracker.addImage(bBishopImage, id); id++;
        pieceImages.put("b", bBishopImage);

        //Image bPawnImage = toolkit.getImage("bpawn.png");
        Image bPawnImage = toolkit.getImage(getClass().getResource("bpawn.png"));
        mediaTracker.addImage(bPawnImage, id); id++;
        pieceImages.put("p", bPawnImage);
        
      
        
        try
        {
           for(int i = 0; i < id; i++)
            mediaTracker.waitForID(i);
        }
        catch (InterruptedException ie)
        {
            System.err.println(ie);
            System.exit(1);            
        }        
	
    }
    
    
    class Mover extends Thread
    {
        Cell c0 = null;
        Cell c1 = null;
        Mover(Cell c0, Cell c1)
        {
            this.c0 = c0;
            this.c1 = c1;
        }
        public void run()
        { 
           ready = false;
           int x0 = 20 + c0.j * 50;
           int x1 = 20 + c1.j * 50;
           int y0 = 20 + c0.i * 50;
           int y1 = 20 + c1.i * 50;
           float dirX = x1 - x0;
           float dirY = y1 - y0;
           float length = (float)Math.sqrt(dirX * dirX + dirY * dirY);
           
           Piece p = board.removePiece(c0);
           float steps = length * 0.1f;
           dirX /= steps; 
           dirY /= steps;
            
           float x = x0;
           float y = y0;
           animPieceCoord = new Cell((int)x, (int)y);
           String key = p.toString();
           animatedPieceImage = (Image) pieceImages.get(key);  
		   try
           {              
            for(int s = 0; s < steps; s++)
            {
             x += dirX; 
             y += dirY;
             animPieceCoord.i = (int)x;
             animPieceCoord.j = (int)y;
             sleep(100);            
             repaint();
            }             
           }
           catch(InterruptedException ie)
           {
           }
           finally
           {
            board.putPiece(c1, p);
            animatedPieceImage = null;
            repaint();            
            ready = true;            
           }

        }
    }
    
   public void startAnimation(Cell c0, Cell c1)
    {
      m = new Mover(c0, c1);
      m.start();
    }
   
   public void stopAnimation()
    {
     if(m != null)
     {
      m.interrupt();
      m=null;
     }
    }
   
    public void paint(Graphics g)
    {
       
       Graphics drawGraphics = g;
       if(backBuffer == null)
       backBuffer = createImage(450, 500);
    
       drawGraphics = backBuffer.getGraphics();
       drawGraphics.clearRect(0,0,getSize().width,
                                 getSize().height); 
 
	   
       drawGraphics.setColor(new Color(0,0,0));
       drawGraphics.drawRect(5,5,430,430);
	   for(int i = 0; i < 8 ; i++)
		for(int j = 0; j < 8 ; j++)
		 {
		  if( (i + j) % 2 == 1)
           drawGraphics.setColor(new Color(0.6f,0.5f,0.3f));
		  else
		   drawGraphics.setColor(new Color(0.95f,0.85f,0.6f));
   
          drawGraphics.fillRect(20 + i * 50, 20 + j * 50, 50, 50);
		  Piece p = board.getPiece(new Cell(j, i));
           if(p != null)
            {
                
             String key = p.toString();
             Image im = (Image) pieceImages.get(key);  
             drawGraphics.drawImage(im, 20 + i * 50, 20 + j * 50, null); 
            }

		  
         }
       drawGraphics.setColor(new Color(0,0,0));
       for(int i = 0; i < 9 ; i++)
        {    
         drawGraphics.drawLine(20 + i * 50, 20, 20 + i * 50, 420);
         drawGraphics.drawLine(20, 20 + i * 50, 420, 20 + i * 50);
         if(i != 8)
          {
           drawGraphics.drawString(String.valueOf(letters[i]), 42 + i * 50, 17);
           drawGraphics.drawString(String.valueOf(letters[i]), 42 + i * 50, 432);
           drawGraphics.drawString(String.valueOf(8 - i), 10, 47 + i * 50);
           drawGraphics.drawString(String.valueOf(8 - i), 425, 47 + i * 50);
          }
        }

        if(animatedPieceImage != null)
         drawGraphics.drawImage(animatedPieceImage,animPieceCoord.i,animPieceCoord.j, null); 
		
		if(selectedCell != null)
         {
          drawGraphics.setColor(new Color(1.0f,0,0));
          drawGraphics.drawRect(19 + selectedCell.j * 50, 19 + selectedCell.i * 50, 52, 52);
         }

	   
	   if(message != null)
        {
         drawGraphics.setColor(new Color(0,0,0));
         drawGraphics.drawString( message, 20, 460);     
        }
        g.drawImage(backBuffer, 0, 0, null); 
    }

    public void display()
    {
	  repaint();   
    }
   
     
   public void printMessage(String message){ 
      this.message = message;
	  repaint();
   }
   
   
   
}
