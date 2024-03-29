/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bootstrap;

import gameElements.Game;
import guiParts.GUIFrame;
import guiParts.SimpleCUI;
import guiParts.SimpleCUIFrame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import network.ServerThread;

/**
 *
 * @author ktajima
 */
public class Laboratory2017Server {
    public static final String TITLE = "The Laboratory 2018 Server";
    public static final String VERSION = "ver1.10  b18062500";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println(TITLE+VERSION);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Laboratory2017Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Laboratory2017Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Laboratory2017Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Laboratory2017Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        //pieceDebug();
        Game mainGame = new Game();
        SimpleCUI cui = new SimpleCUI();
        SimpleCUIFrame frame = new SimpleCUIFrame();
        frame.setTitle(TITLE+VERSION);
        
        GUIFrame guiFrame = new GUIFrame();
        guiFrame.setTitle(TITLE+VERSION);
        
        mainGame.addObserver(cui);
        mainGame.addObserver(frame);
        mainGame.addObserver(guiFrame);

        frame.setVisible(true);
        guiFrame.setVisible(true);
               
        ServerThread sth = new ServerThread(18420,mainGame);
        try {
            sth.waitStart();
        } catch (IOException ex) {
        }
        //    gui.setServerThread(sth);
    }
        
        
    

}
