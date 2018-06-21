/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gameElements.GameResources;
import gui.ClientGUI;
import gui.MessageRecevable;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import network.ServerConnecter;

/**
 * 田島研究室用、AI用抽象クラス 継承しないと利用不可
 *
 * @author niwatakumi
 */
public abstract class TajimaLabAI extends LaboAI {

    // 自プレイヤーの名前
    protected String myName = "TajimaAI";
    // 自プレイヤーの番号
    protected int myNumber;
    // 相手プレイヤーの番号
    protected int enemyNumber;

    // サーバーとのコネクタ
    protected ServerConnecter connecter;
    // GUI
    protected ClientGUI gui;

    // 季節を更新してもいいか
    protected boolean canChangeSeason = true;
    // 季節の更新があるか
    protected boolean changeSeasonFlag = false;

    /**
     * コンストラクタ
     *
     * @param game
     */
    public TajimaLabAI(Game game) {
        super(game);
    }

    public String getMyName() {
        return myName;
    }

    /**
     * サーバーに接続するメソッド
     *
     * @param connecter
     */
    @Override
    public void setConnecter(ServerConnecter connecter) {
        // サーバーに接続する
        this.connecter = connecter;
        this.connecter.addMessageRecever(this);
    }

    /**
     * GUIに接続する
     *
     * @param mr
     */
    @Override
    public void setOutputInterface(MessageRecevable mr) {
        // GUIに接続する
        this.gui = (ClientGUI) mr;
    }

    /**
     * サーバーにメッセージを送る
     *
     * @param sendText 送るメッセージ
     */
    public void sendMessage(String sendText) {
        //属性情報を作成
        SimpleAttributeSet attribute = new SimpleAttributeSet();
        //属性情報の文字色に赤を設定
        attribute.addAttribute(StyleConstants.Foreground, Color.RED);

        //サーバーへ送信
        if (this.connecter.canWrite()) {
            this.connecter.sendMessage(sendText);
            this.gui.addMessage("[send]" + sendText + "\n", attribute);
        } else {
            this.gui.addMessage("(送信失敗)" + sendText + "\n", attribute);
        }
    }

    /**
     * クライアント側のログにテキストを表示（緑）
     *
     * @param text
     */
    @Override
    public void addMessage(String text) {
        //属性情報を作成
        SimpleAttributeSet attribute = new SimpleAttributeSet();
        //属性情報の文字色に緑を設定
        attribute.addAttribute(StyleConstants.Foreground, Color.GREEN);

        // クライアント側のログに緑で表示
        this.gui.addMessage(text, attribute);
    }

    /**
     * メッセージを受信した時のメソッド
     *
     * @param text
     */
    @Override
    public void reciveMessage(String text) {
        String messageNum = text.substring(0, 3);
        switch (messageNum) {
            case "100":
                // サーバーが応答した時
                this.helloServer();
                break;
            case "102":
                // サーバーからプレイヤー番号が返ってきた時
                this.checkNumber(text);
                this.gameBoard.startGame();
                break;
            case "204":
                // 自分のターンの処理
                this.thinkStart();
                this.think();
                this.stopThinking();
                break;
            case "206":
                // 相手が打った時の処理
                this.enemyPlay(text);
                break;
            case "207":
                // 季節が変わったらしい時は自分の仮想ゲームでも更新する
                this.changeSeasonFlag = true;
                this.changeSeason();
                break;
            case "214":
                // トレンドを更新する
                this.setTrend(text);
                break;
        }
    }

    /**
     * サーバーが応答した時のメソッド
     */
    protected void helloServer() {
        // 名前を送る
        this.sendMessage("101 NAME " + this.myName);
    }

    /**
     * サーバーからプレイヤー番号が送られてきた時の処理
     *
     * @param text サーバーからのメッセージ
     */
    protected void checkNumber(String text) {
        // 番号を確認する
        this.myNumber = Integer.parseInt(text.substring(13));
        if (this.myNumber == 0) {
            this.enemyNumber = 1;
        } else {
            this.enemyNumber = 0;
        }
    }

