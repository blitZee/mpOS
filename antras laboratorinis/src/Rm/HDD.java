package Rm;

import java.io.*;

/**
 * Created by blitZ on 3/8/2017.
 */
public class HDD implements Closeable {
    RandomAccessFile file;
    byte[] word;

    public HDD() {
        word = new byte[5];

        try {
            file = new RandomAccessFile("HDD.txt", "rwd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String readFromMemory(){
        return new String("your mom");
    }

    public void writeToMemory(){

    }

    public void writeToDisk(String name){
        byte[] bName = new byte[2];
        bName = name.getBytes();

        try {
            while(true) {
                file.read(word, 0, 5);
                if(bName[0] == word[0] && bName[1] == word[1]){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        file.write();
    }

    @Override
    public void close() throws IOException {
        file.close();
    }
}
