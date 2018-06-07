/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleclient;

import ai.LalasoAI;
import ai.SampleAI;
import gameElements.Game;
import gui.ClientGUI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author koji
 */
public class SimpleClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SimpleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        Game myGame = new Game();
        // TajimaAI myAI = new TajimaAI(myGame);
        LalasoAI myAI = new LalasoAI(myGame);
        ClientGUI gui = new ClientGUI(myAI);
        
        gui.setVisible(true);
    }
}
