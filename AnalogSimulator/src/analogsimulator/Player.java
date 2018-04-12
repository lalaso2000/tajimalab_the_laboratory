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
    public static final int W_DOCTOR = 1;
    public static final int W_ASSISTANT = 2;
    public static final int W_STUDENT = 3;

    public String name;                        // 自分の名前
    public GameBoard board;                    // 自分がいるボード
    public ArrayList<Integer> workers;         // 持ちワーカーのリスト
    public ArrayList<Integer> remainWorkers;   // 手元にいる持ちワーカー(ボードに出すと減る)
    public int money;                          // 資金
    public int flasks;                         // フラスコ(研究成果)
    public int allStars;                       // 星(研究業績)の総和

    public Player(GameBoard gb, String n) {
        board = gb;
        name = n;
        workers = new ArrayList<>(Arrays.asList(W_DOCTOR, W_STUDENT));
        remainWorkers = (ArrayList<Integer>) workers.clone();
        money = 5;
        flasks = 0;
        allStars = 0;
    }

    public void showStatus() {
        System.out.print("workers : ");
        for (Integer worker : workers) {
            System.out.print(worker + ",");
        }
        System.out.println("");
        System.out.println("money : " + money);
        System.out.println("flasks : " + flasks);
        System.out.println("allStars : " + allStars);
    }

}
