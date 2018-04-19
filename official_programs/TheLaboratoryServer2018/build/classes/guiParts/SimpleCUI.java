/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiParts;

import gameElements.Game;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author kosen
 */
public class SimpleCUI implements Observer{

    @Override
    public void update(Observable source, Object arg) {
        if(source instanceof Game){
            System.out.println(((Game)source).getBoardInformation());
            System.out.println(((Game)source).getResourceInformation());
            if(arg instanceof String){
                System.out.println(arg);
            }
        }
    }
    
}
