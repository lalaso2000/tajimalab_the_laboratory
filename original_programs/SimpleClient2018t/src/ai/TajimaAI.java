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

    // サーバーとのコネクター
    private ServerConnecter connecter;
    // GUI
    private ClientGUI gui;

    // デバック用カウンタ
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
     * @param text
     */
    @Override
    public void reciveMessage(String text) {
        String messageNum = text.substring(0, 3);

        switch (messageNum) {
            case "100":
                // サーバーが応答したら自分の名前を送る
                this.sendMessage("101 NAME " + this.myName);
                break;
            case "102":
                // 自分の番号をチェック
                this.myNumber = Integer.parseInt(text.substring(13));
                break;
            case "204":
                // 自分の番が回ってきた時に考え始める
                this.thinkStart();
                // コマをおく
                this.putWorker();
                // 考え終わる
                this.stopThinking();
                break;
            case "206":
            case "207":
                // 季節の変わり目と相手が打った時にボード状態を確認
                this.sendMessage("210 CONFPRM");
                break;
        }
    }

    /**
     * コマをおくメソッド ここに評価関数とか突っ込む
     */
    private void putWorker() {
        // 永遠ゼミに置き続ける
        if (this.count % 2 == 0) {
            this.sendMessage("205 PLAY " + this.myNumber + " P 1-1");
        } else {
            this.sendMessage("205 PLAY " + this.myNumber + "S 1-1");
        }
        this.count++;
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

}
