/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai;

/**
 *
 * @author niwatakumi
 */
public class Action {
    public String worker;
    public String place;
    public String trend;
    
    public Action(String worker, String place){
        this.worker = worker;
        this.place = place;
        this.trend = null;
    }
    
    public Action(String worker, String place, String trend){
        this.worker = worker;
        this.place = place;
        this.trend = trend;
    }
    
    @Override
    public String toString(){
        if(trend != null){
            return worker + " : " + place + "(" + trend + ")";
        }
        else{
            return worker + " : " + place;
        }
    }
}
