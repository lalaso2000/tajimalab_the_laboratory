/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import gameElements.Board;
import gameElements.Game;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author koji
 */
public class ClientConnectionThread implements Runnable {
    private Socket connectedSocket;
    private String ID;
    private int PlayerID = -1;
    private BufferedReader reader;
    private PrintWriter writer;
    private final ServerThread HostServer;
    
    public ClientConnectionThread(Socket csoc,ServerThread server){
        this.connectedSocket = csoc;
        this.HostServer = server;
        this.ID = csoc.getInetAddress().getHostAddress();
    }
    
    public void createStream() throws IOException{
        this.reader = new BufferedReader(new InputStreamReader(this.connectedSocket.getInputStream(),"UTF-8"));
        this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.connectedSocket.getOutputStream(),"UTF-8")));
        
        Thread msgwait = new Thread(this);
        msgwait.start();
        this.HostServer.getGameBoard().printMessage(this.ID+"が接続しました。");
        this.sendMessage("100 HELLO");
    }
    
    public String getAddress(){
        return this.ID;
    }
    
    /** クライアントへのメッセージ送信 */
    public void sendMessage(String message){
        if(this.writer != null){
            this.writer.println(message);
            this.writer.flush();
        }
    }
    
    private Pattern MSGPTN = Pattern.compile("([0-9]+) (.*)");
    private Pattern NAMEMSGPTN = Pattern.compile("101 NAME (.*)");
    private Pattern CONFPRAMMSGPTN = Pattern.compile("210 CONFPRM");
    private Pattern PLAYMSGPTN = Pattern.compile("205 PLAY ([01]) ([PAS]) (([1-6])-([1-3])).*");
    private Pattern PLAYMSGPTN_TREND = Pattern.compile("205 PLAY ([01]) ([PAS]) (([1-6])-([1-3])) (T[1-3])");
    /** クライアントからのメッセ―ジ到着 */
    public void getMessage(String message){
        message = message.trim();
        //this.mainFiled.addMessage(this.ID+":"+message);
        synchronized(this.HostServer){
            //終了処理
            if(message.toUpperCase().equals("203 EXIT")){
                sendMessage("200 OK");
                try {
                    this.connectedSocket.close();
                    this.HostServer.removeClientThread(this);
                } catch (IOException ex) {
                    this.HostServer.getGameBoard().printMessage(this.ID+"が切断時にエラーが発生しました");
                    this.HostServer.removeClientThread(this);
                }
                return;
            }

            //メッセージ解析
            Matcher mc = MSGPTN.matcher(message);
            if(mc.matches()){
                int num = Integer.parseInt(mc.group(1));
                if(this.HostServer.getGameBoard().getGameState() == Game.STATE_WAIT_PLAYER_CONNECTION){
                    if(num == 101){
                        //新規ユーザの接続
                        Matcher nmc = NAMEMSGPTN.matcher(message);
                        if(nmc.matches()){
                            String name = nmc.group(1);
                            if(this.PlayerID == -1){
                                this.PlayerID = this.HostServer.getGameBoard().setPlayerName(name);
                                if(this.PlayerID == 0 || this.PlayerID == 1){
                                    sendMessage("102 PLAYERID "+ this.PlayerID);
                                    this.HostServer.played(this);
                                } else{
                                    sendMessage("301 ROOM IS FULL");
                                }
                            } else {
                                sendMessage("302 ALL REDAY REGSTERD");
                            }
                        } else {
                            sendMessage("300 MESSAGE SYNTAX ERROR");
                        }
                    } else {
                        this.sendMessage("300 MESSAGE SYNTAX ERROR");
                    }
                } else if(this.HostServer.getGameBoard().getGameState() == Game.STATE_WAIT_PLAYER_PLAY){
                    if(num == 210){
                        //ボード状態の取得
                        this.sendBoardInformation();
                    } else if(num == 205){
                        //手を打つメッセージ
                        if (this.HostServer.getGameBoard().getCurrentPlayer() != this.PlayerID){
                            //自分の手ではない
                            this.sendMessage("402 NOT YOURE TURN"); 
                        } else {
                            Matcher nmc = PLAYMSGPTN.matcher(message);
                            if(nmc.matches()){
                                //playメッセージの解析
                                //プレイヤー番号はつけてみたけれど実は認識不要
                                String workerType = nmc.group(2);
                                String place = nmc.group(3);
                                int placeType = Integer.parseInt(nmc.group(4));
                                int placeNumber = Integer.parseInt(nmc.group(5));
                                
                                String trend = "";
                                //トレンド移動の手の場合には値が設定されているか確認
                                if(place.equals("5-3")){
                                    Matcher nmc2 = PLAYMSGPTN_TREND.matcher(message);
                                    if(nmc2.matches()){
                                        trend = nmc2.group(6);
                                    } else {
                                        this.sendMessage("400 MESSAGE SYNTAX ERROR");
                                        this.sendMessage("204 DOPLAY");
                                        return;
                                    }
                                }

                                //その手が打てるか確認
                                boolean playable = this.HostServer.getGameBoard().canPutWorker(this.PlayerID,place,workerType);
                                
                                if(playable == true){
                                    boolean played = false;
                                    played = this.HostServer.getGameBoard().play(this.PlayerID, place, workerType);
                                    if(place.equals("5-3")){
                                        this.HostServer.getGameBoard().setTreand(trend);
                                    }

                                    //手を打つ処理をサーバに依頼
                                    if(played){
                                        //手が打てたらOKを返す
                                        this.sendMessage("200 OK");
                                        this.HostServer.played(this, PlayerID, workerType, placeType, placeNumber);
                                        
                                        //シーズンの終了判定
                                        if(this.HostServer.getGameBoard().getGameState() == Game.STATE_SEASON_END){
                                            try {
                                                //3秒間まつ
                                                Thread.sleep(3000);
                                            } catch (InterruptedException ex) {
                                                Logger.getLogger(ClientConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                            this.HostServer.SeasonChange();
                                            this.HostServer.getGameBoard().changeNewSeason();
                                            this.HostServer.sendDoPlayToCurrentPlayer();
                                        }
                                        //ゲームの終了判定
                                        if(this.HostServer.getGameBoard().getGameState() == Game.STATE_GAME_END){
                                            this.HostServer.gameEnd(this, PlayerID);
                                        }
                                        return;
                                    } else {
                                        //何らかのエラーで手が打てなかった場合は、手を打つように要求
                                        this.sendMessage("401 WORKER COULD NOT PUT"); 
                                        this.sendMessage("204 DOPLAY");
                                    }
                                } else {
                                    //手が打てない場合はエラーを返して再度playするように要求
                                   this.sendMessage("401 WORKER COULD NOT PUT"); 
                                   this.sendMessage("204 DOPLAY");
                                }
                            } else {
                               this.sendMessage("400 MESSAGE SYNTAX ERROR");
                               this.sendMessage("204 DOPLAY");
                            }
                        }
                    } else {
                        sendMessage("400 MESSAGE SYNTAX ERROR");
                    }
                } else if(this.HostServer.getGameBoard().getGameState() == Game.STATE_GAME_END){
                    if(num == 210){
                        //ボード状態の取得と通知
                        this.sendBoardInformation();
                    } else {
                    }
                }
            } else {
                sendMessage("300 MESSAGE SYNTAX ERROR");
            }
        }
    }
    
    private void sendBoardInformation(){
        sendMessage("201 MULTILINE");

        //リソース通知
        sendMessage("211 " + this.HostServer.getGameBoard().getResourcesMessageOf(0));
        sendMessage("211 " + this.HostServer.getGameBoard().getResourcesMessageOf(1));
        //ボードのコマ通知
        for(String place:Board.PLACE_NAMES){
            if(this.HostServer.getGameBoard().getWorkerNameOf(place) != null){
                for(String worker : this.HostServer.getGameBoard().getWorkerNameOf(place)){
                    sendMessage("212 BOARD " + place + " "+ worker);
                }
            }
        }
        //季節通知
        sendMessage("213 SEASON "+ this.HostServer.getGameBoard().getSeason());
        //トレンド通知
        sendMessage("214 TREND "+ this.HostServer.getGameBoard().getTrend());
        //得点通知
        for(int i=1;i<=3;i++){
            sendMessage("215 SCORE T"+ i + " " + this.HostServer.getGameBoard().getScoreOf("T"+i,0) + " "+ this.HostServer.getGameBoard().getScoreOf("T"+i,1));
        }
        //スタートプレイヤー通知
        sendMessage("216 STARTPLAYER "+ this.HostServer.getGameBoard().getStartPlayer());
        
        sendMessage("202 LINEEND");
    }
    
    @Override
    public void run() {
        String mssage;
        try {
            while((mssage = this.reader.readLine())!= null){
                this.getMessage(mssage);
            }
            this.HostServer.removeClientThread(this);
            this.HostServer.getGameBoard().printMessage(this.ID+"が切断しました");
        } catch (IOException ex) {
            this.HostServer.getGameBoard().printMessage(this.ID+"が切断しました");
            this.HostServer.removeClientThread(this);
        }
    }

    /** プレイコマンドを送る */
    public void doplay() {
        this.HostServer.getGameBoard().TimerStart(PlayerID);
        this.sendMessage("204 DOPLAY");
    }

    public int getPlayerID() {
        return this.PlayerID;
    }

    public void closeConnection() throws IOException {
        this.connectedSocket.close();
    }

    public void sendSeasonChangeMessage() {
        this.sendMessage("207 NEXT SEASON");
    }

    public void sendSeasonChangeMessage(int playerID) {
        this.sendMessage("208 AWARD "+ playerID);
    }
   
}
