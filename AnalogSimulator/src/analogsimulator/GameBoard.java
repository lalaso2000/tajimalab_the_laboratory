/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analogsimulator;

import java.util.ArrayList;

/**
 *
 * @author niwatakumi
 */
class GameBoard {
    
    // プレイヤー番号
    public static final int BLUE = 0;
    public static final int RED = 1;
    
    // プレイヤー
    public Player player[] = new Player[2];
    
    public GameBoard(){
        player[BLUE] = new Player(this, "blue");
        player[RED] = new Player(this, "red");
    }
}