    /**
     * 相手の手を仮想ボードで打つ
     *
     * @param text
     */
    private void enemyPlay(String text) {
        String worker = text.substring(13, 14);
        String place = text.substring(15, 18);
        this.gameBoard.play(this.enemyNumber, place, worker);
        if (place.equals("5-3")) {
            // 5-3打たれた時はトレンドを確認
            this.checkTrend();
        }
    }

    /**
     * コマを置くメソッド
     *
     * @param action
     */
    protected void putWorker(Action action) {
        String worker = action.worker;
        String place = action.place;
        String trend = action.trend;
        if (trend != null) {
            this.putWorker(worker, place, trend);
        } else {
            this.putWorker(worker, place);
        }
    }

    /**
     * コマを置くメソッド(トレンド無し版)
     *
     * @param worker [PAS]
     * @param place 1-1|[2-4]-[123]|[56]-[12]
     */
    private void putWorker(String worker, String place) {
        if (this.gameBoard.play(this.myNumber, place, worker, false)) {
            this.sendMessage("205 PLAY " + this.myNumber + " " + worker + " " + place);
            this.gameBoard.play(this.myNumber, place, worker);
        } else {
            System.err.println("Put Error!!");
        }
    }

    /**
     * コマを置くメソッド(トレンドあり)
     *
     * @param worker [PA]
     * @param place 5-3
     * @param trend T[1-3]
     */
    private void putWorker(String worker, String place, String trend) {
        if (this.gameBoard.play(this.myNumber, place, worker, false)) {
            this.sendMessage("205 PLAY " + this.myNumber + " " + worker + " " + place + " " + trend);
            this.gameBoard.play(this.myNumber, place, worker);
            this.gameBoard.setTreand(trend);
        } else {
            System.err.println("Put Error!!");
        }
    }

    /**
     * トレンド移動確認
     */
    private void checkTrend() {
        this.canChangeSeason = false;
        this.sendMessage("210 CONFPRM");
    }

    /**
     * トレンドをセットする
     *
     * @param text
     */
    private void setTrend(String text) {
        String trendStr = text.substring(10);
        this.gameBoard.setTreand(trendStr);
        this.canChangeSeason = true;
        this.changeSeason();
    }

    /**
     * 季節を更新する
     */
    protected void changeSeason() {
        if (this.canChangeSeason && this.changeSeasonFlag) {
            this.gameBoard.changeNewSeason();
//            String log = this.gameBoard.getBoardInformation();
//            this.addMessage(log);
//            log = this.gameBoard.getResourceInformation();
//            this.addMessage(log);
            this.changeSeasonFlag = false;
            this.seasonChanged();
        }
    }

    /**
     * 季節の文字列をトレンドの数値に変換（リソース取得時に必要） 存在しない季節を投げるとnullが返ってきます
     *
     * @param season 季節文字列
     * @return トレンド文字列 or null
     */
    protected Integer convertSeasonToTrend(String season) {
        Integer trendInt = null;    // 現在の季節はトレンドだと何番目か
        switch (season) {
            case "1a":
            case "1b":
            case "4a":
            case "4b":
                trendInt = 0;
                break;
            case "2a":
            case "2b":
            case "5a":
            case "5b":
                trendInt = 1;
                break;
            case "3a":
            case "3b":
            case "6a":
            case "6b":
                trendInt = 2;
                break;
        }
        return trendInt;
    }

    /**
     * トレンドの文字列を数値に変換 トレンド無しは-1、トレンドT1、T2、T3はそれぞれ0,1,2に変換されます
     *
     * @param trendStr トレンドの文字列
     * @return 数値に変換した結果
     */
    protected int convertTrendStrToInt(String trendStr) {
        int trendInt = -1;
        switch (trendStr) {
            case "T1":
                trendInt = 0;
                break;
            case "T2":
                trendInt = 1;
                break;
            case "T3":
                trendInt = 2;
                break;
        }
        return trendInt;
    }

