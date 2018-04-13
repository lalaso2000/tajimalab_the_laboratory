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
    public ArrayList semi = new ArrayList();
    
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
    
    
    
    // ワーカーを作業スペースに置く
    public void setWorker(int playerNum, int workerNum, String workspaceName){
        // 残りワーカーを減らす
        player[playerNum].remainWorkers.set(workerNum, -1);
        
        // 配置するワーカーの情報
        int w[] = {playerNum, workerNum};
        
        // 文字列を解釈
        String workspaceKind = workspaceName.substring(0, 2);
        int workspaceNum = 0;
        if(workspaceName.length() == 3){
            workspaceNum = Integer.parseInt(workspaceName.substring(2));
        }
        
        // マスに置きに行く & 支払い
        switch(workspaceKind){
            case "se":
                semi.add(w.clone());
                System.out.println("put semi : " + playerNum + "," + workerNum);
                break;
            case "ex":
                for (int i = 0; i < 3; i++) {
                    if(experiment[i][0] == -1){
                        experiment[i] = w.clone();
                        player[playerNum].money -= 2;
                        System.out.println("put experiment : " + playerNum + "," + workerNum);
                        break;
                    }
                }
                break;
            default:
                break;
        }
    }
}
