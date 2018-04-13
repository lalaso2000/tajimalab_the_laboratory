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
        gb.players[GameBoard.BLUE].flasks = 10;
        gb.players[GameBoard.BLUE].showStatus();
        
        gb.setKoma(GameBoard.BLUE, Player.KOMA_S, "3-1");
        gb.players[GameBoard.BLUE].showStatus();
        
        gb.setKoma(GameBoard.BLUE, Player.KOMA_D, "3-2");
        gb.players[GameBoard.BLUE].showStatus();
        
    }
    
}
