/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guiParts;

import gameElements.Board;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author ktajima
 */
public class ImagePanel extends JPanel {
    public static String Path = "images\\";
    public static File boardImage = new File(Path + "gameboard.png");
    public static File StartPlayerMakerImage = new File(Path + "startplayer.png");
    public static String[] SEASON_NAMES = {"1a","1b","2a","2b","3a","3b","4a","4b","5a","5b","6a","6b"};

    private HashMap<String,Rectangle> baseBounds;
    private BufferedImage boardImageBuffer;

    
    double scale;
    private HashMap<String,Rectangle> scaledBounds;
    
    
    BufferedImage Player0Professor;
    BufferedImage Player1Professor;
    BufferedImage Player0Assistant;
    BufferedImage Player1Assistant;
    BufferedImage Player0Student;
    BufferedImage Player1Student;
    
    BufferedImage TreandIcon;
    BufferedImage Player0Coin10;
    BufferedImage Player1Coin10;
    BufferedImage Player0Coin5;
    BufferedImage Player1Coin5;
    BufferedImage Player0Coin2;
    BufferedImage Player1Coin2;
    BufferedImage Player0Coin1;
    BufferedImage Player1Coin1;

    BufferedImage StartPlayerIcon;
    BufferedImage SeasonIcon;
    
    HashMap<String,String> workerList;
    
    String currentTrend;
    int[] player0points;
    int[] player1points;
    
    int currentSeason;
    
