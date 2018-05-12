/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on 2018/04/28, 14:31:49
 */
package guiParts;

import gameElements.Board;
import gameElements.Game;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author ktajima
 */
public class GUIFrame extends javax.swing.JFrame implements Observer{
    ImagePanel ipanel;
    PlayerPanel[] playerPanel;

    @Override
    public void update(Observable source, Object arg) {
        if(source instanceof Game){
            Game game = (Game)source;
            this.rePaintImage(game);
            System.out.println(game.getBoardInformation());
            System.out.println(game.getResourceInformation());
            if(arg instanceof String){
                System.out.println(arg);
            }
        }
    }
    
    
    /** Creates new form ButtonBasedGUI */
    public GUIFrame() {
        initComponents();
        this.playerPanel = new PlayerPanel[2];
        this.playerPanel[0] = new PlayerPanel(0);
        this.playerPanel[1] = new PlayerPanel(1);

       this.jPanel1.setLayout(new GridLayout(1,1));
       this.jPanel2.setLayout(new GridLayout(1,1));
       this.jPanel3.setLayout(new GridLayout(1,1));
        
        this.rePaintImage(new Game());
    }
    
    private void rePaintImage(Game board){
        this.ipanel = new ImagePanel();
        this.jPanel2.removeAll();
        this.jPanel2.add(ipanel);
        this.jPanel2.validate();


        this.jPanel1.removeAll();
        this.jPanel1.add(this.playerPanel[0]);
        this.jPanel1.validate();

        this.jPanel3.removeAll();
        this.jPanel3.add(this.playerPanel[1]);
        this.jPanel3.validate();
        
        //名前の表示
        this.playerPanel[0].setNameText(board.getPlayerNameOf(0));
        this.playerPanel[1].setNameText(board.getPlayerNameOf(1));

        //ボード上のワーカーを取得して表示
        this.ipanel.resetAllWorkers();
        for(String key:Board.PLACE_NAMES){
            ArrayList<String> list = board.getWorkerNameOf(key);
            if(list != null){
                if(!list.isEmpty()){
                    for(String worker:list){
                        this.ipanel.AddWorker(key, worker);
                    }
                }
            }
        }
        
        //お金の表示
        this.playerPanel[0].setMoney((board.getResourcesOf(0).getCurrentMoney()));
        this.playerPanel[1].setMoney((board.getResourcesOf(1).getCurrentMoney()));
        //研究ポイントの表示
        this.playerPanel[0].setReserchPoint((board.getResourcesOf(0).getCurrentResrchPoint()));
        this.playerPanel[1].setReserchPoint((board.getResourcesOf(1).getCurrentResrchPoint()));
        //総得点の表示
        this.playerPanel[0].setTotalScore((board.getResourcesOf(0).getTotalScore()));
        this.playerPanel[1].setTotalScore((board.getResourcesOf(1).getTotalScore()));
        
        
        //残っているワーカーの表示
        this.playerPanel[0].setUnusedWorkers(
                board.getResourcesOf(0).getNumberofUseableWorkers("S"),
                board.getResourcesOf(0).getNumberofUseableWorkers("A"),
                board.getResourcesOf(0).getNumberofUseableWorkers("P")
        );
        this.playerPanel[1].setUnusedWorkers(
                board.getResourcesOf(1).getNumberofUseableWorkers("S"),
                board.getResourcesOf(1).getNumberofUseableWorkers("A"),
                board.getResourcesOf(1).getNumberofUseableWorkers("P")
        );
        
        //トレンドのセット
        this.ipanel.setTreand(board.getTrend());
        
        //得点の表示
        this.ipanel.setScore("T1",board.getScoreOf("T1", 0),board.getScoreOf("T1", 1));
        this.ipanel.setScore("T2",board.getScoreOf("T2", 0),board.getScoreOf("T2", 1));
        this.ipanel.setScore("T3",board.getScoreOf("T3", 0),board.getScoreOf("T3", 1));
       
        //季節のセット
        this.ipanel.setSeason(board.getSeason());
        
        //スタートプレイヤーのセット
        if(board.getStartPlayer() == 0){
            this.playerPanel[0].setStartPlayer(true);
            this.playerPanel[1].setStartPlayer(false);
        } else {
            this.playerPanel[0].setStartPlayer(false);
            this.playerPanel[1].setStartPlayer(true);
        }
        
        //手を待つプレイヤーをセット
        if(board.getGameState() == Game.STATE_WAIT_PLAYER_PLAY){
            if(board.getCurrentPlayer() == 0){
                this.playerPanel[0].setCurrentPlayer(true);
                this.playerPanel[1].setCurrentPlayer(false);
            } else if(board.getCurrentPlayer() == 1){
                this.playerPanel[0].setCurrentPlayer(false);
                this.playerPanel[1].setCurrentPlayer(true);
            }
        }

        this.repaint();
    }

    private void printMessage(String msg){
        System.out.println(msg);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 204, 204));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 181, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 504, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel2MouseClicked
        if(evt.getButton() == 1){
            Point p = evt.getPoint();
            String label = this.ipanel.getBoardClickedArea(p);
            if(label != null){
                System.out.println(label);
            }
        }
    }//GEN-LAST:event_jPanel2MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new GUIFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
