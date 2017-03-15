package Rm;

import testTools.Test;
import vm.Vm;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Rm {
    RmRegister mode;
    public static RmRegister ptr;
    RmRegister sp;
    RmRegister r1;
    RmRegister r2;
    RmRegister ch1;
    RmRegister ch2;
    RmRegister ch3;

    RmInterrupt si;
    RmInterrupt pi;

    public HDD hdd;
    public Memory memory;

    public RmStatusFlag sf;

    int ic;
    int ti;

    public Rm() {
        mode = new RmRegister(1, "mode");
        ptr = new RmRegister(2, "ptr");
        sp = new RmRegister(1, "sp");
        r1 = new RmRegister(4, "register 1");
        r2 = new RmRegister(4, "register 2");
        ch1 = new RmRegister(1, "channel 1");
        ch2 = new RmRegister(1, "channel 2");
        ch3 = new RmRegister(1, "channel 3");
        si = new RmInterrupt(InterruptType.SI);
        pi = new RmInterrupt(InterruptType.PI);
        sf = new RmStatusFlag();
        ic = 0;
        ti = 10;
        hdd = new HDD();
        memory = new Memory(this);
    }

    public void load(String programName) throws Exception {
        long pos = 0;
        int ret = 0;// 0 - CSEG; 1 - DSEG; 2 - HALT
        Vm vm = new Vm(this);
        //vm.ptr.data = Test.intToBytes(memory.getFreeBlock(), 4);
        byte[][] rmTable = memory.memory[Test.bytesToInt(Rm.ptr.data)];
        int vmPtr = -1;
        for(int i = 0; i < 16; i++){
            if(Test.bytesToInt(rmTable[i]) == 0) {
                vmPtr = i;
                break;
            }
        }
        if(vmPtr == -1){
            System.out.println("FUCK THIS SHIT IM OUT");
            throw new Exception("FUCK THIS SHIT IM OUT");
        }
        vm.ptr.data = Test.intToBytes(vmPtr,4);
        //memory.memory[Test.bytesToInt(Rm.ptr.data)][0] = vm.ptr.data;
        try {
            findFilePos(programName);
            while(true){
                String line = hdd.file.readLine();
                ret = memory.addToVm(line, vm, programName);
                //System.out.println(line);
                if(ret == 2)
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Load ended");
    }

    public void load(String fileName, String programName){
        byte[][] block = new byte[16][4];
        int counter = 0;
        byte[] temp = new byte[14];
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))){
            String buffer = in.readLine();
            if(buffer.equals("DSEG")) {
                while (in.ready()) {//Kol yra ka skaityti.
                    buffer = in.readLine();
                    //System.out.println("buffer " +  buffer);
                    if (buffer.equals("CSEG")){//Jei sutinkame CSEG, iseiname is ciklo
                        break;
                    }
                    if (buffer.startsWith("DW")) {
                        counter = addDataToBlock(block, buffer, counter); // Jei eilute prasideda 'DW', kviecieme funkcija,
                        // kuri prideda duomenis i bloka ir grazina nauja counter reiksme, jei ji pasikeite.

                        if (counter >= 15) { //Jei counter reiksme pasiekia bloko dydi, ji yra nunulinama ir blokas pridedamas
                            //i supervizoriaus atminti
                            memory.addToSupervisor(block, false, programName);
                            counter = 0;
                            block = new byte[16][4];
                        }
                    }
                }
            }
            memory.addToSupervisor(block, false, programName);
            counter = 0;
            block = new byte[16][4];

            if (buffer.equals("CSEG")){// Pabaigus DSEG, pereinam prie CSEG.
                while (in.ready()) {//Kol yra ka skaityti.
                    buffer = in.readLine();
                    //System.out.println(buffer);
                    if (buffer.equals("HALT")){//Jei sutinkame HALT, iseiname is ciklo
                        counter = addCodeToBlock(block, buffer, counter);
                        break;
                    }
                    counter = addCodeToBlock(block, buffer, counter); // Kviecieme funkcija,
                    // kuri prideda duomenis i bloka ir grazina nauja counter reiksme, jei ji pasikeite.
                    //validacija vyksta supervizoriaus atminty

                    if (counter > 15) { //Jei counter reiksme pasiekia bloko dydi, ji yra nunulinama ir blokas pridedamas
                        //i supervizoriaus atminti
                        memory.addToSupervisor(block, true, programName);
                        counter = 0;
                        block = new byte[16][4];
                    }
                }
                memory.addToSupervisor(block, true, programName);
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private int addDataToBlock(byte[][] block, String buffer, int counter){
        block[counter][0] = 'D';
        block[counter][1] = 'W';
        counter++;
        block[counter] = intToByte(buffer.substring(3));
        counter++;
        return counter;
    }

    private int addCodeToBlock(byte[][] block, String buffer, int counter){
        block[counter] = buffer.getBytes();
        counter++;
        return counter;
    }

    private byte[] intToByte(String string){

        /*byte[] buffer = new byte[4];
        int temp = Integer.valueOf(string);

        buffer[0] = (byte) (temp & 255);
        temp = temp >> 8;
        buffer[1] = (byte) (temp & 255);
        temp = temp >> 8;
        buffer[2] = (byte) (temp & 255);
        temp = temp >> 8;
        buffer[3] = (byte) (temp & 255);*/
        ByteBuffer temp = ByteBuffer.allocate(4);
        temp.putInt(Integer.valueOf(string));

        return temp.array();
    }

    private long findFilePos(String programName){
        byte[] word = new byte[5];
        byte[] pName = programName.getBytes();
        int counter = 0;
        int blockNr = 0;
        long blockPosition = 0;
        try {
            hdd.file.seek(0);
            while(true){
                hdd.file.read(word, 0, 5);
                if(pName[0] == word[0] && pName[1] == word[1]){
                    blockPosition = hdd.getBlockPosition();
                    break;
                }
                counter++;
                if(counter % 16 == 0){
                    hdd.file.read(word, 0, 1);
                }
            }
            hdd.file.seek(1296);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }// Found which file it is
        try {// now go to that file
            while(blockNr < counter) {
                String line = hdd.file.readLine();
                if (line.equals("$$$$")) {
                    blockNr++;
                    for (int i = 0; i < 16; i++) {
                        hdd.file.readLine();
                    }
                }
            }
            //System.out.println("FOUND IT!!");
            hdd.file.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockPosition;
    }

    public int getVirtualPtr(){
        return 0;
    }
}
