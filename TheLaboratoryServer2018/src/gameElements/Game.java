/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * ゲームの進行状況を管理するクラス
 * 手を打てるプレイヤーや得点を管理する
 * @author koji
 */
public class Game extends Observable{
    public static final int STATE_WAIT_PLAYER_CONNECTION = 0;
    public static final int STATE_WAIT_PLAYER_PLAY = 1;
    public static final int STATE_SEASON_END = 2;
    public static final int STATE_GAME_END = 4;
    //最大思考時間
    public static long maxThinkingTime = 1000*60*5;
    
    private int CurrentPlayer;
    private Board gameBoard;
    private String[] PlayerName;
    private int gameState;
    private GameResources[] gameResource;
    private int currentSeason;
    public static String[] SEASON_NAMES = {"1a","1b","2a","2b","3a","3b","4a","4b","5a","5b","6a","6b"};
    private int trendID;
    public static String[] TREAND_ID_LIST = {"T1","T2","T3"};
    private int currentStartPlayer = 0;
    
    /** タイマー */
    private TimerThread timerThread;
    
    public Game(){
        this.init();
    }
    
    /** ゲームの状況を取得する */
    public int getGameState(){
        return this.gameState;
    }
    
    
    public String[] getPlayerName(){
        return this.PlayerName;
    }
    
    public int[] getScore(){
        int[] score = new int[2];
        score[0] = this.gameResource[0].getTotalScore();
        score[1] = this.gameResource[0].getTotalScore();
        return score;
    }
    
    public int getCurrentPlayer(){
        return this.CurrentPlayer;
    }
    
    /** 時間計測開始 */
    public void TimerStart(int PlayerID){
        this.timerThread.StartTimeCount(PlayerID);
    }
    /** 時間計測終了 */
    public void TimerStop(int PlayerID){
        this.timerThread.StopTimeCount(PlayerID);
    }

    public void setObserver(Observer gui) {
        this.addObserver(gui);
    }
    
    public void setTimerObserver(Observer gui) {
        this.timerThread.addObserver(gui);
    }
    
    //以下はボードの状態を変更するメソッドのため、呼び出し時はObserverに必ず通知すること
    
    /** ボードなどの初期化 */
    private void init(){
        this.CurrentPlayer = 0;
        this.gameBoard = new Board();
        
        this.gameResource = new GameResources[2];
        this.gameResource[0] = new GameResources();
        this.gameResource[0].setStartPlayer(true);
        this.gameResource[1] = new GameResources();
        
        this.PlayerName = new String[2];
        this.PlayerName[0] = null;
        this.PlayerName[1] = null;
        
        this.gameState = STATE_WAIT_PLAYER_CONNECTION;
        
        this.currentSeason = 0;
        this.trendID = -1;
        
        this.timerThread = new TimerThread();
        new Thread(this.timerThread).start();
        
        this.setChanged();
        this.notifyObservers();
    }

