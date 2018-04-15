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
        gb.players[GameBoard.RED].showStatus();
        
        gb.players[GameBoard.BLUE].flasks = 20;
        gb.players[GameBoard.RED].flasks = 20;
        gb.players[GameBoard.RED].allStars = 20;
        
        gb.setKoma(0, 2, "4");
        gb.setKoma(0, 0, "6-1");
        gb.setKoma(1, 0, "6-2");
        
        gb.actionReturn();
        
        gb.players[GameBoard.BLUE].showStatus();
        gb.players[GameBoard.RED].showStatus();
        
    }
    
}
