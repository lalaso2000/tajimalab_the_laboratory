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

    // プレイヤーID
    public static final int BLUE = 0;
    public static final int RED = 1;

    // プレイヤー
    public Player players[] = new Player[2];

    // ゼミ
    public ArrayList semi = new ArrayList();

    // 実験
    public int experiment[][] = {{-1, -1}, {-1, -1}, {-1, -1}};

    // 発表
    public int presentation[][] = {{-1, -1}, {-1, -1}, {-1, -1}};

    // 論文
    public int paper[][] = {{-1, -1}, {-1, -1}, {-1, -1}};

    // 研究報告
    public int report[][] = {{-1, -1}, {-1, -1}, {-1, -1}};

    // 雇用
    public int employ[][] = {{-1, -1}, {-1, -1}};

    // スタートプレイヤー
    public int startPlayer;

    // トレンド
    public boolean trend = false;

    // タイムライン
    public int season = 0;
    public int seasonStars[][] = {{0, 0}, {0, 0}, {0, 0}};

    // コンストラクタ
    public GameBoard(int s) {
        players[BLUE] = new Player(this, "blue");
        players[RED] = new Player(this, "red");

        startPlayer = s;
    }

    // コマをアクションに置く
    public boolean setKoma(int playerNum, int KomaKind, String actionName) {
        // 配置するコマの情報
        int w[] = {playerNum, KomaKind};

        // コマ人数を確認
        if (!players[playerNum].existKoma(KomaKind)) {
            System.out.println("Error!");
            return false;
        }

        // 文字列を解釈
        int actionKind = Integer.parseInt(actionName.substring(0, 1));
        int actionNum = 0;
        if (actionName.length() == 3) {
            actionNum = Integer.parseInt(actionName.substring(2));
        }

        // マスに置きに行く & 支払い
        switch (actionKind) {
            case 1:
                // ゼミ
                semi.add(w.clone());
                players[playerNum].putKoma(KomaKind);
                System.out.println("put semi(1-1) : " + playerNum + "," + KomaKind);
                return true;
            case 2:
                // 実験
                for (int i = 0; i < 3; i++) {
                    // 空きを調べて
                    if (experiment[i][0] == -1) {
                        // 空いてれば
                        if (!players[playerNum].payMoney(2)) {
                            // 金がなければエラー、戻す
                            System.out.println("Error!");
                            return false;
                        }
                        // 追加
                        experiment[i] = w.clone();
                        players[playerNum].putKoma(KomaKind);
                        System.out.println("put experiment(2-" + (i+1) + ") : " + playerNum + "," + KomaKind);
                        return true;
                    }
                }
                // 空きが無ければエラー、
                System.out.println("Error!");
                return false;
            case 3:
                // プレゼンテーション
                if(presentation[actionNum][0] != -1){
                    // 空いてなければエラー
                    System.out.println("Error!");
                    return false;
                }
                // 空きに応じて支払い、追加
                switch (actionNum) {
                    case 1:
                        if(!players[playerNum].payFlasks(2)){
                            // フラスコが足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        presentation[0] = w.clone();
                        players[playerNum].putKoma(KomaKind);
                        System.out.println("put presentation(3-1) : " + playerNum + "," + KomaKind);
                        return true;
                    case 2:
                        if(!players[playerNum].payFlasks(4)){
                            // フラスコが足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        if(!players[playerNum].payMoney(1)){
                            // 金が足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        presentation[1] = w.clone();
                        players[playerNum].putKoma(KomaKind);
                        System.out.println("put presentation(3-2) : " + playerNum + "," + KomaKind);
                        return true;
                    case 3:
                        if(!players[playerNum].payFlasks(8)){
                            // フラスコが足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        if(!players[playerNum].payMoney(1)){
                            // 金が足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        presentation[2] = w.clone();
                        players[playerNum].putKoma(KomaKind);
                        System.out.println("put presentation(3-3) : " + playerNum + "," + KomaKind);
                        return true;
                }
            default:
                break;
        }
        return true;
    }
}
