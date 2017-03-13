package Rm;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Memory {
    byte[][][] memory;
    HDD hdd;

    public Memory(Rm rm) {
        memory = new byte[256][16][4];
        rm.ptr.data[0] = (byte) 2; //rm.ptr yra registras, jo data laukas nustatomas i 2. Tas laukas rodys i bloka,
        //kuriame yra puslapiu lentele
        hdd = rm.hdd;

    }

    public void addToSupervisor(byte[][] block, boolean isCode, String name) throws IOException {
        memory[0] = block;
        int len = parse(isCode);
        hdd.writeToDisk(name, memory[0], len);
        /*for(int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++)
                System.out.print((char)block[i][j]);
            System.out.println();
        }*/
    }

    private int parse(boolean isCode) {
        if (isCode) {
            for(int i = 0; i < 16; i++){
                if(memory[0][i][0] == 0)
                    return i;
                if(memory[0][i].length < 3){
                    byte[] t = memory[0][i];
                    memory[0][i] = new byte[4];
                    for(int j = 0; j < t.length; j++){
                        memory[0][i][j] = t[j];
                    }
                    for(int j = t.length; j < 4; j++){
                        memory[0][i][j] = '0';
                    }
                }
            }

        } else {
            for(int i = 0; i < 8; i++){
                if(memory[0][i * 2][0] == 0)
                    return i * 2;
            }
        }
        return 16;
    }
}
