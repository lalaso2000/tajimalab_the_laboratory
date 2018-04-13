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
    public ArrayList<Integer> semi = new ArrayList<>();
    
    // 実験
    public int experiment[][] = {{-1,-1},{-1,-1},{-1,-1}};

    // 発表
    public int presentation[][] = {{-1,-1},{-1,-1},{-1,-1}};
    
    // 論文
    public int paper[][] = {{-1,-1},{-1,-1},{-1,-1}};
    
    // 研究報告
    public int report[][] = {{-1,-1},{-1,-1},{-1,-1}};
    
    // 雇用
    public int employ[][] = {{-1,-1},{-1,-1}};
    
    // スタートプレイヤー
    public int startPlayer;
    
    // トレンド
    public boolean trend = false;
    
    // タイムライン
    public int season = 0;
    public int seasonStars[][] = {{0,0},{0,0},{0,0}};
    
    
    
    // コンストラクタ
    public GameBoard(int s){
        player[BLUE] = new Player(this, "blue");
        player[RED] = new Player(this, "red");
                
        startPlayer = s;
    }
}
