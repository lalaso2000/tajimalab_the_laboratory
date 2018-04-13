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
                        if (players[playerNum].payMoney(2)) {
                            // 金を払って、追加
                            experiment[i] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put experiment(2-" + (i + 1) + ") : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // カネがない
                        System.out.println("Error!");
                        return false;

                    }
                }
                // 空きが無ければエラー、
                System.out.println("Error!");
                return false;
            case 3:
                // プレゼンテーション
                if (presentation[actionNum-1][0] != -1) {
                    // 空いてなければエラー
                    System.out.println("Error!");
                    return false;
                }
                // 番号に応じて支払い、追加
                switch (actionNum) {
                    case 1:
                        if (players[playerNum].payFlasks(2)) {
                            // フラスコを払って追加
                            presentation[0] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put presentation(3-1) : " + playerNum + "," + KomaKind);
                            return true;

                        }
                        // フラスコが足りない
                        System.out.println("Error!");
                        return false;
                    case 2:
                        if (players[playerNum].payMoneyAndFlasks(1, 4)) {
                            // 金とフラスコを払って追加
                            presentation[1] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put presentation(3-2) : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // 足りない
                        System.out.println("Error!");
                        return false;
                    case 3:
                        if (players[playerNum].payMoneyAndFlasks(1, 8)) {
                            // 金とフラスコを払って追加
                            presentation[2] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put presentation(3-3) : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // 足りない
                        System.out.println("Error!");
                        return false;
                    default:
                        // 番号がおかしい
                        System.out.println("Error!");
                        return false;
                }
            case 4:
                // 論文
                for (int i = 0; i < 3; i++) {
                    // 空きを調べて
                    if (paper[i][0] == -1) {
                        // 空いてれば
                        if (players[playerNum].payMoneyAndFlasks(1, 8)) {
                            // 金とフラスコを払って追加
                            paper[i] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put Paper(4-" + (i + 1) + ") : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // たりない
                        System.out.println("Error!");
                        return false;
                    }
                }
                // 空きが無ければエラー、
                System.out.println("Error!");
                return false;
            case 5:
                // 研究報告
                if (report[actionNum-1][0] != -1) {
                    // 空いてなければエラー
                    System.out.println("Error!");
                    return false;
                }
                if (KomaKind == Player.KOMA_S) {
                    // 学生はおけない
                    System.out.println("Error!");
                    return false;
                }
                // 番号に応じて支払い、追加
                switch (actionNum) {
                    case 1:
                        report[0] = w.clone();
                        players[playerNum].putKoma(KomaKind);
                        System.out.println("put report(5-1) : " + playerNum + "," + KomaKind);
                        return true;
                    case 2:
                        if (players[playerNum].payFlasks(1)) {
                            // フラスコを払って追加
                            report[1] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put report(5-2) : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // 足りない
                        System.out.println("Error!");
                        return false;
                    case 3:
                        if (players[playerNum].payFlasks(3)) {
                            // フラスコを払って追加
                            report[2] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put report(5-3) : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // 足りない
                        System.out.println("Error!");
                        return false;
                    default:
                        // 番号がおかしい
                        System.out.println("Error!");
                        return false;
                }
            case 6:
                // 雇用
                if (employ[actionNum - 1][0] != -1) {
                    // 空いてなければエラー
                    System.out.println("Error!");
                    return false;
                }
                // 番号に応じて支払い、追加
                switch (actionNum) {
                    case 1:
                        if (KomaKind == Player.KOMA_S) {
                            // 学生はおけない
                            System.out.println("Error!");
                            return false;
                        }
                        if (players[playerNum].payFlasks(3)) {
                            // フラスコを払って追加
                            employ[0] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put employ(6-1) : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // 足りない
                        System.out.println("Error!");
                        return false;
                    case 2:
                        if (KomaKind == Player.KOMA_D && players[playerNum].getAllStars() >= 10){
                            // 条件を満たしていれば追加
                            employ[1] = w.clone();
                            players[playerNum].putKoma(KomaKind);
                            System.out.println("put employ(6-2) : " + playerNum + "," + KomaKind);
                            return true;
                        }
                        // 満たしてない
                        System.out.println("Error!");
                        return false;
                    default:
                        // 番号がおかしい
                        System.out.println("Error!");
                        return false;
                }
            default:
                System.out.println("Error!");
                return false;
        }

    }
}