    public ImagePanel(){
        this.workerList = new HashMap<String,String>();
        player0points = new int[3];
        player1points = new int[3];
        for(int i=0;i<3;i++){
            player0points[i] = 0;
            player1points[i] = 0;
        }
        this.currentSeason = 0;
        this.currentTrend = "T0";

        try {
            this.initImage();
        } catch (IOException ex) {
            Logger.getLogger(ImagePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**配置サンプル*/
        //this.workerList.put("1-1", "S0,S1,P1,P0,P1,P1,P1,P1,A0");
        //this.workerList.put("2-1", "S0");
        //
    }
    
    public void resetAllWorkers(){
        this.workerList = new HashMap<String,String>();
    }
    
    public boolean AddWorker(String place,String worker){
        if(place.equals("1-1")){
            if(this.workerList.containsKey(place)){
                this.workerList.put(place,  this.workerList.get(place) + "," + worker);
                return true;
            } else {
                this.workerList.put(place, worker);
                return true;
            }
        } else {
            if(this.workerList.containsKey(place)){
                return false;
            } else {
                this.workerList.put(place, worker);
                return true;
            }
        }
    }
    
    public ArrayList<String> getWorkers(String place){
        ArrayList<String> list = new ArrayList<String>();
        if(this.workerList.containsKey(place)){
            if(place.equals("1-1")){
                String[] workernames = this.workerList.get(place).split(",");
                list.addAll(Arrays.asList(workernames));
            } else {
                String workername = this.workerList.get(place);
                list.add(workername);
            }
        }
        return list;
    }
    
        
    private void initImage() throws IOException{
        Player0Student = ImageIO.read(new File(Path +"worker_student0.png"));
        Player1Student = ImageIO.read(new File(Path +"worker_student1.png"));
        Player0Professor = ImageIO.read(new File(Path +"worker_professer0.png"));
        Player1Professor = ImageIO.read(new File(Path +"worker_professer1.png"));
        Player0Assistant = ImageIO.read(new File(Path +"worker_assistant0.png"));
        Player1Assistant = ImageIO.read(new File(Path +"worker_assistant1.png"));
        TreandIcon = ImageIO.read(new File(Path +"trend.png"));
        Player0Coin10 = ImageIO.read(new File(Path +"10Point0.png"));
        Player1Coin10 = ImageIO.read(new File(Path +"10Point1.png"));
        Player0Coin5 = ImageIO.read(new File(Path +"5Point0.png"));
        Player1Coin5 = ImageIO.read(new File(Path +"5Point1.png"));
        Player0Coin2 = ImageIO.read(new File(Path +"2Point0.png"));
        Player1Coin2 = ImageIO.read(new File(Path +"2Point1.png"));
        Player0Coin1 = ImageIO.read(new File(Path +"1Point0.png"));
        Player1Coin1 = ImageIO.read(new File(Path +"1Point1.png"));
        StartPlayerIcon = ImageIO.read(new File(Path +"startplayer.png"));
        SeasonIcon  = ImageIO.read(new File(Path +"season.png"));
        
        
        
        this.boardImageBuffer = ImageIO.read(boardImage);
        this.baseBounds = new HashMap<String,Rectangle>();
        baseBounds.put("1-1", new Rectangle(29,157,286,247));
        baseBounds.put("2-1", new Rectangle(385,159,86,68));
        baseBounds.put("2-2", new Rectangle(385,258,86,68));
        baseBounds.put("2-3", new Rectangle(385,355,86,68));
        baseBounds.put("3-1", new Rectangle(711,159,83,66));
        baseBounds.put("3-2", new Rectangle(711,273,83,66));
        baseBounds.put("3-3", new Rectangle(711,385,83,66));
        baseBounds.put("4-1", new Rectangle(1043,164,84,67));
        baseBounds.put("4-2", new Rectangle(1043,273,84,67));
        baseBounds.put("4-3", new Rectangle(1043,378,84,67));
        baseBounds.put("5-1", new Rectangle(47,520,82,64));
        baseBounds.put("5-2", new Rectangle(47,613,82,64));
        baseBounds.put("5-3", new Rectangle(47,705,82,64));
        baseBounds.put("6-1", new Rectangle(389,520,84,65));
        baseBounds.put("6-2", new Rectangle(389,607,188,67));
        baseBounds.put("T1", new Rectangle(901,556,52,46));
        baseBounds.put("T2", new Rectangle(1059,556,52,46));
        baseBounds.put("T3", new Rectangle(1233,556,52,46));
        baseBounds.put("Score-T1-0", new Rectangle(867,651,56,178));
        baseBounds.put("Score-T1-1", new Rectangle(923,651,56,178));
        baseBounds.put("Score-T2-0", new Rectangle(1035,651,56,178));
        baseBounds.put("Score-T2-1", new Rectangle(1091,651,56,178));
        baseBounds.put("Score-T3-0", new Rectangle(1203,651,56,178));
        baseBounds.put("Score-T3-1", new Rectangle(1259,651,56,178));

        baseBounds.put(SEASON_NAMES[0], new Rectangle(869,830,56,41));
        baseBounds.put(SEASON_NAMES[1], new Rectangle(869+56,830,56,41));
        baseBounds.put(SEASON_NAMES[2], new Rectangle(869+56*2+60,830,56,41));
        baseBounds.put(SEASON_NAMES[3], new Rectangle(869+56*3+60,830,56,41));
        baseBounds.put(SEASON_NAMES[4], new Rectangle(869+56*4+120,830,56,41));
        baseBounds.put(SEASON_NAMES[5], new Rectangle(869+56*5+120,830,56,41));
        baseBounds.put(SEASON_NAMES[6], new Rectangle(869,830+41,56,41));
        baseBounds.put(SEASON_NAMES[7], new Rectangle(869+56,830+41,56,41));
        baseBounds.put(SEASON_NAMES[8], new Rectangle(869+56*2+60,830+41,56,41));
        baseBounds.put(SEASON_NAMES[9], new Rectangle(869+56*3+60,830+41,56,41));
        baseBounds.put(SEASON_NAMES[10], new Rectangle(869+56*4+120,830+41,56,41));
        baseBounds.put(SEASON_NAMES[11], new Rectangle(869+56*5+120,830+41,56,41));
            
        
    }
    
    @Override
    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        this.scale = (double)this.getWidth() / (double)boardImageBuffer.getWidth();
        System.out.println("Scale:"+this.scale);
        if (boardImageBuffer != null){
            //サイズ決定
            int new_width = this.getWidth();
            int new_height = this.getWidth() * boardImageBuffer.getHeight() / boardImageBuffer.getWidth();
            //画像変換
            AffineTransformOp xform = new AffineTransformOp( AffineTransform.getScaleInstance( ( double )new_width / boardImageBuffer.getWidth(), ( double )new_height / boardImageBuffer.getHeight() ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage resized = new BufferedImage( new_width, new_height, boardImageBuffer.getType() );
            xform.filter( boardImageBuffer, resized );
            //基本図の描画
            g2.drawImage(resized, 0, 0, this);
            
            //クリックできる領域の変更
            this.scaledBounds = new HashMap<String,Rectangle>();
            for(String key:baseBounds.keySet()){
                Rectangle base = baseBounds.get(key);
                Rectangle newbounds = new Rectangle((int)(base.x*this.scale), (int)(base.y*this.scale), (int)(base.width*this.scale), (int)(base.height*this.scale));
                this.scaledBounds.put(key, newbounds);
            }

            BufferedImage resizedIconP0 = new BufferedImage(((int)(Player0Professor.getWidth()*this.scale)),((int)(Player0Professor.getHeight()*this.scale)), Player0Professor.getType());
            xform.filter( Player0Professor, resizedIconP0 );
            BufferedImage resizedIconP1 = new BufferedImage(((int)(Player1Professor.getWidth()*this.scale)),((int)(Player1Professor.getHeight()*this.scale)), Player1Professor.getType());
            xform.filter( Player1Professor, resizedIconP1 );
            BufferedImage resizedIconA0 = new BufferedImage(((int)(Player0Assistant.getWidth()*this.scale)),((int)(Player0Assistant.getHeight()*this.scale)), Player0Assistant.getType());
            xform.filter( Player0Assistant, resizedIconA0 );
            BufferedImage resizedIconA1 = new BufferedImage(((int)(Player1Assistant.getWidth()*this.scale)),((int)(Player1Assistant.getHeight()*this.scale)), Player1Assistant.getType());
            xform.filter( Player1Assistant, resizedIconA1 );
            BufferedImage resizedIconS0 = new BufferedImage(((int)(Player0Student.getWidth()*this.scale)),((int)(Player0Student.getHeight()*this.scale)), Player0Student.getType());
            xform.filter( Player0Student, resizedIconS0 );
            BufferedImage resizedIconS1 = new BufferedImage(((int)(Player1Student.getWidth()*this.scale)),((int)(Player1Student.getHeight()*this.scale)), Player1Student.getType());
            xform.filter( Player1Student, resizedIconS1 );
            
            for(String place:Board.PLACE_NAMES){
                Rectangle placeBound = this.scaledBounds.get(place);
                if(place.equals("1-1")){
                    if(this.workerList.containsKey(place)){
                        //1-1だけは個数に合わせて分割する
                        ArrayList<String> workernames = this.getWorkers(place);
                        int addX = placeBound.width / 3;
                        int addY = 0;
                        if(workernames.size() > 3){
                            addY = placeBound.height / (workernames.size() / 3 + 1);
                        }
                        for(int i=0;i<workernames.size();i++){
                            String workername = workernames.get(i);
                            int x = placeBound.x + (addX*(i%3));
                            int y = placeBound.y + (addY*(i/3));

                            if(workername.equals("P0")){
                                g2.drawImage(resizedIconP0, x, y, this);
                            } else if(workername.equals("P1")){
                                g2.drawImage(resizedIconP1, x, y, this);
                            } else if(workername.equals("A0")){
                                g2.drawImage(resizedIconA0, x, y, this);
                            } else if(workername.equals("A1")){
                                g2.drawImage(resizedIconA1, x, y, this);
                            } else if(workername.equals("S0")){
                                g2.drawImage(resizedIconS0, x, y, this);
                            } else if(workername.equals("S1")){
                                g2.drawImage(resizedIconS1, x, y, this);
                            }
                        }
                        String[] workername = this.workerList.get(place).split(",");
                    }
                    
                } else {
                    //それ以外は単に置く
                    if(this.workerList.containsKey(place)){
                        String workername = this.workerList.get(place);
                        if(workername.equals("P0")){
                            g2.drawImage(resizedIconP0, placeBound.x, placeBound.y, this);
                        } else if(workername.equals("P1")){
                            g2.drawImage(resizedIconP1, placeBound.x, placeBound.y, this);
                        } else if(workername.equals("A0")){
                            g2.drawImage(resizedIconA0, placeBound.x, placeBound.y, this);
                        } else if(workername.equals("A1")){
                            g2.drawImage(resizedIconA1, placeBound.x, placeBound.y, this);
                        } else if(workername.equals("S0")){
                            g2.drawImage(resizedIconS0, placeBound.x, placeBound.y, this);
                        } else if(workername.equals("S1")){
                            g2.drawImage(resizedIconS1, placeBound.x, placeBound.y, this);
                        }
                    }
                }
            }
            //トレンドアイコン
            BufferedImage resizedTreandIcon = new BufferedImage(((int)(TreandIcon.getWidth()*this.scale*0.6)),((int)(TreandIcon.getHeight()*this.scale*0.6)), TreandIcon.getType());
            AffineTransformOp xform2 = new AffineTransformOp( AffineTransform.getScaleInstance( this.scale*0.6, this.scale*0.6 ), AffineTransformOp.TYPE_BILINEAR );
            xform2.filter( TreandIcon, resizedTreandIcon );
            if(this.currentTrend.equals("T0")){
            } else {
                Rectangle placeBound = this.scaledBounds.get(this.currentTrend);
                g2.drawImage(resizedTreandIcon, placeBound.x, placeBound.y, this);
            }
            
            
            //スコアの表示
            double scale_star = this.scale * 0.5;
            xform = new AffineTransformOp( AffineTransform.getScaleInstance(scale_star, scale_star), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage resizedPlayer0Coin10 = new BufferedImage(((int)(Player0Coin10.getWidth()*scale_star)),((int)(Player0Coin10.getHeight()*scale_star)), Player0Coin10.getType());
            xform.filter( Player0Coin10, resizedPlayer0Coin10 );
            BufferedImage resizedPlayer1Coin10 = new BufferedImage(((int)(Player1Coin10.getWidth()*scale_star)),((int)(Player1Coin10.getHeight()*scale_star)), Player1Coin10.getType());
            xform.filter( Player1Coin10, resizedPlayer1Coin10 );
            BufferedImage resizedPlayer0Coin5 = new BufferedImage(((int)(Player0Coin5.getWidth()*scale_star)),((int)(Player0Coin5.getHeight()*scale_star)), Player0Coin5.getType());
            xform.filter( Player0Coin5, resizedPlayer0Coin5 );
            BufferedImage resizedPlayer1Coin5 = new BufferedImage(((int)(Player1Coin5.getWidth()*scale_star)),((int)(Player1Coin5.getHeight()*scale_star)), Player1Coin5.getType());
            xform.filter( Player1Coin5, resizedPlayer1Coin5 );
            BufferedImage resizedPlayer0Coin2 = new BufferedImage(((int)(Player0Coin2.getWidth()*scale_star)),((int)(Player0Coin2.getHeight()*scale_star)), Player0Coin2.getType());
            xform.filter( Player0Coin2, resizedPlayer0Coin2 );
            BufferedImage resizedPlayer1Coin2 = new BufferedImage(((int)(Player1Coin2.getWidth()*scale_star)),((int)(Player1Coin2.getHeight()*scale_star)), Player1Coin2.getType());
            xform.filter( Player1Coin2, resizedPlayer1Coin2 );
            BufferedImage resizedPlayer0Coin1 = new BufferedImage(((int)(Player0Coin1.getWidth()*scale_star)),((int)(Player0Coin1.getHeight()*scale_star)), Player0Coin1.getType());
            xform.filter( Player0Coin1, resizedPlayer0Coin1 );
            BufferedImage resizedPlayer1Coin1 = new BufferedImage(((int)(Player1Coin1.getWidth()*scale_star)),((int)(Player1Coin1.getHeight()*scale_star)), Player1Coin1.getType());
            xform.filter( Player1Coin1, resizedPlayer1Coin1 );
            
            String[] boundNames = {"Score-T1-0","Score-T1-1","Score-T2-0","Score-T2-1","Score-T3-0","Score-T3-1"};
            int[] pointValues = {this.player0points[0],this.player1points[0],this.player0points[1],this.player1points[1],this.player0points[2],this.player1points[2]};
            int[] starPattern = {0,1,0,1,0,1};
            BufferedImage[][] starIcons = {{resizedPlayer0Coin10,resizedPlayer0Coin5,resizedPlayer0Coin2,resizedPlayer0Coin1},{resizedPlayer1Coin10,resizedPlayer1Coin5,resizedPlayer1Coin2,resizedPlayer1Coin1}};
            for(int i=0;i<boundNames.length;i++){
                Rectangle placeBound = this.scaledBounds.get(boundNames[i]);
                int addY = 0;
                for(int c=0;c<(pointValues[i] / 10);c++){
                    g2.drawImage(starIcons[starPattern[i]][0], placeBound.x, placeBound.y+addY, this);
                    addY += starIcons[starPattern[i]][0].getHeight()-30*this.scale;
                }
                int remain = pointValues[i] % 10;
                for(int c=0;c<(remain / 5);c++){
                    g2.drawImage(starIcons[starPattern[i]][1], placeBound.x, placeBound.y+addY, this);
                    addY += starIcons[starPattern[i]][1].getHeight()-30*this.scale;
                }
                remain = remain % 5;
                for(int c=0;c<(remain / 2);c++){
                    g2.drawImage(starIcons[starPattern[i]][2], placeBound.x, placeBound.y+addY, this);
                    addY += starIcons[starPattern[i]][2].getHeight()-30*this.scale;
                }
                remain = remain % 2;
                for(int c=0;c<remain;c++){
                    g2.drawImage(starIcons[starPattern[i]][3], placeBound.x, placeBound.y+addY, this);
                    addY += starIcons[starPattern[i]][3].getHeight()-30*this.scale;
                }
            }
            
            //TODO 季節の表示
            BufferedImage resizedSeasonIcon = new BufferedImage(((int)(SeasonIcon.getWidth()*scale_star)),((int)(SeasonIcon.getHeight()*scale_star)), SeasonIcon.getType());
            xform.filter( SeasonIcon, resizedSeasonIcon );
            Rectangle placeBound = this.scaledBounds.get(SEASON_NAMES[this.currentSeason]);
            g2.drawImage(resizedSeasonIcon, placeBound.x, placeBound.y, this);
            
        
            //スタートプレイヤーアイコン
            BufferedImage resizedStartPlayerIcon = new BufferedImage(((int)(StartPlayerIcon.getWidth()*this.scale)),((int)(StartPlayerIcon.getHeight()*this.scale)), StartPlayerIcon.getType());
            xform.filter( StartPlayerIcon, resizedStartPlayerIcon );
            

        }
    }
    
    public String getBoardClickedArea(Point p){
        for(String key:scaledBounds.keySet()){
            Rectangle bounds = scaledBounds.get(key);
            if(bounds.contains(p)){
                //Rectangle flush = this.scaledBounds.get(key);
                //this.paint(null);
                
                return key;
            }
        }
        return null;
    }

    public void setTreand(String trend) {
        if(trend.equals("T1")){
            this.currentTrend = "T1";
        } else if(trend.equals("T2")){
            this.currentTrend = "T2";
        } else if(trend.equals("T3")){
            this.currentTrend = "T3";
        } else {
            this.currentTrend = "T0";            
        }
    }

    public void setScore(String trend, int scoreOfPlayer0, int scoreOfPlayer1) {
        if(trend.equals("T1")){
            this.player0points[0] = scoreOfPlayer0;
            this.player1points[0] = scoreOfPlayer1;
        } else if(trend.equals("T2")){
            this.player0points[1] = scoreOfPlayer0;
            this.player1points[1] = scoreOfPlayer1;
        } else if(trend.equals("T3")){
            this.player0points[2] = scoreOfPlayer0;
            this.player1points[2] = scoreOfPlayer1;
        } else {
        }
    }
    
    public void setSeason(int i){
        this.currentSeason = i;
    }
    
    public void setSeason(String seasonText){
        this.currentSeason = 0;
        for(int i=0;i<SEASON_NAMES.length;i++){
            if(SEASON_NAMES[i].equals(seasonText)){
                this.currentSeason = i;
                return;
            }
        }
    }
    
    
}
