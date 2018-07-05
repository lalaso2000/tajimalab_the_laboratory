/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

import java.util.ArrayList;

/**
 *
 * @author niwatakumi
 */
public class AwardCheckData implements Comparable<AwardCheckData>{
    private ArrayList<Action> path;
    private boolean awardable;
    private int workers;
    private int reserchPoint;
    private int score;

    /**
     * 初期化コンストラクタ
     */
    public AwardCheckData() {
        path = new ArrayList<>();
        awardable = false;
        workers = 0;
        reserchPoint = 0;
        score = 0;
    }
    
    /**
     * コピーコンストラクタ
     * @param a 
     */
    public AwardCheckData(AwardCheckData a){
        path = (ArrayList<Action>)a.path.clone();
        awardable = a.awardable;
        workers = a.workers;
        reserchPoint = a.reserchPoint;
        score = a.score;
    }

    @Override
    public int compareTo(AwardCheckData o) {
        // 表彰が取れるやつが優先
        if(this.awardable == true && o.awardable == false) return 1;
        else if(this.awardable == false && o.awardable == true) return -1;
        
        // 3,4に置くコマが少ないほうが優先
        if(this.workers < o.workers) return 1;
        else if(this.workers > o.workers) return -1;
        
        // 使う研究ポイントが少ないほうが優先
        if(this.reserchPoint < o.reserchPoint) return 1;
        else if(this.reserchPoint > o.reserchPoint) return -1;
        
        // もらえる点が多いほうが優先
        if(this.score > o.score) return 1;
        else if(this.score < o.score) return -1;
        
        // それでも同じなら評価は同じ
        return 0;
    }
    
    public void addPath(Action a){
        this.path.add(a);
    }
    
    public void setAwardable(boolean b){
        this.awardable = b;
    }
    
    public void addReserchPoint(int reserchPoint){
        this.reserchPoint += reserchPoint;
    }
    
    public void addScore(int score){
        this.score += score;
    }
    
    
}
