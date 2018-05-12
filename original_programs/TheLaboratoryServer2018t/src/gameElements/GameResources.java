/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameElements;

/**
 *
 * @author kosen
 */
public class GameResources {
    private int money;
    private int reserchPoint;
    private int[] score;
    private int debtCount;

    private int[] workerList;
    private int[] usedWorkers;

    private boolean startPlayerFlag;


    public GameResources(){
        this.money = 5;
        this.reserchPoint = 0;
        this.score = new int[3];
        this.score[0] = 0;
        this.score[1] = 0;
        this.score[2] = 0;
        this.workerList = new int[3];
        this.workerList[0] = 1;
        this.workerList[1] = 0;
        this.workerList[2] = 1;
        this.usedWorkers = new int[3];
        this.debtCount = 0;
        this.startPlayerFlag = false;
    }

    public boolean hasWorkerOf(String typeOfWorker) {
        if(typeOfWorker.equals("P")){
            return (this.workerList[0] > 0);
        } else if (typeOfWorker.equals("A")){
            return (this.workerList[1] > 0);
        } else if (typeOfWorker.equals("S")){
            return (this.workerList[2] > 0);
        }
        return false;
    }

    public int getCurrentMoney() {
        return this.money;
    }

    public void addMoney(int i) {
        this.money += i;
    }

    //fixed 18.05.11
    public boolean alreadyHiredAssistant(){
        if(this.workerList[1] > 0){
            return true;
        }
        if(this.usedWorkers[1] > 0){
            return true;
        }
        return false;
    }
    //fixed 18.05.11


    public void putWorker(String typeOfWorker) {
        if(this.hasWorkerOf(typeOfWorker)){
            if(typeOfWorker.equals("P")){
                this.workerList[0]--;
                this.usedWorkers[0]++;
            } else if (typeOfWorker.equals("A")){
                this.workerList[1]--;
                this.usedWorkers[1]++;
            } else if (typeOfWorker.equals("S")){
                this.workerList[2]--;
                this.usedWorkers[2]++;
            }
        }
    }

    public int getCurrentResrchPoint() {
        return this.reserchPoint;
    }

    public void addReserchPoint(int i) {
        this.reserchPoint += i;
    }

    public boolean hasWorker() {
        return ((this.workerList[0]+this.workerList[1]+this.workerList[2]) > 0);
    }

    public int getSocreOf(String trend) {
        if(trend.equals("T1")){
            return this.score[0];
        } else if(trend.equals("T2")){
            return this.score[1];
        } else if(trend.equals("T3")){
            return this.score[2];
        }
        return -1;
    }

    public int getTotalScore() {
        return this.score[0]+this.score[1]+this.score[2]-3*this.debtCount;
    }

    public boolean isStartPlayer() {
        return this.startPlayerFlag;
    }

    public void addScorePoint(int scoreTreand,int point) {
        this.score[scoreTreand] += point;
    }

    public void addNewStudent() {
        this.usedWorkers[2]++;
    }

    public void addNewAssistant() {
        this.usedWorkers[1]++;
    }

    public void returnAllWorkers() {
        for(int i=0;i<3;i++){
            this.workerList[i] = this.usedWorkers[i];
            this.usedWorkers[i] = 0;
        }
    }

    public void payMoneytoWokers() {
        this.money -= this.workerList[2];
        this.money -= 3*this.workerList[1];
        if(this.money < 0 ) {
            this.debtCount += (-1)*this.money;
            this.money = 0;
        }
    }

    public void setStartPlayer(boolean b) {
        this.startPlayerFlag = b;
    }

    public int getTotalStudentsCount() {
        return this.workerList[2] + this.usedWorkers[2];
    }

    public int getNumberofUseableWorkers(String typeOfWorker) {
        if(typeOfWorker.equals("P")){
            return this.workerList[0];
        } else if (typeOfWorker.equals("A")){
            return this.workerList[1];
        } else if (typeOfWorker.equals("S")){
            return this.workerList[2];
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDebt() {
        return this.debtCount;
    }

}
