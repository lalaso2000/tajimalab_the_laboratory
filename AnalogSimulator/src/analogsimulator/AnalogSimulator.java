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
        gb.setWorker(GameBoard.RED, 0, "ex");
        gb.setWorker(GameBoard.BLUE, 1, "ex");
        gb.setWorker(GameBoard.RED, 1, "se");
        gb.player[GameBoard.RED].showStatus();
    }
    
}
