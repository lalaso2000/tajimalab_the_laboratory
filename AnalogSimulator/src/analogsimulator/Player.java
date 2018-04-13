/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analogsimulator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author niwatakumi
 */
public class Player {
    // プレイヤーのクラス

    // ワーカーの種類
    public static final int KOMA_D = 0;         // 教授
    public static final int KOMA_A = 1;         // 助手
    public static final int KOMA_S = 2;         // 学生

    public String name;                         // 自分の名前
    public GameBoard board;                     // 自分がいるボード
    public int[] komas = {1,0,1};             // コマ人数
    private int[] remainKomas = {1,0,1};       // 手元のコマ人数
    public int money = 5;                       // 資金
    public int flasks = 0;                      // フラスコ(研究成果)
    public int allStars = 0;                    // 星(研究業績)の総和

    public Player(GameBoard gb, String n) {
        board = gb;
        name = n;

    }

    public void showStatus() {
        System.out.println("====== " + name + "======");
        System.out.println("koma : " + komas);
        System.out.println("money : " + money);
        System.out.println("flasks : " + flasks);
        System.out.println("allStars : " + allStars);
        System.out.println("\n");
    }
    
    public boolean existKoma(int workerKind){
        if(remainKomas[workerKind] <= 0) return false;
        return true;
    }
    
    public boolean putKoma(int workerKind){
        if(remainKomas[workerKind] == 0) return false;
        remainKomas[workerKind] -= 1;
        return true;
    }
    
    public boolean payMoney(int cost) {
        int m = money;
        int sub = m - cost;
        if (sub < 0) return false;
        money = sub;
        return true;
    }
    
    public boolean payFlasks(int cost) {
        int f = flasks;
        int sub = f - cost;
        if (sub < 0) return false;
        flasks = sub;
        return true;
    }

}
