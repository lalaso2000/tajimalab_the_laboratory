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
public class TajimaAI extends LaboAI{
    // サーバーとのコネクター
    private ServerConnecter connecter;
    // GUI
    private ClientGUI gui;
    

    public TajimaAI(Game game) {
        super(game);
    }

    @Override
    public void getNewMessage(String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setConnecter(ServerConnecter connecter) {
        this.connecter = connecter;
        this.connecter.addMessageRecever(this);
    }

    @Override
    public void setOutputInterface(MessageRecevable mr) {
        this.gui = (ClientGUI) mr;
    }

    @Override
    public void reciveMessage(String text) {
        if(text.startsWith("207") || text.startsWith("206")){
            this.sendMessage("210 CONFPRM");
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
    
    @Override
    public void addMessage(String text) {
        gui.addMessage(text);
    }
    
    
}
