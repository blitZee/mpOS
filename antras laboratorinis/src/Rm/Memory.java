package Rm;

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


    }

    public void addToSupervisor(byte[][] block, boolean isCode, String name){
        memory[0] = block;
        parse(isCode);

        /*for(int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++)
                System.out.print((char)block[i][j]);
            System.out.println();
        }*/
    }

    private void parse(boolean isCode){
        if(isCode){

        }
        else{

        }

    }
}
