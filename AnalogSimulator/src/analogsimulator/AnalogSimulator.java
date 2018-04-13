/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analogsimulator;

/**
 *
 * @author niwatakumi
 */
public class AnalogSimulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        GameBoard gb = new GameBoard(GameBoard.RED);
        gb.player[GameBoard.BLUE].flasks = 10;
        gb.player[GameBoard.BLUE].showStatus();
        
        gb.setWorker(GameBoard.BLUE, Player.WK_STUDENT, "pr0");
        gb.player[GameBoard.BLUE].showStatus();
        
        gb.setWorker(GameBoard.BLUE, Player.WK_DOCTOR, "pr2");
        gb.player[GameBoard.BLUE].showStatus();
        
        gb.setWorker(GameBoard.BLUE, Player.WK_STUDENT, "pr0");
        gb.player[GameBoard.BLUE].showStatus();
        
        gb.player[GameBoard.BLUE].showStatus();
    }
    
}
