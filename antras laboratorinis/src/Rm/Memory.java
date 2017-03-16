package Rm;

import testTools.Test;
import vm.Vm;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Memory {
    byte[][][] memory;
    HDD hdd;
    boolean prevDW = false;
    boolean prevDT = false;

    public Memory(Rm rm) {
        memory = new byte[256][16][4];
        rm.ptr.data = ByteBuffer.allocate(4).putInt(2).array(); //rm.ptr yra registras, jo data laukas nustatomas i 2.
        // Tas laukas rodys i bloka, kuriame yra puslapiu lentele
        hdd = rm.hdd;

    }

    public void addToSupervisor(byte[][] block, boolean isCode, String name) throws IOException {
        memory[0] = block;
        hdd.writeToDisk(name, memory[0], 16);
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

    public int addToVm(String line, Vm vm, String programName){
        byte[][] pagesTable = memory[Test.bytesToInt(Rm.ptr.data)];
        String[] words = line.split(" ");
        int ret = 0;
        if(prevDW){
            memory[0][0] = ByteBuffer.allocate(4).putInt(Integer.parseInt(words[0])).array();
            prevDW = false;
        } else if (prevDT){
            memory[0][0] = words[0].getBytes();
            prevDT = false;
        }
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("DSEG")) {
                memory[0][i] = "DSEG".getBytes();
                ++i;
                ret = 0;
            }
            if (ret == 0) {
                if (words[i].equals("DW00")) {
                    memory[0][i] = "DW00".getBytes();
                    ++i;
                    if(i > 15) {
                        prevDW = true;
                        break;
                    }
                    memory[0][i] = ByteBuffer.allocate(4).putInt(Integer.parseInt(words[i])).array();// This should work
                } else if (words[i].equals("DT00")) {
                    memory[0][i] = "DT00".getBytes();
                    ++i;
                    if(i > 15) {
                        prevDT = true;
                        break;
                    }
                    memory[0][i] = words[i].getBytes();
                } else if (words[i].equals("CSEG")) {
                    memory[0][i] = "CSEG".getBytes();
                    ret = 1;
                }
            } else {
                if (words[i].equals("HALT")) {
                    memory[0][i] = "HALT".getBytes();
                    parseInSupervisor();
                    addToVm(vm, programName);
                    //showTrackMemory(Test.bytesToInt(pagesTable[Test.bytesToInt(vm.ptr.data)]));
                    return 2;
                }
                memory[0][i] = words[i].getBytes();
            }
        }
        parseInSupervisor();
        addToVm(vm, programName);

        //showTrackMemory(Test.bytesToInt(pagesTable[Test.bytesToInt(vm.ptr.data)]));
        return ret;
    }

    private void addToVm(Vm vm, String programName){
        byte[][] pagesTable = memory[Test.bytesToInt(Rm.ptr.data)];
        /*if(vm.ptr.blocksUsed == 0) {// if this is first track in the vm, then we need first to assign block in rm
            for (int i = 0; i < 16; i++) {
                if (Test.bytesToInt(pagesTable[i]) == 0) {
                    pagesTable[i] = Test.intToBytes(getFreeBlock(), 4);
                    vm.ptr.data = pagesTable[i];
                    break;
                }
            }
        }*/
        // vm already has assigned ptr, so just add these blocks where is free

        //copyBlock(memory[0], memory[freeBlock]);
        //memory[Test.bytesToInt(Rm.ptr.data)][vm.ptr.blocksUsed] = Test.intToBytes(freeBlock, 4);
        //memory[Test.bytesToInt(pagesTable[Test.bytesToInt(vm.ptr.data)])][vm.ptr.blocksUsed] = Test.intToBytes(freeBlock, 4);
        int rmPageTablePtr = Test.bytesToInt(Rm.ptr.data);
        int vmPageTablePtr = Test.bytesToInt(vm.ptr.data);
        if(vm.ptr.blocksUsed == 0){
            pagesTable[vmPageTablePtr] = Test.intToBytes(getFreeBlock(), 4);
        }
        //int temp = Test.bytesToInt(memory[rmPageTablePtr][Test.bytesToInt(memory[vmPageTablePtr][vm.ptr.blocksUsed])]);
        byte[][] vmPageTable = memory[Test.bytesToInt(memory[rmPageTablePtr][vmPageTablePtr])];
        int freeBlock = getFreeBlock();
        vmPageTable[vm.ptr.blocksUsed] = Test.intToBytes(freeBlock, 4);
        //memory[vmPageTablePtr][vm.ptr.blocksUsed] = Test.intToBytes(freeBlock, 4);
        copyBlock(memory[0], memory[freeBlock]);
        vm.ptr.blocksUsed++;
        //System.out.println(Test.bytesToInt(pagesTable[vm.ptr.blocksUsed - 1]));
    }

    private void copyBlock(byte[][] source, byte[][] dest){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 4; j++){
                dest[i][j] = source[i][j];
            }
        }
    }
    public int getFreeBlock(){
        int[] usedBlocks = getUsed();
        for(int i = 1; i < 256; i++){
            if(usedBlocks[i] == 0)
                return i;
        }
        return 10;// TODO need to implement this
    }

    private int[] getUsed(){
        int[] blocks = new int[256];
        for(int i = 0; i < 256; i++){
            blocks[i] = 0;
        }
        int ptr = Test.bytesToInt(Rm.ptr.data);
        blocks[ptr] = 1;

        byte[][] rmPtrBlock = memory[ptr];
        byte[][] vmPtrBlock;

        for(int i = 0; i < 16; i++){
            //vmPtrBlock = memory[Test.bytesToInt(rmPtrBlock[Test.bytesToInt(rmPtrBlock[i])])];
            int vmPtr = Test.bytesToInt(rmPtrBlock[i]);
            blocks[vmPtr] = 1;
            vmPtrBlock = memory[vmPtr];
            if(vmPtr != 0) {
                for (int j = 0; j < 16; j++) {
                    int temp = Test.bytesToInt(vmPtrBlock[j]);
                    try {
                        blocks[temp]++;// TODO change to "blocks[temp] = 1;"
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return blocks;
    }
    private void parseInSupervisor(){
        /*for(int i = 0; i < 16; i++){
            for(int j = 0; j < 4; j++){
                System.out.print((char)memory[0][i][j]);
            }
            System.out.print(" ");
        }*/

    }

    public int getInt(int i, int j){
        return ByteBuffer.wrap(memory[i][j]).getInt();
    }

    public void showTrackMemory(int blockNumber){
        int lastCommand = 0;
        for(int i = 0; i < 16; i++) {
            ByteBuffer buffer = ByteBuffer.wrap(memory[blockNumber][i]);
            int t = buffer.getInt();
            if(t != 0) {
                lastCommand = showBlockMemory(t, lastCommand);
            } else {
                for(int j = i; j < 16; j++){
                    System.out.println("0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000");
                }
                break;
            }

            //System.out.println();
        }

    }

    private int showBlockMemory(int t, int lastCommand){
        //lastCommand meaning 1 - DW; 2 - DT
        for (int j = 0; j < 16; j++) {
            if(lastCommand == 1) {
                ByteBuffer buffer2 = ByteBuffer.wrap(memory[t][j]);
                int t2 = buffer2.getInt();
                System.out.print(t2 + " ");
                lastCommand = 0;
                j++;
            } else if(lastCommand == 2) {
                String command = new String(memory[t][j]);
                System.out.print(command + " ");
                lastCommand = 0;
                j++;
            }
            if (isEqual(t,j,"DSEG") ) {
                System.out.print("DSEG ");
            } else if (isEqual(t,j, "DW00")) {
                System.out.print("DW00 ");
                j++;
                if (j < 16 ) {
                    ByteBuffer buffer2 = ByteBuffer.wrap(memory[t][j]);
                    int t2 = buffer2.getInt();
                    System.out.print(t2 + " ");
                } else
                    lastCommand = 1;

            } else if (isEqual(t,j, "DT00")) {
                System.out.print("DT00 ");
                j++;
                if (j < 16 ) {
                    String command = new String(memory[t][j]);
                    System.out.print(command + " ");
                } else
                    lastCommand = 2;
            } else if (isEqual(t,j, "CSEG")) {
                System.out.print("CSEG ");
                /*j++;
                while (j < 16) {
                    String command = new String(memory[t][j]);
                    if (command.equals("HALT")) {
                        System.out.print("HALT ");
                        j++;
                        while (j < 16) {
                            System.out.print("0000" + " ");
                            j++;
                        }
                    } else {
                        System.out.print(command + " ");
                        j++;
                    }
                }*/
            } else if (isEqual(t,j, "HALT")) {
                System.out.print("HALT ");
                j++;
                while (j < 16) {
                    System.out.print("0000 ");
                    j++;
                }
            } else{
                String command = new String(memory[t][j]);
                System.out.print(command + " ");
            }
        }
        System.out.println();
        return lastCommand;
    }

    private boolean isEqual(int t, int j, String command){
        byte[] array = command.getBytes();
        if (memory[t][j][0] == array[0] & memory[t][j][1] == array[1] & memory[t][j][2] == array[2] &
                memory[t][j][3] == array[3]) {
            return true;
        } else return false;
    }
}
