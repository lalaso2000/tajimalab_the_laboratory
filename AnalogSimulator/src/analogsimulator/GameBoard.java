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
        player[BLUE] = new Player(this, "blue");
        player[RED] = new Player(this, "red");

        startPlayer = s;
    }

    // ワーカーを作業スペースに置く
    public boolean setWorker(int playerNum, int workerKind, String workspaceName) {
        // 配置するワーカーの情報
        int w[] = {playerNum, workerKind};

        // ワーカー人数を確認
        if (!player[playerNum].existWoker(workerKind)) {
            System.out.println("Error!");
            return false;
        }

        // 文字列を解釈
        String workspaceKind = workspaceName.substring(0, 2);
        int workspaceNum = 0;
        if (workspaceName.length() == 3) {
            workspaceNum = Integer.parseInt(workspaceName.substring(2));
        }

        // マスに置きに行く & 支払い
        switch (workspaceKind) {
            case "se":
                // ゼミ
                semi.add(w.clone());
                player[playerNum].putWorker(workerKind);
                System.out.println("put semi : " + playerNum + "," + workerKind);
                return true;
            case "ex":
                // 実験
                for (int i = 0; i < 3; i++) {
                    // 空きを調べて
                    if (experiment[i][0] == -1) {
                        // 空いてれば
                        if (!player[playerNum].payMoney(2)) {
                            // 金がなければエラー、戻す
                            System.out.println("Error!");
                            return false;
                        }
                        // 追加
                        experiment[i] = w.clone();
                        player[playerNum].putWorker(workerKind);
                        System.out.println("put experiment : " + playerNum + "," + workerKind);
                        return true;
                    }
                }
                // 空きが無ければエラー、
                System.out.println("Error!");
                return false;
            case "pr":
                // プレゼンテーション
                if(presentation[workspaceNum][0] != -1){
                    // 空いてなければエラー
                    System.out.println("Error!");
                    return false;
                }
                // 空きに応じて支払い、追加
                switch (workspaceNum) {
                    case 0:
                        if(!player[playerNum].payFlasks(2)){
                            // フラスコが足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        presentation[0] = w.clone();
                        player[playerNum].putWorker(workerKind);
                        System.out.println("put presentation[0] : " + playerNum + "," + workerKind);
                        return true;
                    case 1:
                        if(!player[playerNum].payFlasks(4)){
                            // フラスコが足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        if(!player[playerNum].payMoney(1)){
                            // 金が足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        presentation[1] = w.clone();
                        player[playerNum].putWorker(workerKind);
                        System.out.println("put presentation[1] : " + playerNum + "," + workerKind);
                        return true;
                    case 2:
                        if(!player[playerNum].payFlasks(8)){
                            // フラスコが足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        if(!player[playerNum].payMoney(1)){
                            // 金が足りない、エラー
                            System.out.println("Error!");
                            return false;
                        }
                        presentation[1] = w.clone();
                        player[playerNum].putWorker(workerKind);
                        System.out.println("put presentation[2] : " + playerNum + "," + workerKind);
                        return true;
                }
            default:
                break;
        }
        return true;
    }
}
