/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analogsimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 *
 * @author niwatakumi
 */
public class GameSystem {
    GameBoard gb;
    
    public GameSystem() {
        gb = new GameBoard(GameBoard.BLUE);
        System.out.println("====== Game Start ======");
        
        // ネームエントリー
        nameEntry();
        
        for (int i = 0; i < 12; i++) {
            String id = convertTimeID(i);
            System.out.println("====== 【" + id + "】 ======");
            
            if(i != 0){
                System.out.println("=== payment(" + id + ") ===");
                gb.payment();
            }
            
            System.out.println("=== action(" + id + ") ===");
            int k = gb.getKomasCount();
            for (int j = 0; j < k; j++) {
                String line = input();
                
            }
            
            
        }
        
    }
    
    private String input(){
        String line;
        BufferedReader systemReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            line = systemReader.readLine();
            return line;
        } catch (IOException ex) {
            System.err.println("キーボード入力エラー");
            return null;
        }
    }
    
    private void nameEntry(){
        System.out.println("====== Name Entry ======");
        System.out.println("blue name?");
        gb.players[GameBoard.BLUE].setName(input());
        System.out.println("red name?");
        gb.players[GameBoard.RED].setName(input());
    }
    
    private String convertTimeID(int i){
        Integer num = (i / 2) + 1;
        int alpha = i % 2;
        String id = num.toString();
        if(alpha == 0){
            id += "a";
        }
        else{
            id += "b";
        }
        return id;
    }
    
    
}