    /**
     * 計算用の仮想リソースを返す
     *
     * @param game アクション前のゲーム盤面
     * @return リソースの配列
     */
    protected GameResources[] getResourcesForEvaluation(Game game) {
        // リソース
        GameResources[] resources = new GameResources[2];

        resources[0] = game.getResourcesOf(0).clone();
        resources[1] = game.getResourcesOf(1).clone();

        // 行動で増える分を加味
        HashMap<String, ArrayList<String>> workers = game.getBoard().getWorkersOnBoard();
        //ゼミによる研究ポイントの獲得
        ArrayList<String> seminorwokers = workers.get("1-1");
        if (seminorwokers != null) {
            int PACount = 0;
            int SCount[] = {0, 0};
            for (String w : seminorwokers) {
                switch (w) {
                    case "P0":
                        PACount++;
                        resources[0].addReserchPoint(2);
                        break;
                    case "P1":
                        PACount++;
                        resources[1].addReserchPoint(2);
                        break;
                    case "A0":
                        PACount++;
                        resources[0].addReserchPoint(3);
                        break;
                    case "A1":
                        PACount++;
                        resources[1].addReserchPoint(3);
                        break;
                    case "S0":
                        SCount[0]++;
                        break;
                    case "S1":
                        SCount[1]++;
                        break;
                    default:
                        break;
                }
            }
            resources[0].addReserchPoint((int) ((SCount[0] + SCount[1]) / 2) * PACount);
            resources[1].addReserchPoint((int) ((SCount[0] + SCount[1]) / 2) * PACount);
        }

        //実験による研究ポイントの獲得
        String[] keys = {"2-1", "2-2", "2-3"};
        int[] points = {3, 4, 5};
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (workers.containsKey(key)) {
                String worker = workers.get(key).get(0);
                if (worker.endsWith("0")) {
                    resources[0].addReserchPoint(points[i]);
                } else if (worker.endsWith("1")) {
                    resources[1].addReserchPoint(points[i]);
                }
            }
        }

        //発表による業績の獲得
        int ScoreTreand = this.convertSeasonToTrend(game.getSeason());

