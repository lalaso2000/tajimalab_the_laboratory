/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import gameElements.Game;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author niwatakumi
 */
public class LogWriter implements Observer {

    File file;
    FileWriter writer;

    public LogWriter(String filePath) {
        try {
            file = new File(filePath);
            writer = new FileWriter(file);
            String line = "Num\t";
            line += "Season\t";
            line += "1-1(Seminar)\t";
            line += "2-1(Experiment)\t";
            line += "2-2(Experiment)\t";
            line += "2-3(Experiment)\t";
            line += "3-1(Presentation)\t";
            line += "3-2(Presentation)\t";
            line += "3-3(Presentation)\t";
            line += "4-1(Paper)\t";
            line += "4-2(Paper)\t";
            line += "4-3(Paper)\t";
            line += "5-1(Report)\t";
            line += "5-2(Report)\t";
            line += "5-3(Report)\t";
            line += "6-1(Employ)\t";
            line += "6-2(Employ)\t";
            line += "Trend\t";
            line += "player0_T1_Score\t";
            line += "player1_T1_Score\t";
            line += "player0_T2_Score\t";
            line += "player1_T2_Score\t";
            line += "player0_T3_Score\t";
            line += "player1_T3_Score\t";
            line += "player0_P\t";
            line += "player0_A\t";
            line += "player0_S\t";
            line += "player0_StudentCount\t";
            line += "player0_Moey\t";
            line += "player0_ReserchPoint\t";
            line += "player0_TotalScore\t";
            line += "player1_P\t";
            line += "player1_A\t";
            line += "player1_S\t";
            line += "player1_StudentCount\t";
            line += "player1_Moey\t";
            line += "player1_ReserchPoint\t";
            line += "player1_TotalScore";
            line += "\n";
            writer.write(line);
        } catch (IOException ex) {
            System.err.println("ログファイルエラー");
            Logger.getLogger(LogWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(Observable source, Object arg) {
        if (source instanceof Game) {
            try {
                writer.flush();
                String line = ((Game) source).getInfomationForTsv();
                if (line != null) {
                    writer.write(line);
                }
//                if (arg instanceof String) {
//                    writer.write(arg.toString());
//                }
                writer.flush();
            } catch (IOException e) {
                System.err.println("ログファイルエラー");
                Logger.getLogger(LogWriter.class.getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
        fileClose();
    }

    public void fileClose() {
        try {
            writer.close();
        } catch (IOException e) {
            System.err.println("ログファイルエラー");
            Logger.getLogger(LogWriter.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
