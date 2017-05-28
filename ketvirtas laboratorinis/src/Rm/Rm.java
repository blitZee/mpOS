package Rm;

import processes.*;
import resources.*;
import testTools.Constants;
import utils.Utils;
import utils.OsLogger;
import vm.Vm;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Rm implements Runnable{
    public static boolean stepMode = false;

    static RmRegister mode;
    public static RmRegister ptr;
    static RmRegister r1;
    static RmRegister r2;
    static RmRegister ch1;
    static RmRegister ch2;
    static RmRegister ch3;

    static RmInterrupt si;
    static RmInterrupt pi;

    public static HDD hdd;
    public static Memory memory;

    public static RmStatusFlag sf;

    public static int timer = 10;
    static int ic;
    static int ti;
    public static boolean cont = true;
    public static ArrayList<Vm> VmList = new ArrayList();
    private static ArrayList<Resource> resourceList = new ArrayList<>();
    public static ArrayList<MIKOSProcess> processes = new ArrayList<>();
    public static ArrayList<MIKOSProcess> processesToAdd = new ArrayList<>();
    public static ArrayList<MIKOSProcess> processesToRemove = new ArrayList<>();
    public static void init() {
        /*processes.add(new ProcessManager());
        processes.add(new ResourceManager());
        processes.add(new Loader());
        processes.add(new JCL());*/
        mode = new RmRegister(1, "mode");
        ptr = new RmRegister(2, "ptr");
        r1 = new RmRegister(4, "register 1");
        r2 = new RmRegister(4, "register 2");
        ch1 = new RmRegister(1, "channel 1");
        ch2 = new RmRegister(1, "channel 2");
        ch3 = new RmRegister(1, "channel 3");
        si = new RmInterrupt(InterruptType.NO_INTERRUPT);
        pi = new RmInterrupt(InterruptType.NO_INTERRUPT);
        sf = new RmStatusFlag();
        ic = 0;
        ti = 10;
        hdd = new HDD();
        memory = new Memory();
    }
    @Override
    public void run(){
        try {
            OsLogger.init("logger.txt");
            new StartStop().doProcess(null);
            Rm.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        while (cont) {
            synchronized (Rm.class) {
                Rm.callResourceManager();
                Rm.callProcessManager();
                //System.out.println("hello");
            }
        }
        try {
            Rm.hdd.close();
            OsLogger.close();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void callResourceManager(){
        for(MIKOSProcess p : processes){
            if(p.ID.equals("ResourceManager")){
                p.doProcess(null);
                return;
            }
        }
    }

    public static void callProcessManager(){
        for(MIKOSProcess p : processes){
            if(p.ID.equals("ProcessManager")){
                p.doProcess(null);
                return;
            }
        }
    }

    /*public static void start(String programName) {
        Vm vmDescriptor = getVmDescriptor(programName);
        if (vmDescriptor == null) {
            //System.out.println("No such program");
            OsLogger.writeToLog("No program named " + programName + " to start");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        Vm vm = vmDescriptor;
        int[] codeBeginning = getCodeBeginning(vm);
        int[] indexes;
        //System.out.println("row: " + codeBeginning[0]);
        //System.out.println("column: " + codeBeginning[1]);
        int row = codeBeginning[0];
        int column = codeBeginning[1];
        byte[] command;
        boolean cont;// continue
        while (true) {
            command = getCommand(vm, row, column);
            if(stepMode){
                showRegister(vm);
                System.out.println("Next command: " + new String(command));
                while(true) {
                    String line = scanner.nextLine();
                    if (line.toUpperCase().equals("SHOW CS")) {
                        showCseg(programName);
                    } else if (line.toUpperCase().equals("SHOW DS")) {
                        showDseg(programName);
                    } else if (line.toUpperCase().equals("FINISH")) {
                        stepMode = false;
                        break;
                    }
                    if (line.equals("")) {
                        break;
                    }
                }
            }
            cont = executeCommand(vm, command);
            vm.ic++;
            timer--;
            if (!cont) {
                break;
            }
            indexes = getNextIndexes(vm);
            row = indexes[0];
            column = indexes[1];
            test(vm);
        }
    }*/

    public static long findFilePos(String programName) {
        byte[] word = new byte[5];
        byte[] pName = programName.getBytes();
        int counter = 0;
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
                if(counter > 256){
                    return -1;
                }
            }
            hdd.file.seek(1296);
        } catch (Exception ex) {
            ex.printStackTrace();
        }// Found which file it is
        goToFilePosition(counter);
        return blockPosition;
    }

    public static int getFilePos(int x, int y, boolean open) {
        byte[] word = new byte[5];
        byte[] pName = new byte[2];
        pName[0] = (byte) x;
        pName[1] = (byte) y;
        int counter = 0;
        try {
            hdd.file.seek(0);
            while (true) {
                hdd.file.read(word, 0, 5);
                if (pName[0] == word[0] && pName[1] == word[1]) {
                    if(word[2] == '1')
                        return -1;
                    hdd.file.seek(hdd.file.getFilePointer() - 3);
                    hdd.file.writeByte('1');
                    break;
                }
                counter++;
                if (counter % 16 == 0) {
                    hdd.file.read(word, 0, 1);
                }
                if(counter > 256){
                    return -1;
                }
            }
            hdd.file.seek(1296);
        } catch (Exception ex) {
            ex.printStackTrace();
        }// Found which file it is


        return counter;
    }

    public static void closeFile(byte[] handler){
        byte[] word = new byte[5];
        byte[] temp = {0, 0, handler[2], handler[3]};
        int hndl = Utils.bytesToInt(temp);
        int counter = 0;
        try {
            hdd.file.seek(0);
            for(int i = 0; i < hndl; i++) {
                hdd.file.read(word, 0, 5);
                counter++;
                if (counter % 16 == 0) {
                    hdd.file.read(word, 0, 1);
                }
            }
            hdd.file.seek(hdd.file.getFilePointer() + 2);
            hdd.file.writeByte('0');
        } catch (Exception ex) {
            ex.printStackTrace();
        }// Found which file it is

    }

    public static byte[] fileRead(byte[] dr1){
        //DR1 - file handler
        // 10*x + y - vieta, i kuria rasysim duomenu segmente
        //DR2 - adresas is kurio skaitysim
        byte[] temp1 = {0, 0, dr1[2], dr1[3]};
        int dr1Int = ByteBuffer.wrap(temp1).getInt();
        try {
            hdd.file.seek(1296);
        } catch (IOException e) {
            e.printStackTrace();
        }
        goToFilePosition(dr1Int);
        try {
            byte[] temp = new byte[5];
            byte[] temp2 = {0, 0, dr1[0], dr1[1]};
            int readPosition = Utils.bytesToInt(temp2);
            for(int i = 0; i < readPosition; i++){
                if((i + 1) % 16 == 0)
                    hdd.file.readByte();
                hdd.file.read(temp, 0, 5);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] word = new byte[4];
        try {
            hdd.file.read(word, 0, 4);
            try {
                //vm.saveData(x, y, word);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return word;
    }

    private static void goToFilePosition(int handler){
        int blockNr = 0;
        try {// now go to that file
            hdd.file.seek(1296);
            while (blockNr < handler) {
                String line = hdd.file.readLine();
                if (line.equals("$$$$")) {
                    blockNr++;
                    for (int i = 0; i < 16; i++) {
                        hdd.file.readLine();
                    }
                }
            }
            hdd.file.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void fileWrite(byte[] dr1, byte[] data){
        byte[] filePos = {0, 0, dr1[2], dr1[3]};
        byte[] positionToWrite = {0, 0, dr1[0], dr1[1]};
        goToFilePosition(Utils.bytesToInt(filePos));
        try {
            byte[] temp = new byte[5];
            int writePosition = Utils.bytesToInt(positionToWrite);
            for(int i = 0; i < writePosition; i++){
                if((i + 1) % 16 == 0)
                    hdd.file.readByte();
                hdd.file.read(temp, 0, 5);
            }
            hdd.file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void deleteFile(byte[] dr1){
        int hndl = Utils.bytesToInt(dr1);
        byte[] word = new byte[5];
        int counter = 0;
        try {
            hdd.file.seek(0);

            for(int i = 0; i < hndl; i++) {
                hdd.file.read(word, 0, 5);
                counter++;
                if (counter % 16 == 0) {
                    hdd.file.read(word, 0, 1);
                }
            }
            hdd.file.write("0000".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        goToFilePosition(Utils.bytesToInt(dr1));
        byte[] newLine = "0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000\r\n".getBytes();
        for(int i = 0; i < 16; i++){
            try {
                hdd.file.write(newLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getVmPtr() {
        byte[][] rmTable = memory.memory[Utils.bytesToInt(Rm.ptr.data)];
        int vmPtr = -1;
        for (int i = 0; i < 16; i++) {
            if (Utils.bytesToInt(rmTable[i]) == 0) {
                vmPtr = i;
                break;
            }
        }
        return vmPtr;
    }

    public static void showBlock(String programName) {
        Vm vmDescriptor = null;
        for(int i = 0; i < 16; i++){
            //vmDescriptor = memory.memory[Utils.bytesToInt(vmListTable[i])];
            vmDescriptor = getVmDescriptor(programName);
            if(vmDescriptor.name.equals(programName))
            {
                byte[][] rmTable = memory.memory[Utils.bytesToInt(Rm.ptr.data)];
                memory.showTrackMemory(ByteBuffer.wrap(rmTable[i]).getInt());
                break;
            }
        }
    }

    public static void showCseg(int id){
        memory.showCodeSegment(id);
    }

    public static void showDseg(int id){
        memory.showDataSegment(id);
    }

    public static Vm getVmDescriptor(int id){
        //byte[][] vmListTable = memory.memory[memory.vmList];
        for(Vm vm : VmList){
            if(vm.id == id)
                return vm;
        }
        return null;
    }

    public static Vm getVmDescriptor(String programName){
        for(Vm temp : VmList){
            if(temp.name.equals(programName))
                return temp;
        }

        return null;
    }

    public static void removeVm(Vm vm) {
        int rmPtr = Utils.bytesToInt(Rm.ptr.data);
        byte[][] rmPtrTable = memory.memory[rmPtr];
        int vmPtr = Utils.bytesToInt(vm.ptr.data);
        byte[][] vmPtrTable = memory.memory[Utils.bytesToInt(rmPtrTable[vmPtr])];
        rmPtrTable[vmPtr] = Utils.intToBytes(0, 4);
        for (int i = 0; i < 16; i++) {
            byte[][] track = memory.memory[Utils.bytesToInt(vmPtrTable[i])];
            vmPtrTable[i] = Utils.intToBytes(0, 4);
            for (int j = 0; j < 16; j++) {
                track[j] = Utils.intToBytes(0, 4);
            }
        }
        VmList.remove(vm);
    }

    /*public void removeVm(String programName){
        int rmPtr = Utils.bytesToInt(Rm.ptr.data);
        byte[][] rmPtrTable = memory.memory[rmPtr];
        byte[][] descriptor = getVmDescriptor(programName);
        if(descriptor == null){
            System.out.println("No such program");
            return;
        }
        int vmPtr = Utils.bytesToInt(descriptor[Constants.VM_PTR_INDEX]);
        byte[][] vmPtrTable = memory.memory[Utils.bytesToInt(rmPtrTable[vmPtr])];
        rmPtrTable[vmPtr] = Utils.intToBytes(0, 4);
        for (int i = 0; i < 16; i++) {
            byte[][] track = memory.memory[Utils.bytesToInt(vmPtrTable[i])];
            vmPtrTable[i] = Utils.intToBytes(0, 4);
            for (int j = 0; j < 16; j++) {
                track[j] = Utils.intToBytes(0, 4);
            }
        }
        for(int i = 0; i < 16; i++){
            descriptor[i] = Utils.intToBytes(0, 4);
        }

    }*/

    public static void removeVm(int id){
        int rmPtr = Utils.bytesToInt(Rm.ptr.data);
        byte[][] rmPtrTable = memory.memory[rmPtr];
        Vm descriptor = getVmDescriptor(id);
        if(descriptor == null){
            System.out.println("No such program");
            return;
        }
        int vmPtr = Utils.bytesToInt(descriptor.ptr.data);
        byte[][] vmPtrTable = memory.memory[Utils.bytesToInt(rmPtrTable[vmPtr])];
        rmPtrTable[vmPtr] = Utils.intToBytes(0, 4);
        for (int i = 0; i < 16; i++) {
            byte[][] track = memory.memory[Utils.bytesToInt(vmPtrTable[i])];
            vmPtrTable[i] = Utils.intToBytes(0, 4);
            for (int j = 0; j < 16; j++) {
                track[j] = Utils.intToBytes(0, 4);
            }
        }
        VmList.remove(descriptor);
    }

    private static int[] getCodeBeginning(Vm vm){
        int temp = Utils.bytesToInt(vm.cs.data);
        int[] ret = new int[2];
        ret[0] = temp / 16;// row
        ret[1] = temp % 16;// column
        return ret;
    }

    public static byte[] getCommand(Vm vm, int row, int col){
        byte[] ret;
        byte[][] rmPtrTable = memory.memory[ptr.getDataInt()];
        byte[][] vmPtrTable = memory.memory[Utils.bytesToInt(rmPtrTable[vm.ptr.getDataInt()])];
        byte[][] rowInTable = memory.memory[Utils.bytesToInt(vmPtrTable[row])];
        ret = rowInTable[col];
        return ret;
    }

    public static int[] getNextIndexes(Vm vm){
        int temp = Utils.bytesToInt(vm.cs.data) + vm.ic;
        int[] ret = new int[2];
        ret[0] = temp / 16;// row
        ret[1] = temp % 16;// column
        return ret;
    }

    public static boolean executeCommand(Vm vm, byte[] command){// returns false if command is HALT
        String cmd = new String(command);
        switch(cmd){
            case "ADRR":
                vm.adrr();
                return true;
            case "SBRR":
                vm.sbrr();
                return true;
            case "MLRR":
                vm.mlrr();
                return true;
            case "DVRR":
                vm.dvrr();
                return true;
            case "MOV1":
                vm.mov1();
                return true;
            case "MOV2":
                vm.mov2();
                return true;
            case "PRNT":
                vm.prnt();
                return true;
            case "PRNS":
                vm.prns();
                return true;
            case "PUSH":
                vm.push();
                return true;
            case "HALT":
                vm.halt();
                return false;
        }
        switch (cmd.substring(0, 3)){
            case "AND":
                vm.and();
                return true;
            case "XOR":
                vm.xor();
                return true;
            case "NOT":
                vm.not();
                return true;
            case "CMP":
                vm.cmp();
                return true;
            case "POP":
                vm.pop();
                return true;
        }
        switch (cmd.substring(0, 2)){
            case "AD":
                vm.ad(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "SB":
                vm.sb(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "ML":
                vm.ml(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "DV":
                vm.dv(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "LW":
                vm.lw(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "SW":
                vm.sw(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "JM":
                vm.jm(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "JE":
                vm.je(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "JA":
                vm.ja(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "JL":
                vm.jl(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "FO":
                vm.fo(cmd.charAt(2), cmd.charAt(3));
                return true;
            case "FR":
                vm.fr(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "FW":
                vm.fw(Utils.hexToInt(cmd.toUpperCase().charAt(2)), Utils.hexToInt(cmd.toUpperCase().charAt(3)));
                return true;
            case "OR":
                vm.or();
                return true;
            case "FC":
                vm.fc();
                return true;
            case "FD":
                vm.fd();
                return true;
        }
        setSI(InterruptType.UNDEFINED_OPERATION);
        System.out.println("SOMETHING HORRIBLE JUST HAPPENED");
        return false;
    }

    public static void showRegister(Vm vm){
        int numOfSpaces = 11;
        byte[][] realTable = memory.memory[ptr.getDataInt()];
        int vmPtr = Utils.bytesToInt(realTable[vm.ptr.getDataInt()]);
        System.out.println("REGISTERS: PTR         R1         R2         DS         CS         TI         IC");
        System.out.println(String.format("   %" + numOfSpaces + "s" + "%" + numOfSpaces + "s" +"%" + numOfSpaces + "s"
                        +"%" + numOfSpaces + "s" +"%" + numOfSpaces + "s"  +"%" + numOfSpaces + "s"
                        +"%" + numOfSpaces + "s",
                String.valueOf(vmPtr),
                String.valueOf(vm.r1.getDataInt()),
                String.valueOf(vm.r2.getDataInt()),
                String.valueOf(vm.ds.getDataInt()),
                String.valueOf(vm.cs.getDataInt()),
                timer,
                vm.ic));
    }
    public static InterruptType test(Vm vm){
        if(si.type != InterruptType.NO_INTERRUPT){
            System.out.println("System interrupt");
        }
        if(pi.type != InterruptType.NO_INTERRUPT){
            System.out.println("Program interrupt");
        }
        if(timer <= 0){
            System.out.println("Timer interupt");
            timer = 10;
            return InterruptType.TIMER_INTERRUPT;
        }
        if(si.type == InterruptType.DUPLICATE_NAME){
            System.out.println("Interrupt: Duplicate name");
            OsLogger.writeToLog("Interrupt: Duplicate name");
            return InterruptType.DUPLICATE_NAME;
        }
        if(si.type == InterruptType.INCORRECT_FILE_NAME){
            System.out.println("Interrupt: Incorrect file name");
            OsLogger.writeToLog("Interrupt: Incorrect file name");
            return  InterruptType.INCORRECT_FILE_NAME;
        }
        if(si.type == InterruptType.OUT_OF_MEMORY){
            System.out.println("Interrupt: Out of memory");
            OsLogger.writeToLog("Interrupt: Out of memory");
            // TODO: remove vm
            return InterruptType.OUT_OF_MEMORY;
        }
        if(si.type == InterruptType.UNDEFINED_OPERATION_WHILE_LOADING){
            System.out.println("Interrupt: Undefined operation while loading");
            OsLogger.writeToLog("Interrupt: Undefined operation while loading");
            // TODO: remove vm
            return InterruptType.UNDEFINED_OPERATION_WHILE_LOADING;
        }
        if(pi.type == InterruptType.INCORRECT_FILE_HANLDE){
            System.out.println("Interrupt: Incorrect file handle");
            OsLogger.writeToLog("Interrupt: Incorrect file handle");
            return InterruptType.INCORRECT_FILE_HANLDE;
        }
        si.type = InterruptType.NO_INTERRUPT;
        pi.type = InterruptType.NO_INTERRUPT;
        return InterruptType.NO_INTERRUPT;
    }

    public static void setSI(InterruptType interupt){
        si.type = interupt;
    }

    public static void setPI(InterruptType interrupt){
        pi.type = interrupt;
    }

    private static boolean namesEqual(byte[] arr1, byte[] arr2){
        return arr1[0] == arr2[0] && arr1[1] == arr2[1];
    }

    public static void addResource(Type resourceType, String content, resources.State state){
        Resource resource = new Resource();
        resource.type = resourceType;
        resource.content = content;
        resource.state = state;
        resourceList.add(resource);
    }

    public static Iterator<Resource> getResourceListIterator(){
        return resourceList.iterator();
    }

    public static Vm getVm(int id){
        for(Vm vm : VmList){
            if(vm.id == id){
                return vm;
            }
        }
        return null;
    }

}