        String key;
        key = "3-1";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.equals("P0")) {
                resources[0].addScorePoint(ScoreTreand, 1);
            } else if (w.equals("P1")) {
                resources[1].addScorePoint(ScoreTreand, 1);
            } else if (w.equals("A0")) {
                resources[0].addScorePoint(ScoreTreand, 1);
            } else if (w.equals("A1")) {
                resources[1].addScorePoint(ScoreTreand, 1);
            } else if (w.equals("S0")) {
                resources[0].addScorePoint(ScoreTreand, 2);
            } else if (w.equals("S1")) {
                resources[1].addScorePoint(ScoreTreand, 2);
            }
        }
        key = "3-2";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.equals("P0")) {
                resources[0].addScorePoint(ScoreTreand, 3);
            } else if (w.equals("P1")) {
                resources[1].addScorePoint(ScoreTreand, 3);
            } else if (w.equals("A0")) {
                resources[0].addScorePoint(ScoreTreand, 4);
            } else if (w.equals("A1")) {
                resources[1].addScorePoint(ScoreTreand, 4);
            } else if (w.equals("S0")) {
                resources[0].addScorePoint(ScoreTreand, 4);
            } else if (w.equals("S1")) {
                resources[1].addScorePoint(ScoreTreand, 4);
            }
        }
        key = "3-3";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.equals("P0")) {
                resources[0].addScorePoint(ScoreTreand, 7);
            } else if (w.equals("P1")) {
                resources[1].addScorePoint(ScoreTreand, 7);
            } else if (w.equals("A0")) {
                resources[0].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("A1")) {
                resources[1].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("S0")) {
                resources[0].addScorePoint(ScoreTreand, 5);
            } else if (w.equals("S1")) {
                resources[1].addScorePoint(ScoreTreand, 5);
            }
        }

        //論文による業績の獲得
        key = "4-1";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.equals("P0")) {
                resources[0].addScorePoint(ScoreTreand, 8);
            } else if (w.equals("P1")) {
                resources[1].addScorePoint(ScoreTreand, 8);
            } else if (w.equals("A0")) {
                resources[0].addScorePoint(ScoreTreand, 7);
            } else if (w.equals("A1")) {
                resources[1].addScorePoint(ScoreTreand, 7);
            } else if (w.equals("S0")) {
                resources[0].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("S1")) {
                resources[1].addScorePoint(ScoreTreand, 6);
            }
        }
        key = "4-2";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.equals("P0")) {
                resources[0].addScorePoint(ScoreTreand, 7);
            } else if (w.equals("P1")) {
                resources[1].addScorePoint(ScoreTreand, 7);
            } else if (w.equals("A0")) {
                resources[0].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("A1")) {
                resources[1].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("S0")) {
                resources[0].addScorePoint(ScoreTreand, 5);
            } else if (w.equals("S1")) {
                resources[1].addScorePoint(ScoreTreand, 5);
            }
        }
        key = "4-3";
        if (workers.containsKey(key)) {
            String w = workers.get(key).get(0);
            if (w.equals("P0")) {
                resources[0].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("P1")) {
                resources[1].addScorePoint(ScoreTreand, 6);
            } else if (w.equals("A0")) {
                resources[0].addScorePoint(ScoreTreand, 5);
            } else if (w.equals("A1")) {
                resources[1].addScorePoint(ScoreTreand, 5);
            } else if (w.equals("S0")) {
                resources[0].addScorePoint(ScoreTreand, 4);
            } else if (w.equals("S1")) {
                resources[1].addScorePoint(ScoreTreand, 4);
            }
        }

        //スタートプレイヤーの決定
        key = "5-1";
        if (workers.containsKey(key)) {
            String worker = workers.get(key).get(0);
            if (worker.endsWith("0")) {

                resources[0].addMoney(3);
                resources[0].setStartPlayer(true);
                resources[1].setStartPlayer(false);
            } else if (worker.endsWith("1")) {

                resources[1].addMoney(3);
                resources[0].setStartPlayer(false);
                resources[1].setStartPlayer(true);
            }
        }

        //お金の獲得
        key = "5-2";
        if (workers.containsKey(key)) {
            String worker = workers.get(key).get(0);
            if (worker.endsWith("0")) {
                resources[0].addMoney(5);
            } else if (worker.endsWith("1")) {
                resources[1].addMoney(5);
            }
        }
        key = "5-3";
        // トレンド未実装
        if (workers.containsKey(key)) {
            String worker = workers.get(key).get(0);
            if (worker.endsWith("0")) {
                resources[0].addMoney(6);
            } else if (worker.endsWith("1")) {
                resources[1].addMoney(6);
            }
        }

        //コマの獲得
        key = "6-1";
        if (workers.containsKey(key)) {
            String worker = workers.get(key).get(0);
            if (worker.endsWith("0")) {
                resources[0].addNewStudent();
            } else if (worker.endsWith("1")) {
                resources[1].addNewStudent();
            }
        }
        key = "6-2";
        if (workers.containsKey(key)) {
            String worker = workers.get(key).get(0);
            if (worker.endsWith("0")) {
                resources[0].addNewAssistant();
            } else if (worker.endsWith("1")) {
                resources[1].addNewAssistant();
            }
        }
        return resources;
    }

    /**
     * 手を考えて打つ処理 継承＆オーバーライドで実装しないと使えません
     */
    protected abstract void think();

//    /**
//     * 評価関数 継承＆オーバーライドで実装しないと使えません 現在のボード状態、プレイする人の番号、アクションを入れると、そのアクション後のボードの評価が返る player0が有利な時はプラス、player1が有利な時はマイナスになるようにするといい感じ？
//     *
//     * @param game
//     * @param playerNum
//     * @param action
//     * @return 盤面の評価
//     */
//    protected abstract Double evaluateBoard(Game game, int playerNum, Action action);
    /**
     * 季節が変わった時に呼び出される関数 継承先でオーバーライドしてください
     */
    protected abstract void seasonChanged();

}
