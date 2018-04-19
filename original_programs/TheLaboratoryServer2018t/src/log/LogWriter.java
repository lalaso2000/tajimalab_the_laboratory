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
                writer.write(((Game) source).getBoardInformation() + "\n");
                writer.write(((Game) source).getResourceInformation() + "\n");
                if (arg instanceof String) {
                    writer.write(arg.toString());
                }
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
