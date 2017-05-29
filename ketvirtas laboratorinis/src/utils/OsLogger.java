package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by irmis on 2017.03.31.
 */
public class OsLogger {
    public static final int LEVEL_1 = 1;
    public static final int LEVEL_2 = 2;
    public static final int LEVEL_3 = 3;


    public static BufferedWriter log = null;
    public static void init(String filename) throws IOException {
        if(log == null) {
            FileWriter temp = new FileWriter(filename);
            log = new BufferedWriter(temp);
        }
    }
    public static void writeToLog(String msg, int level){
        if(log != null) {
            try {
                if(level == LEVEL_1){
                } else if(level == LEVEL_2) {
                    log.write(msg);
                    log.newLine();
                    log.flush();
                } else if(level == LEVEL_3){
                    log.write(msg);
                    log.newLine();
                    log.flush();
                    System.out.println(msg);
                }

            } catch (IOException e) {
                System.out.println("Unable to write to file");
            }
        } else{
            System.out.println("Logger is not open");
        }
    }
    public static void close() throws IOException {
        if(log != null){
            log.close();
        }
    }
}
