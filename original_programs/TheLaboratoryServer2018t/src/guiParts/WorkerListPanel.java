/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guiParts;

import gameElements.Board;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import os.PlatformUtils;

/**
 *
 * @author ktajima
 */
public class WorkerListPanel extends JPanel {
    String Path = "images/";
  
    double baseWidth = 180;
    double baseHeight = 190;
    double scale;
    
    int countStudent = 0;
    int countAssistant = 0;
    int countProfessor = 0;
       
    BufferedImage Player0Professor;
    BufferedImage Player1Professor;
    BufferedImage Player0Assistant;
    BufferedImage Player1Assistant;
    BufferedImage Player0Student;
    BufferedImage Player1Student;
    
    int playerNumber = 0;
    
    public WorkerListPanel(int player){
        // os判定
        // ふっざ(ry
        if(PlatformUtils.isWindows()){
            this.Path = "images\\";
        }
        
        try {
            this.initImage();
            this.playerNumber = player;
        } catch (IOException ex) {
        }
        
        /**配置サンプル*/
        //this.workerList.put("1-1", "S0,S1,P1,P0,P1,P1,P1,P1,A0");
        //this.workerList.put("2-1", "S0");
        //
        
    }
    
    public void setCountOfStudent(int s){
        this.countStudent = s;
    }
    public void setCountOfAssistant(int s){
        this.countAssistant = s;
    }
    public void setCountOfProfessor(int s){
        this.countProfessor = s;
    }
    
    private void initImage() throws IOException{
        Player0Student = ImageIO.read(new File(Path +"worker_student0.png"));
        Player1Student = ImageIO.read(new File(Path +"worker_student1.png"));
        Player0Professor = ImageIO.read(new File(Path +"worker_professer0.png"));
        Player1Professor = ImageIO.read(new File(Path +"worker_professer1.png"));
        Player0Assistant = ImageIO.read(new File(Path +"worker_assistant0.png"));
        Player1Assistant = ImageIO.read(new File(Path +"worker_assistant1.png"));
    }
    
    @Override
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        this.scale = (double)this.getWidth() / (double)baseWidth;
        System.out.println("Scale:"+this.scale);
        int new_width = (int)this.getWidth();
        int new_height = (int)(this.getWidth() * baseHeight / baseWidth);
        AffineTransformOp xform = new AffineTransformOp( AffineTransform.getScaleInstance( ( double )new_width / baseWidth, ( double )new_height / baseHeight ), AffineTransformOp.TYPE_BILINEAR );


        BufferedImage resizedIconS = null;
        BufferedImage resizedIconA = null;
        BufferedImage resizedIconP = null;
        if(this.playerNumber == 0){
            resizedIconP = new BufferedImage(((int)(Player0Professor.getWidth()*this.scale)),((int)(Player0Professor.getHeight()*this.scale)), Player0Professor.getType());
            xform.filter( Player0Professor, resizedIconP );
            resizedIconA = new BufferedImage(((int)(Player0Assistant.getWidth()*this.scale)),((int)(Player0Assistant.getHeight()*this.scale)), Player0Assistant.getType());
            xform.filter( Player0Assistant, resizedIconA );
            resizedIconS = new BufferedImage(((int)(Player0Student.getWidth()*this.scale)),((int)(Player0Student.getHeight()*this.scale)), Player0Student.getType());
            xform.filter( Player0Student, resizedIconS );
        } else if(this.playerNumber == 1){
            resizedIconP = new BufferedImage(((int)(Player1Professor.getWidth()*this.scale)),((int)(Player1Professor.getHeight()*this.scale)), Player1Professor.getType());
            xform.filter( Player1Professor, resizedIconP );
            resizedIconA = new BufferedImage(((int)(Player1Assistant.getWidth()*this.scale)),((int)(Player1Assistant.getHeight()*this.scale)), Player1Assistant.getType());
            xform.filter( Player1Assistant, resizedIconA );
            resizedIconS = new BufferedImage(((int)(Player1Student.getWidth()*this.scale)),((int)(Player1Student.getHeight()*this.scale)), Player1Student.getType());
            xform.filter( Player1Student, resizedIconS );
        }
        
        ArrayList<String> list = new ArrayList<String>();
        for(int i=0;i<this.countProfessor;i++){
            list.add("P");
        }
        for(int i=0;i<this.countAssistant;i++){
            list.add("A");
        }
        for(int i=0;i<this.countStudent;i++){
            list.add("S");
        }
        
        if(list.size() > 0){
            int addX = this.getWidth() / 2;
            int addY = 0;
            if(list.size() > 2){
                addY = this.getHeight() / (list.size() / 2 + 1);
            }
            for(int i=0;i<list.size();i++){
                String workername = list.get(i);
                int x = 0 + (addX*(i%3));
                int y = 0 + (addY*(i/3));

                if(workername.equals("P")){
                    g2.drawImage(resizedIconP, x, y, this);
                } else if(workername.equals("A")){
                    g2.drawImage(resizedIconA, x, y, this);
                } else if(workername.equals("S")){
                    g2.drawImage(resizedIconS, x, y, this);
                }
            }
        }
    }

}
