package Rm;

import testTools.Constants;
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
        if(getVmDescriptor(programName) != null){
            System.out.println("Program with this name already exists");
            return;
        }

        int ret = 0;
        int vmPtr = getVmPtr();// assign space in rm ptr. Value can be from 0 to 15
        if (vmPtr == -1) {
            System.out.println("Out of space to add vm to pages table");
            return;
        }
        Vm vm = new Vm(this);// create vm only after we know that there are enough space in rm ptr
        byte[][] rmTable = memory.memory[Test.bytesToInt(Rm.ptr.data)];
        vm.ptr.data = Test.intToBytes(vmPtr, 4);
        rmTable[vmPtr] = Test.intToBytes(memory.getFreeBlock(), 4);
        try {
            findFilePos(programName);
            while (true) {
                String line = hdd.file.readLine();
                ret = memory.addToVm(line, vm, programName, ret);
                if (ret == Constants.HALT)
                    break;
            }
        } catch (IOException e) {
            //removeVm(vm);
            //System.out.println("Removed vm");
            System.out.println("IO Exception");
            // e.printStackTrace();
        } catch (Exception e) {
            removeVm(vm);
            System.out.println("Removed vm");
            System.out.println("Incorrect code");
        }
        System.out.println("Load ended");
        //now add to vm list
        byte[][] vmListTable = memory.memory[memory.vmList];
        byte[][] vmDescriptor = memory.memory[Test.bytesToInt(vmListTable[vm.ptr.getDataInt()])];
        vmDescriptor[Constants.VM_NAME_INDEX] = programName.getBytes();
        vmDescriptor[Constants.VM_CS_INDEX] = vm.cs.data;
        vmDescriptor[Constants.VM_DS_INDEX] = vm.ds.data;
        vmDescriptor[Constants.VM_PTR_INDEX] = vm.ptr.data;
    }

    private long findFilePos(String programName) {
        byte[] word = new byte[5];
        byte[] pName = programName.getBytes();
        int counter = 0;
        int blockNr = 0;
        long blockPosition = 0;
        try {
            hdd.file.seek(0);
            while (true) {
                hdd.file.read(word, 0, 5);
                if (pName[0] == word[0] && pName[1] == word[1]) {
                    blockPosition = hdd.getBlockPosition();
                    break;
                }
                counter++;
                if (counter % 16 == 0) {
                    hdd.file.read(word, 0, 1);
                }
            }
            hdd.file.seek(1296);
        } catch (Exception ex) {
            ex.printStackTrace();
        }// Found which file it is
        try {// now go to that file
            while (blockNr < counter) {
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

    private int getVmPtr() {
        byte[][] rmTable = memory.memory[Test.bytesToInt(Rm.ptr.data)];
        int vmPtr = -1;
        for (int i = 0; i < 16; i++) {
            if (Test.bytesToInt(rmTable[i]) == 0) {
                vmPtr = i;
                break;
            }
        }
        return vmPtr;
    }

    public void showBlock(String programName) {
        byte[][] vmListTable = memory.memory[memory.vmList];
        byte[][] vmDescriptor;
        for(int i = 0; i < 16; i++){
            vmDescriptor = memory.memory[Test.bytesToInt(vmListTable[i])];
            if(namesEqual(vmDescriptor[Constants.VM_NAME_INDEX], programName.getBytes()))
            {
                byte[][] rmTable = memory.memory[Test.bytesToInt(Rm.ptr.data)];
                memory.showTrackMemory(ByteBuffer.wrap(rmTable[i]).getInt());
                break;
            }
        }
    }

    public byte[][] getVmDescriptor(String programName){
        byte[][] vmListTable = memory.memory[memory.vmList];
        byte[][] vmDescriptor;
        for(int i = 0; i < 16; i++){
            vmDescriptor = memory.memory[Test.bytesToInt(vmListTable[i])];
            if(namesEqual(vmDescriptor[Constants.VM_NAME_INDEX], programName.getBytes()))
            {
                return vmDescriptor;
            }
        }
        return null;
    }

    private void removeVm(Vm vm) {
        int rmPtr = Test.bytesToInt(Rm.ptr.data);
        byte[][] rmPtrTable = memory.memory[rmPtr];
        int vmPtr = Test.bytesToInt(vm.ptr.data);
        byte[][] vmPtrTable = memory.memory[Test.bytesToInt(rmPtrTable[vmPtr])];
        rmPtrTable[vmPtr] = Test.intToBytes(0, 4);
        for (int i = 0; i < 16; i++) {
            byte[][] track = memory.memory[Test.bytesToInt(vmPtrTable[i])];
            vmPtrTable[i] = Test.intToBytes(0, 4);
            for (int j = 0; j < 16; j++) {
                track[j] = Test.intToBytes(0, 4);
            }
        }
        byte[][] vmListTable = memory.memory[memory.vmList];
        byte[][] vmDescriptor = memory.memory[Test.bytesToInt(vmListTable[vm.ptr.getDataInt()])];
        for(int i = 0; i < 16; i++){
            vmDescriptor[i] = Test.intToBytes(0, 4);
        }
    }

    public void removeVm(String programName){
        int rmPtr = Test.bytesToInt(Rm.ptr.data);
        byte[][] rmPtrTable = memory.memory[rmPtr];
        byte[][] descriptor = getVmDescriptor(programName);
        if(descriptor == null){
            System.out.println("No such program");
            return;
        }
        int vmPtr = Test.bytesToInt(descriptor[Constants.VM_PTR_INDEX]);
        byte[][] vmPtrTable = memory.memory[Test.bytesToInt(rmPtrTable[vmPtr])];
        rmPtrTable[vmPtr] = Test.intToBytes(0, 4);
        for (int i = 0; i < 16; i++) {
            byte[][] track = memory.memory[Test.bytesToInt(vmPtrTable[i])];
            vmPtrTable[i] = Test.intToBytes(0, 4);
            for (int j = 0; j < 16; j++) {
                track[j] = Test.intToBytes(0, 4);
            }
        }
        for(int i = 0; i < 16; i++){
            descriptor[i] = Test.intToBytes(0, 4);
        }

    }

    private boolean namesEqual(byte[] arr1, byte[] arr2){
        return arr1[0] == arr2[0] && arr1[1] == arr2[1];
    }

}
