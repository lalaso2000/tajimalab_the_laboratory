/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Board;
import gameElements.Game;
import gameElements.GameResources;
import gui.ClientGUI;
import gui.MessageRecevable;
import java.awt.Color;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import network.ServerConnecter;

/**
 *
 * @author niwatakumi
 */
public class TajimaAI extends LaboAI {

    // 自プレイヤーの名前
    private String myName = "TajimaAI";
    // 自プレイヤーのプレイヤー番号
    private int myNumber;
    // 相手プレイヤーのプレイヤー番号
    private int enemyNumber;

    // 思考部分
    private Thinker thinker;
    
    // サーバーとのコネクター
    private ServerConnecter connecter;
    // GUI
    private ClientGUI gui;

    // 手数を数える
    private int count = 0;

    // 状態を更新しても良いか
    private boolean canChangeSeason = true;
    // 季節更新？
    private boolean changeSeasonFlag = false;

    /**
     * コンストラクタ
     *
     * @param game
     */
    public TajimaAI(Game game) {
        super(game);
        this.thinker = new Thinker(Thinker.MONEY_AND_RESERCH_PRIORITY);
    }

    /**
     * *
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
     * GUIに接続するメソッド
     *
     * @param mr
     */
    @Override
    public void setOutputInterface(MessageRecevable mr) {
        // GUIに接続する
        this.gui = (ClientGUI) mr;
    }

    /**
     * 名前を送るメソッド
     */
    private void sendName() {
        // 名前を送る
        this.sendMessage("101 NAME " + this.myName);
    }

    /**
     * 自分のプレイヤー番号を確認する
     *
     * @param msg サーバーからのメッセージ
     */
    private void checkNumber(String msg) {
        // 番号を確認する
        this.myNumber = Integer.parseInt(msg.substring(13));
        if (this.myNumber == 0) {
            this.enemyNumber = 1;
        } else {
            this.enemyNumber = 0;
        }
    }

    /**
     * コマを置くメソッド
     *
     * @param action
     */
    private void putWorker(Action action) {
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
            this.count++;
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
            this.count++;
        } else {
            System.err.println("Put Error!!");
        }
    }

    /**
     * 相手がコマをおいたとき
     *
     * @param msg サーバーからのメッセージ
     */
    private void enemyPlay(String msg) {
        String worker = msg.substring(13, 14);
        String place = msg.substring(15, 18);
        this.gameBoard.play(this.enemyNumber, place, worker);
        if (place.equals("5-3")) {
            // 5-3打たれた時はトレンドを確認
            this.checkTrend();
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
     * @param msg
     */
    private void setTrend(String msg) {
        String trendStr = msg.substring(10);
        this.gameBoard.setTreand(trendStr);
        this.canChangeSeason = true;
        this.changeSeason();
    }

    /**
     * 季節を更新する
     */
    private void changeSeason() {
        if (this.canChangeSeason && this.changeSeasonFlag) {
            this.gameBoard.changeNewSeason();
            String log = this.gameBoard.getBoardInformation();
            this.addMessage(log);
            log = this.gameBoard.getResourceInformation();
            this.addMessage(log);
            this.changeSeasonFlag = false;
            
            // デバック用
            if(this.gameBoard.getSeason().equals("5a")){
                // 5aまで来たらモードチェンジ
                this.thinker.setMode(Thinker.SCORE_PRIORITY);
            }
        }
    }

    /**
     * 通信先にメッセージを送信する。サーバにつながっていない場合は送らない
     */
    public void sendMessage(String sendText) {
        //属性情報を作成
        SimpleAttributeSet attribute = new SimpleAttributeSet();
        //属性情報の文字色に赤を設定
        attribute.addAttribute(StyleConstants.Foreground, Color.RED);

        //サーバーへ送信
        if (this.connecter.canWrite()) {
            connecter.sendMessage(sendText);
            gui.addMessage("[send]" + sendText + "\n");
        } else {
            gui.addMessage("(送信失敗)" + sendText + "\n");
        }

    }

    /**
     * ログを出力
     *
     * @param text
     */
    @Override
    public void addMessage(String text) {
        gui.addMessage(text);
    }

    public String getMyName() {
        return myName;
    }

    /**
     * メッセージを受け取った時のメソッド
     *
     * @param msg
     */
    @Override
    public void reciveMessage(String msg) {
        String messageNum = msg.substring(0, 3);

        switch (messageNum) {
            case "100":
                // サーバーが応答したら自分の名前を送る
                this.sendName();
                break;
            case "102":
                // 自分の番号をチェック
                this.checkNumber(msg);
                // 仮想ゲームを始める
                this.gameBoard.startGame();
                break;
            case "204":
                // 自分の番が回ってきた時に考え始める
                this.thinkStart();
                // コマをおく
                this.think();
                // 考え終わる
                this.stopThinking();
                break;
            case "206":
                // 相手が打ったときはその手を自分の仮想ボードでも打つ
                this.enemyPlay(msg);
                break;
            case "207":
                // 季節が変わったらしい時は自分の仮想ゲームでも更新する
                this.changeSeasonFlag = true;
                this.changeSeason();
                break;
            case "214":
                // トレンドを更新する
                this.setTrend(msg);
                break;
        }
    }
    
    
    /**
     * ここで考える
     */
    private void think() {
        // とりあえず全探索＆最適手を探す
        //this.test = this.gameBoard.clone();
        Action bestAction = new Action("P", "1-1");
        Double bestEva = Double.NEGATIVE_INFINITY;
        Double eva = null;
        for (int j = 0; j < Board.PLACE_NAMES.length; j++) {
            // 全部の場所ループ
            String p = Board.PLACE_NAMES[j];
            for (int i = 0; i < GameResources.WORKER_NAMES.length; i++) {
                // 全部のワーカーループ
                String w = GameResources.WORKER_NAMES[i];
                Action a = new Action(w, p);
                eva = this.thinker.evaluateBoard(gameBoard, myNumber, a);
                // 評価良いの見つけたら
                if(eva != null && eva > bestEva){
                    // 更新
                    bestEva = eva;
                    bestAction = a;
                }
            }
        }
        
//      // 永遠ゼミに置き続ける
//        if (count % 2 == 0) {
//            a = new Action("P", "1-1");
//            //System.out.println(this.test.play(myNumber, "2-1", "P"));
//        } else {
//            a = new Action("S", "1-1");
//            //this.test.play(myNumber, "2-1", "S");
//        }
        this.putWorker(bestAction);
    }

}
