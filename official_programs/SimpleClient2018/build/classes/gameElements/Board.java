/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 盤面を管理するクラス
 * HashMapで状態を取得可能で,
 * テキストによる出力も可能
 * 
 * -1:誰もおいていない
 * 0:プレイヤー0の設置
 * 1:プレイヤー1の設置
 * とする。
 * 
 * ワーカー配置のメソッドを持つ
 * @author ktajima
 */
public class Board {
    public static final String[] PLACE_NAMES = {"1-1","2-1","2-2","2-3","3-1","3-2","3-3","4-1","4-2","4-3","5-1","5-2","5-3","6-1","6-2"};
    public static final int PLAYER_COUNT = 2;

    public HashMap<String,String> boardState;
    public ArrayList<String> SeminorWorkers;
    
    public Board(){
        init();
    }
    
    private void init(){
        //ボードの初期化
        this.boardState = new HashMap<String,String>();
        for(String key:PLACE_NAMES){
            this.boardState.put(key, "");
        }
        this.SeminorWorkers = new ArrayList<String>();
    }
    
    /** ピース設置可能かの判定
     * 第1引数:player プレイヤー番号0または1
     * 第2引数:pice 設置場所
     * 
     * 戻り値は設置可能かどうかのブール値
     */
    public boolean canPutWorker(int player,String place){
        if(player < 0 || player > 1){
            //プレイヤー番号が不正
            return false;
        }
        if(!this.boardState.containsKey(place)){
            //設置場所が不正
            return false;
        }
        //1-1はいくつでもピースを受け入れ可能
        if(place.equals("1-1")){
            return true;
        }
        //その場所に既にコマがおかれているかを確認
        if(!this.boardState.get(place).equals("")){
            return false;
        }
        //設置誓約がある場所かを確認
        if(place.equals("2-2")){
            if(this.boardState.get("2-1").equals("")){
                return false;
            }
        }
        if(place.equals("2-3")){
            if(this.boardState.get("2-2").equals("")){
                return false;
            }
       }
        if(place.equals("4-2")){
            if(this.boardState.get("4-1").equals("")){
                return false;
            }
        }
        if(place.equals("4-3")){
            if(this.boardState.get("4-2").equals("")){
                return false;
            }
        }
        
        //以上の条件に引っかからなければOK
        return true;
    }
    
    /** コマを設置するメソッド
     * 第1引数:player プレイヤー番号0または1
     * 第2引数:plac 設置場所
     * 第3引数:worker ワーカーの種類
     */
    public boolean putWorker(int player,String place,String worker){
        if(!this.canPutWorker(player, place)){
            return false;
        }
        
        if(place.equals("1-1")){
            this.SeminorWorkers.add(worker+player);
        } else {
            this.boardState.put(place, worker+player);
        }

        return true;
    }
    
    public ArrayList<String> getSeminorWorkers(){
        return this.SeminorWorkers;
    }
    
    public HashMap<String,ArrayList<String>> getWorkersOnBoard(){
         HashMap<String,ArrayList<String>> workers = new  HashMap<String,ArrayList<String>>();
        for(String key:PLACE_NAMES){
            if(key.equals("1-1")){
                if(!this.SeminorWorkers.isEmpty()){
                    workers.put(key, this.SeminorWorkers);
                }
            } else {
                if(!this.boardState.get(key).equals("")){
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(this.boardState.get(key));
                    workers.put(key,list);
                }
            }
        }
        return workers;
    }

    public void printCurrentBoard(){
        for(String key:PLACE_NAMES){
            if(key.equals("1-1")){
                System.out.print("1-1:");
                System.out.println(this.SeminorWorkers);
            } else {
                System.out.print(key);
                System.out.print(":");
                System.out.println(this.boardState.get(key));
            }
        }
    }
    
}