    /***
     * 手が打てるか事前に検証するメソッド。実際にはPLAYとやることは変わらない
     * @param PlayerID
     * @param workerType
     * @param place
     * @return 
     */
    public boolean canPutWorker(int PlayerID, String place, String workerType) {
        return this.play(PlayerID, place, workerType ,false);
    }

    
    /***
     * 実際に手を打つメソッド  
     * @param player
     * @param place
     * @param typeOfWorker
     * @return 
     */
    public boolean play(int player,String place,String typeOfWorker){
        return this.play(player, place, typeOfWorker,true);
    }
    /***
     * 実際に手を打つメソッドで最後の引数により打てるかの調査なのかが決まる
     * @param player
     * @param place
     * @param typeOfWorker
     * @param putmode trueの場合は実際に手を打つ
     * @return 
     */
    public boolean play(int player,String place,String typeOfWorker,boolean putmode){

        if(this.gameState != STATE_WAIT_PLAYER_PLAY){
            return false;
        }
        if(this.CurrentPlayer != player){
            return false;
        }
        if(!this.gameResource[player].hasWorkerOf(typeOfWorker)){
            return false;
        }
        if(!this.gameBoard.canPutWorker(player, place)){
            return false;
        }
        
        if(place.equals("1-1")){
            if(putmode){
                this.gameBoard.putWorker(player, place, typeOfWorker);
                this.gameResource[player].putWorker(typeOfWorker);
                this.changePlayer();
                this.setChanged();
                this.notifyObservers();
            }
            return true;
        }
        //リソースが十分かどうかを確認
        if(place.startsWith("2")){
            if(this.gameResource[player].getCurrentMoney() >= 2){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].addMoney(-2);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        if(place.equals("3-1")){
            if(this.gameResource[player].getCurrentResrchPoint() >= 2){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].addReserchPoint(-2);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        if(place.equals("3-2")){
            if(this.gameResource[player].getCurrentResrchPoint() >= 4 && this.gameResource[player].getCurrentMoney() >= 1){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].addMoney(-1);
                    this.gameResource[player].addReserchPoint(-4);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        if(place.equals("3-3")){
            if(this.gameResource[player].getCurrentResrchPoint() >= 8 && this.gameResource[player].getCurrentMoney() >= 1){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].addMoney(-1);
                    this.gameResource[player].addReserchPoint(-8);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        if(place.startsWith("4")){
            if(this.gameResource[player].getCurrentResrchPoint() >= 8 && this.gameResource[player].getCurrentMoney() >= 1){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].addMoney(-1);
                    this.gameResource[player].addReserchPoint(-8);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }

        if(place.equals("5-1")){
            if(typeOfWorker.equals("P") || typeOfWorker.equals("A")){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }

        if(place.equals("5-2")){
            if(this.gameResource[player].getCurrentResrchPoint() >= 1){
                if(typeOfWorker.equals("P") || typeOfWorker.equals("A")){
                    if(putmode){
                        this.gameResource[player].addReserchPoint(-1);
                        this.gameBoard.putWorker(player, place, typeOfWorker);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
                
        }

        if(place.equals("5-3")){
            if(this.gameResource[player].getCurrentResrchPoint() >= 3){
                if(typeOfWorker.equals("P") || typeOfWorker.equals("A")){
                    if(putmode){
                        this.gameResource[player].addReserchPoint(-3);
                        this.gameBoard.putWorker(player, place, typeOfWorker);
                        this.gameResource[player].putWorker(typeOfWorker);
                        this.changePlayer();
                        this.setChanged();
                        this.notifyObservers();
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
                
        }

        if(place.equals("6-1")){
            if(typeOfWorker.equals("P") || typeOfWorker.equals("A")){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        if(place.equals("6-2")){
            if(typeOfWorker.equals("P") || this.gameResource[player].getTotalScore() >= 10){
                if(putmode){
                    this.gameBoard.putWorker(player, place, typeOfWorker);
                    this.gameResource[player].putWorker(typeOfWorker);
                    this.changePlayer();
                    this.setChanged();
                    this.notifyObservers();
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
   }
    
    /** タイムアウトが発生した場合 */
    public void pass(int playerID) {
        /* 強制的にゼミに打たれる？ */
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        this.setChanged();
//        this.notifyObservers();
    }
            
    /** 通常の手番切換え */
    public void changePlayer(){
        this.timerThread.StopTimeCount(this.CurrentPlayer);

        if(this.gameResource[(this.CurrentPlayer+1)%2].hasWorker()){
            //相手に手がうつせる場合
            this.CurrentPlayer = (this.CurrentPlayer+1)%2;
            this.timerThread.StartTimeCount(this.CurrentPlayer);
            this.setChanged();
            this.notifyObservers();
            return;
        } else {
            //相手がもう手が打てない場合
            if(this.gameResource[this.CurrentPlayer].hasWorker()){
                //自分がまだ打てるんであれば、そのまま自分の手番で継続
                this.timerThread.StartTimeCount(this.CurrentPlayer);
                this.setChanged();
                this.notifyObservers();
                return;
            } else {
                //互いに手が打てないのであれば、季節を進める
                this.CurrentPlayer = -1;
                this.gameState = STATE_SEASON_END;
                this.changeNewSeason();
                this.setChanged();
                this.notifyObservers();
                return;
            }
        }
    }
    
    /** ボードそのもののメソッドを呼び出すための取得 */
    public Board getBoard(){
        return this.gameBoard;
    }
    
    public int setPlayerName(String name){
        if(this.gameState == STATE_WAIT_PLAYER_CONNECTION){
            if(this.PlayerName[0] == null){
                this.setPlayerName(0, name);
                return 0;
            } else if(this.PlayerName[1] == null){
                this.setPlayerName(1, name);
                return 1;
            }
        }
        return -1;
    }
    
    public void setPlayerName(int player,String name){
        if(player>=0 && player < 2){
            this.PlayerName[player] = name;
        }
        if(this.PlayerName[0] != null && this.PlayerName[1] != null){
            this.gameState = STATE_WAIT_PLAYER_PLAY;
        }
        this.setChanged();
        this.notifyObservers();
    }
    
    /***
     * RESOURCES [01] P[01] A[01] S[0-9]+ M[1-9]*[0-9]+ R[1-9]*[0-9]+ D[0-9]+
     * プレイヤーID（SP）コマの種類と残り個数（ SP 区切り）Mお金（SP）R研究力（SP） D負債
     * を構造もつメッセージを返す
     * @param playerID 0または1
     * @return 
     */
    public String getResourcesMessageOf(int playerID) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("RESOURCES").append(" ");
        sbuf.append(playerID).append(" ");
        sbuf.append("P").append(this.gameResource[playerID].getNumberofUseableWorkers("P")).append(" ");
        sbuf.append("A").append(this.gameResource[playerID].getNumberofUseableWorkers("A")).append(" ");
        sbuf.append("S").append(this.gameResource[playerID].getNumberofUseableWorkers("S")).append(" ");
        sbuf.append("M").append(this.gameResource[playerID].getCurrentMoney()).append(" ");
        sbuf.append("R").append(this.gameResource[playerID].getCurrentResrchPoint()).append(" ");
        sbuf.append("D").append(this.gameResource[playerID].getDebt());
        return sbuf.toString();
    }

    public ArrayList<String> getWorkerNameOf(String place) {
        return this.gameBoard.getWorkersOnBoard().get(place);
    }

    public String getSeason() {
        return Game.SEASON_NAMES[this.currentSeason];
    }

    public String getTrend() {
        if(this.trendID < 0){
            return "T0";
        } else if(this.trendID < 3){
            return Game.TREAND_ID_LIST[this.trendID];
        } else {
            return "T0";
        }
    }

    public int getScoreOf(String trend, int playerID) {
        return this.gameResource[playerID].getSocreOf(trend);
    }

    /***
     * 季節の進行
     */
    private void changeNewSeason() {
        if(this.gameState == STATE_SEASON_END){
            HashMap<String,ArrayList<String>> workers = this.getBoard().getWorkersOnBoard();
            //ゼミによる研究ポイントの獲得
            ArrayList<String> seminorwokers = workers.get("1-1");
            if(seminorwokers != null){
                int PACount = 0;
                int SCount[] = {0,0};
                for(String w:seminorwokers){
                    if(w.equals("P0")){
                        PACount++;
                        this.gameResource[0].addReserchPoint(2);
                    } else if(w.equals("P1")){
                        PACount++;
                        this.gameResource[1].addReserchPoint(2);
                    } else if(w.equals("A0")){
                        PACount++;
                        this.gameResource[0].addReserchPoint(3);
                    } else if(w.equals("A1")){
                        PACount++;
                        this.gameResource[1].addReserchPoint(3);
                    } else if(w.equals("S0")){
                        SCount[0]++;
                    } else if(w.equals("S1")){
                        SCount[1]++;
                    }
                }
                this.gameResource[0].addReserchPoint((int)((SCount[0]+SCount[1])/2)*PACount);
                this.gameResource[1].addReserchPoint((int)((SCount[0]+SCount[1])/2)*PACount);
            }
            
            //実験による研究ポイントの獲得
            String[] keys = {"2-1","2-2","2-3"};
            int[] points = {3,4,5};
            for(int i=0;i<keys.length;i++){
                String key = keys[i];
                if(workers.containsKey(key)){
                    String worker = workers.get(key).get(0);
                    if(worker.endsWith("0")){
                        this.gameResource[0].addReserchPoint(points[i]);
                    } else if(worker.endsWith("1")){
                        this.gameResource[1].addReserchPoint(points[i]);
                    }
                }
            }

            //発表による業績の獲得
            int ScoreTreand = 0;
            if(this.currentSeason == 0 || this.currentSeason == 1 || this.currentSeason == 6 || this.currentSeason == 7){
                ScoreTreand = 0;
            } else if(this.currentSeason == 2 || this.currentSeason == 3 || this.currentSeason == 8 || this.currentSeason == 9){
                ScoreTreand = 1;
            } else if(this.currentSeason == 4 || this.currentSeason == 5 || this.currentSeason == 10 || this.currentSeason == 11){
                ScoreTreand = 2;
            }
            String key;
            key = "3-1";
            if(workers.containsKey(key)){
                String w = workers.get(key).get(0);
                if(w.equals("P0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,1);
                } else if(w.equals("P1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,1);
                } else if(w.equals("A0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,1);
                } else if(w.equals("A1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,1);
                } else if(w.equals("S0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,2);
                } else if(w.equals("S1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,2);
                }                    
            }
            key = "3-2";
            if(workers.containsKey(key)){
                String w = workers.get(key).get(0);
                if(w.equals("P0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,3);
                } else if(w.equals("P1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,3);
                } else if(w.equals("A0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,4);
                } else if(w.equals("A1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,4);
                } else if(w.equals("S0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,4);
                } else if(w.equals("S1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,4);
                }                    
            }
            key = "3-3";
            if(workers.containsKey(key)){
                String w = workers.get(key).get(0);
                if(w.equals("P0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,7);
                } else if(w.equals("P1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,7);
                } else if(w.equals("A0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,6);
                } else if(w.equals("A1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,6);
                } else if(w.equals("S0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,5);
                } else if(w.equals("S1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,5);
                }                    
            }
            
            //論文による業績の獲得
            key = "4-1";
            if(workers.containsKey(key)){
                String w = workers.get(key).get(0);
                if(w.equals("P0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,8);
                } else if(w.equals("P1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,8);
                } else if(w.equals("A0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,7);
                } else if(w.equals("A1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,7);
                } else if(w.equals("S0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,6);
                } else if(w.equals("S1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,6);
                }                    
            }
            key = "4-2";
            if(workers.containsKey(key)){
                String w = workers.get(key).get(0);
                if(w.equals("P0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,7);
                } else if(w.equals("P1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,7);
                } else if(w.equals("A0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,6);
                } else if(w.equals("A1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,6);
                } else if(w.equals("S0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,5);
                } else if(w.equals("S1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,5);
                }                    
            }
            key = "4-3";
            if(workers.containsKey(key)){
                String w = workers.get(key).get(0);
                if(w.equals("P0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,6);
                } else if(w.equals("P1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,6);
                } else if(w.equals("A0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,5);
                } else if(w.equals("A1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,5);
                } else if(w.equals("S0")){
                    this.gameResource[0].addScorePoint(ScoreTreand,4);
                } else if(w.equals("S1")){
                    this.gameResource[1].addScorePoint(ScoreTreand,4);
                }                    
            }

            //スタートプレイヤーの決定
            key = "5-1";
            if(workers.containsKey(key)){
                String worker = workers.get(key).get(0);
                if(worker.endsWith("0")){
                    this.currentStartPlayer = 0;
                    this.gameResource[0].setStartPlayer(true);
                    this.gameResource[1].setStartPlayer(false);
                } else if(worker.endsWith("1")){
                    this.currentStartPlayer = 1;
                    this.gameResource[0].setStartPlayer(false);
                    this.gameResource[1].setStartPlayer(true);
                }
            }
            this.CurrentPlayer = this.currentStartPlayer;

            //お金の獲得
            key = "5-2";
            if(workers.containsKey(key)){
                String worker = workers.get(key).get(0);
                if(worker.endsWith("0")){
                    this.gameResource[0].addMoney(5);
                } else if(worker.endsWith("1")){
                    this.gameResource[1].addMoney(5);
                }
            }
            key = "5-3";
            if(workers.containsKey(key)){
                String worker = workers.get(key).get(0);
                if(worker.endsWith("0")){
                    this.gameResource[0].addMoney(6);
                } else if(worker.endsWith("1")){
                    this.gameResource[1].addMoney(5);
                }
            }
            //トレンドを動かす処理はPLAY時に指定する

            //コマの獲得
            key = "6-1";
            if(workers.containsKey(key)){
                String worker = workers.get(key).get(0);
                if(worker.endsWith("0")){
                    this.gameResource[0].addNewStudent();
                } else if(worker.endsWith("1")){
                    this.gameResource[1].addNewStudent();
                }
            }
            key = "6-2";
            if(workers.containsKey(key)){
                String worker = workers.get(key).get(0);
                if(worker.endsWith("0")){
                    this.gameResource[0].addNewAssistant();
                } else if(worker.endsWith("1")){
                    this.gameResource[1].addNewAssistant();
                }
            }
            
            //ボードのコマを全部戻す
            this.gameResource[0].returnAllWorkers();
            this.gameResource[1].returnAllWorkers();
            this.getBoard().returnAllWorkers();
            
            if(this.currentSeason == 11) {
                //最後の季節の終了
                this.gameState = STATE_GAME_END;
            } else if(this.currentSeason % 2 == 1){
                //奇数は表彰のある季節なので表彰する
                int addmoney = 5;
                if(this.currentSeason == 1 || this.currentSeason == 7){
                    if(this.getTrend().equals("T1")) { addmoney += 3; }
                    if(this.gameResource[0].getSocreOf("T1") == this.gameResource[1].getSocreOf("T1")){
                        this.gameResource[0].addMoney(addmoney);
                        this.gameResource[1].addMoney(addmoney);
                    } else if(this.gameResource[0].getSocreOf("T1") > this.gameResource[1].getSocreOf("T1")){
                        this.gameResource[0].addMoney(addmoney);
                    } else {
                        this.gameResource[1].addMoney(addmoney);
                    }
                } else if(this.currentSeason == 3 || this.currentSeason == 9){
                    if(this.getTrend().equals("T2")) { addmoney += 3; }
                    if(this.gameResource[0].getSocreOf("T2") == this.gameResource[1].getSocreOf("T2")){
                        this.gameResource[0].addMoney(addmoney);
                        this.gameResource[1].addMoney(addmoney);
                    } else if(this.gameResource[0].getSocreOf("T2") > this.gameResource[1].getSocreOf("T2")){
                        this.gameResource[0].addMoney(addmoney);
                    } else {
                        this.gameResource[1].addMoney(addmoney);
                    }
                } else if(this.currentSeason == 5){
                    if(this.getTrend().equals("T3")) { addmoney += 3; }
                    if(this.gameResource[0].getSocreOf("T3") == this.gameResource[1].getSocreOf("T3")){
                        this.gameResource[0].addMoney(addmoney);
                        this.gameResource[1].addMoney(addmoney);
                    } else if(this.gameResource[0].getSocreOf("T3") > this.gameResource[1].getSocreOf("T3")){
                        this.gameResource[0].addMoney(addmoney);
                    } else {
                        this.gameResource[1].addMoney(addmoney);
                    }
                }
                //季節を一つ進める
                this.currentSeason++;
                //雇っているコストのお金を支払う
                this.gameResource[0].payMoneytoWokers();
                this.gameResource[1].payMoneytoWokers();
                this.gameState = STATE_WAIT_PLAYER_PLAY;
            } else {
                //表彰なく進行する場合
                //季節を一つ進める
                this.currentSeason++;
                //雇っているコストのお金を支払う
                this.gameResource[0].payMoneytoWokers();
                this.gameResource[1].payMoneytoWokers();
                this.gameState = STATE_WAIT_PLAYER_PLAY;
            }
        }
    }

    public int getStartPlayer() {
        if(this.gameResource[0].isStartPlayer()){
            return 0;
        } else {
            return 1;
        }
    }

    public void setTreand(String treand) {
        for(int i=0;i<3;i++){
            String key = TREAND_ID_LIST[i];
            if(key.equals(treand)){
                this.trendID = i;
            }
        }
    }
    
    /***
     * CUI出力用
     * 現在のボードの状態（どこに誰のコマがおいてあるか）を文字列で出力
     * @return 
     */
    public String getBoardInformation() {
        if(this.gameState == STATE_WAIT_PLAYER_CONNECTION){
            return "プレイヤー接続待ち";
        }
        
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/  ボードの状態  /_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append("1-1 ゼミの配置状況\n");
        sbuf.append(this.gameBoard.getWorkersOnBoard().get("1-1")+"\n");
        sbuf.append("2 実験の配置状況\n");
        sbuf.append("2-1:"+this.gameBoard.getWorkersOnBoard().get("2-1")+"\n");
        sbuf.append("2-2:"+this.gameBoard.getWorkersOnBoard().get("2-2")+"\n");
        sbuf.append("2-3:"+this.gameBoard.getWorkersOnBoard().get("2-3")+"\n");
        sbuf.append("3 発表の配置状況\n");
        sbuf.append("3-1:"+this.gameBoard.getWorkersOnBoard().get("3-1")+"\n");
        sbuf.append("3-2:"+this.gameBoard.getWorkersOnBoard().get("3-2")+"\n");
        sbuf.append("3-3:"+this.gameBoard.getWorkersOnBoard().get("3-3")+"\n");
        sbuf.append("4 論文の配置状況\n");
        sbuf.append("4-1:"+this.gameBoard.getWorkersOnBoard().get("4-1")+"\n");
        sbuf.append("4-2:"+this.gameBoard.getWorkersOnBoard().get("4-2")+"\n");
        sbuf.append("4-3:"+this.gameBoard.getWorkersOnBoard().get("4-3")+"\n");
        sbuf.append("5 研究報告の配置状況\n");
        sbuf.append("5-1:"+this.gameBoard.getWorkersOnBoard().get("5-1")+"\n");
        sbuf.append("5-2:"+this.gameBoard.getWorkersOnBoard().get("5-2")+"\n");
        sbuf.append("5-3:"+this.gameBoard.getWorkersOnBoard().get("5-3")+"\n");
        sbuf.append("6 雇用の配置状況\n");
        sbuf.append("6-1:"+this.gameBoard.getWorkersOnBoard().get("6-1")+"\n");
        sbuf.append("6-2:"+this.gameBoard.getWorkersOnBoard().get("6-2")+"\n");
        sbuf.append("----------------------------------------------\n");
        sbuf.append("時間経過と研究成果\n");
        sbuf.append("現在の季節："+this.getSeason()+"\n");
        sbuf.append("現在のトレンド："+this.getTrend()+"\n");
        sbuf.append("トレンド1のスコア：Player0="+this.gameResource[0].getSocreOf("T1")+",Player1="+this.gameResource[1].getSocreOf("T1")+"\n");
        sbuf.append("トレンド2のスコア：Player0="+this.gameResource[0].getSocreOf("T2")+",Player1="+this.gameResource[1].getSocreOf("T2")+"\n");
        sbuf.append("トレンド3のスコア：Player0="+this.gameResource[0].getSocreOf("T3")+",Player1="+this.gameResource[1].getSocreOf("T3")+"\n");
        sbuf.append("----------------------------------------------\n");
        sbuf.append("現在プレイ待ちのプレイヤー：Player"+this.CurrentPlayer+"("+ this.PlayerName[this.CurrentPlayer] +")\n");
        sbuf.append("スタートプレイヤー：Player"+this.currentStartPlayer+"\n");
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        return sbuf.toString();
    }

    /***
     * CUI出力用
     * 現在のリソース状態（各プレイヤーが持つリソース）を文字列で出力
     * @return 
     */
    public String getResourceInformation() {
        if(this.gameState == STATE_WAIT_PLAYER_CONNECTION){
            return "プレイヤー接続待ち";
        }

        StringBuilder sbuf = new StringBuilder();
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/  プレイヤーの状態  /_/_/_/_/_/_/_/\n");
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        sbuf.append(this.getResourceInformation(0));
        sbuf.append(this.getResourceInformation(1));
        sbuf.append("/_/_/_/_/_/_/_/_/_/_/_/_/_/_/_//_/_/_/_/_/_/_/\n");
        return sbuf.toString();
    }
    
    private String getResourceInformation(int playerID){
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("Player"+playerID+"("+ this.PlayerName[playerID] +")\n");
        sbuf.append("----------------------------------------------\n");
        sbuf.append("1 コマの利用可能状況\n");
        sbuf.append("教授:"+this.gameResource[playerID].hasWorkerOf("P")+"\n");
        sbuf.append("助手:"+this.gameResource[playerID].hasWorkerOf("A")+"\n");
        sbuf.append("学生:"+this.gameResource[playerID].hasWorkerOf("S")+"\n");
        sbuf.append("学生コマの保持数:");
        sbuf.append(this.gameResource[playerID].getTotalStudentsCount()+"\n");
        sbuf.append("2 資金と研究ポイントの状況\n");
        sbuf.append("お金:"+this.gameResource[playerID].getCurrentMoney()+"\n");
        sbuf.append("研究ポイント:"+this.gameResource[playerID].getCurrentResrchPoint()+"\n");
        sbuf.append("3 総合得点:"+this.gameResource[playerID].getTotalScore()+"\n");
        sbuf.append("----------------------------------------------\n");
        return sbuf.toString();
    }
    
    public void printMessage(String text){
        this.setChanged();
        this.notifyObservers(text);
    }


    
}
