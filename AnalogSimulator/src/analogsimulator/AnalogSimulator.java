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
        gb.player[GameBoard.RED].showStatus();
        gb.setWorker(GameBoard.RED, Player.WK_DOCTOR, "se");
        gb.setWorker(GameBoard.BLUE, Player.WK_STUDENT, "ex");
        gb.setWorker(GameBoard.RED, Player.WK_ASSISTANT, "se");
        gb.player[GameBoard.RED].showStatus();
    }
    
}
