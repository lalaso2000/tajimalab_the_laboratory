/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

import java.text.DecimalFormat;
import java.util.Observable;

/**
 *
 * @author koji
 */
public class TimerThread extends Observable implements Runnable  {
    
    private boolean runnable = true;
    
    private long counter[];
    private int countingID = -1;
    private long startTime[];
    
    public TimerThread(){
        //Thread th = new Thread(this);
        this.init();
        //th.start();
    }

    public void init(){
        this.startTime = new long[2];
        this.counter = new long[2];
        this.counter[0] = 0;
        this.counter[1] = 0;
    }
    
    public void StartTimeCount(int PlayerID){
        if(countingID == -1){
            this.startTime[PlayerID] = System.currentTimeMillis();
            this.countingID = PlayerID;
        } else {
            StopTimeCount(countingID);
            StartTimeCount(PlayerID);
        }
    }
    
    public long StopTimeCount(int PlayerID){
        if(countingID == PlayerID){
            this.counter[PlayerID] += System.currentTimeMillis() - this.startTime[PlayerID];
            this.countingID = -1;
        }
        return this.counter[PlayerID];
    }
    
    private static DecimalFormat ddec = new DecimalFormat("00");
    private static DecimalFormat qdec = new DecimalFormat("0000");
    public static String formatTimes(long millis){
        long sec = millis / 1000;
        long ms  = millis % 1000;
        int min = (int)(sec / 60);
        sec = sec % 60;
        int hour = min / 60;
        min = min % 60;
        
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(ddec.format(hour));
        sbuf.append(":");
        sbuf.append(ddec.format(min));
        sbuf.append(":");
        sbuf.append(ddec.format(sec));
        sbuf.append(".");
        sbuf.append(qdec.format(ms));
        return sbuf.toString();
    }
            
    
    public void run() {
        while(this.runnable){
            try {
                Thread.sleep(1000);
                this.setChanged();
                this.notifyObservers();
            } catch (InterruptedException ex) {
                //
            }
        }
    }
    
    public long getUsedTime(int playerID){
        if(this.countingID == playerID){
            return counter[playerID] + System.currentTimeMillis() - this.startTime[playerID];
        } else {
            return counter[playerID];
        }
    }

    public void timeOutPlayer(int playerID) {
        this.StopTimeCount(playerID);
    }
    
}
