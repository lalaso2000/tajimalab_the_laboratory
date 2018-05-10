/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import gameElements.Game;
import gui.ClientGUI;
import gui.MessageRecevable;
import java.awt.Color;
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

    // サーバーとのコネクター
    private ServerConnecter connecter;
    // GUI
    private ClientGUI gui;

    // 手数を数える
    private int count = 0;

    public TajimaAI(Game game) {
        super(game);
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
                this.gameBoard.changeNewSeason();
                break;
            case "214":
                // トレンドを更新する
                this.setTrend(msg);
                break;
        }
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
     * コマを置くメソッド(トレンド無し版)
     *
     * @param worker [PAS]
     * @param action 1-1|[2-4]-[123]|[56]-[12]
     */
    private void putWorker(String worker, String action) {
        if (this.gameBoard.play(this.myNumber, action, worker, false)) {
            this.sendMessage("205 PLAY " + this.myNumber + " " + worker + " " + action);
            this.gameBoard.play(this.myNumber, action, worker);
            this.count++;
        }
    }

    /**
     * コマを置くメソッド(トレンドあり)
     *
     * @param worker [PA]
     * @param action 5-3
     * @param trend T[1-3]
     */
    private void putWorker(String worker, String action, String trend) {
        if (this.gameBoard.play(this.myNumber, action, worker, false)) {
            this.sendMessage("205 PLAY " + this.myNumber + " " + worker + " " + action + " " + trend);
            this.gameBoard.play(this.myNumber, action, worker);
            this.gameBoard.setTreand(trend);
            this.count++;
        }
    }

    /**
     * 相手がコマをおいたとき
     *
     * @param msg サーバーからのメッセージ
     */
    private void enemyPlay(String msg) {
        String worker = msg.substring(13, 14);
        String action = msg.substring(15, 18);
        this.gameBoard.play(this.enemyNumber, action, worker);
        if (action.equals("5-3")) {
            // 5-3打たれた時はトレンドを確認
            this.sendMessage("210 CONFPRM");
        }
    }

    /**
     * トレンドをセットする
     *
     * @param msg
     */
    private void setTrend(String msg) {
        String trendStr = msg.substring(10);
        this.gameBoard.setTreand(trendStr);
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
     * ここで考える
     */
    private void think() {
        // 永遠ゼミに置き続ける
        if (count % 2 == 0) {
            this.putWorker("P", "1-1");
        } else {
            this.putWorker("S", "1-1");
        }
    }

}
