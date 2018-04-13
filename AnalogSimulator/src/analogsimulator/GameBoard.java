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
  
    // ゼミ
    public ArrayList<Integer> semi;
    
    // 実験
    public int experiment[][];

    // 発表
    public int presentation[][];
    
    // 論文
    public int paper[][];
    
    // 研究報告
    public int report[][];
    
    // 雇用
    public int employ[][];
    
    
    // コンストラクタ
    public GameBoard(){
        player[BLUE] = new Player(this, "blue");
        player[RED] = new Player(this, "red");
        
        semi = new ArrayList<>();
        
        experiment = new int[3][2];
        presentation = new int[3][2];
        paper = new int[3][2];
        report = new int[3][2];
        employ = new int[2][2];
        
        
        
    }
}
