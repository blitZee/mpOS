package vm;

import Rm.InterruptType;
import resources.Type;
import utils.Utils;
import utils.OsLogger;
import Rm.*;
import java.nio.ByteBuffer;

/**
 * Created by irmis on 2017.03.15.
 */
public class Vm {
    private int defaultLogLevel = OsLogger.LEVEL_2;


    private static int counter = 0;
    public int id;
    public int iterations;

    public VmRegister ptr;
    public VmRegister r1;
    public VmRegister r2;
    public VmRegister ds;
    public VmRegister cs;
    public VmRegister sp;

    public VmStatusFlag sf;

    public int ic;
    public String name;
    public Vm(String programName) {
        ptr = new VmRegister(4, "puslapiavimo lentele");
        for (int i = 0; i < 16; i++) {
            if (Rm.memory.getInt(Utils.bytesToInt(Rm.ptr.data), i) == 0) {
                ptr.data = Utils.intToBytes(i, 4);
            }
        }
        sp = new VmRegister(1, "sp");
        r1 = new VmRegister(4, "register 1");
        r2 = new VmRegister(4, "register 2");
        ds = new VmRegister(4, "data segment");
        cs = new VmRegister(4, "code segment");
        sf = new VmStatusFlag();
        ds.data = Utils.intToBytes(0, 4);
        ic = 0;
        name = programName;
        id = counter++;
        iterations = 0;
    }

    public void adrr() {
        StringBuilder sb = new StringBuilder();
        long temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        temp += temp2;

        sb.append("; result: " + temp);

        if (temp > 9999) {
            temp -= 9999;
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }

        OsLogger.writeToLog("ADRR; " + sb, defaultLogLevel);
    }

    public void ad(int x, int y) {
        StringBuilder sb = new StringBuilder();
        long temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        try {
            buffer = ByteBuffer.wrap(getData(x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }
        temp2 = buffer.getInt();
        sb.append(temp2);

        temp += temp2;

        sb.append("; result: " + temp);

        if (temp > 9999) {
            temp -= 9999;
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
        OsLogger.writeToLog("AD " + x + " " + y + "; " + sb, defaultLogLevel);
    }

    public void sbrr() {
        StringBuilder sb = new StringBuilder();
        int temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values ");
        sb.append(temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();
        sb.append(temp2);

        temp -= temp2;

        sb.append("; result: " + temp);
        if (temp < 0) {
            temp += 9999;
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
        OsLogger.writeToLog("SBRR; " + sb, defaultLogLevel);
    }

    public void sb(int x, int y) {
        StringBuilder sb = new StringBuilder();
        //System.out.println("SB " + x + y);
        int temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");
        try {
            buffer = ByteBuffer.wrap(getData(x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }
        temp2 = buffer.getInt();
        temp -= temp2;

        sb.append(temp2);
        sb.append("; result: " + temp);
        if (temp < 0) {
            temp += 9999;
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
        OsLogger.writeToLog("SB" + x + " " + y  + "; " + sb, defaultLogLevel);
    }

    public void mlrr() {
        StringBuilder sb = new StringBuilder();
        long temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        temp = temp * temp2;

        sb.append("; result: " + temp);
        if (temp > 9999) {
            temp = temp - (9999 * (temp / 9999));
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
        OsLogger.writeToLog("MLRR; " + sb, defaultLogLevel);
    }

    public void ml(int x, int y) {
        StringBuilder sb = new StringBuilder();
        long temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        try {
            buffer = ByteBuffer.wrap(getData(x, y));
            temp2 = buffer.getInt();
            temp *= temp2;

            sb.append(temp2);
            sb.append("; result: "+ temp);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (temp > 9999) {
            temp = temp - (9999 * (temp / 9999));
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
        OsLogger.writeToLog("ML" + x + " " + y + "; " + sb, defaultLogLevel);

    }

    public void dvrr(){
        StringBuilder sb = new StringBuilder();
        int temp, temp2, temp3;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        if (temp2 == 0) {
            //TODO:set interupt to division by zero
        } else {
            buffer = ByteBuffer.allocate(4);
            buffer.putInt(temp / temp2);
            temp3 = buffer.getInt();

            sb.append("; result: " + temp3 + ", ");

            r1.data = ByteBuffer.allocate(4).putInt(temp3).array();
            buffer = ByteBuffer.allocate(4);
            buffer.putInt((int) temp % temp2);

            temp3 = buffer.getInt();

            sb.append(temp3);

            r2.data = ByteBuffer.allocate(4).putInt(temp3).array();
            if (temp == 0) {
                sf.setZf(1);
            }
        }

        OsLogger.writeToLog("DVRR; " + sb, defaultLogLevel);
    }

    public void dv(int x, int y){
        StringBuilder sb = new StringBuilder();
        int temp, temp2, temp3;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        try {
            buffer = ByteBuffer.wrap(getData(x, y));
        } catch (Exception e) {
            e.printStackTrace();
        }

        temp2 = buffer.getInt();

        sb.append(temp2);

        if (temp2 == 0) {
            //TODO:set interupt to division by zero
        } else {
            temp3 = temp / temp2;

            sb.append("; result: " + temp3 + ", ");

            r1.data = ByteBuffer.allocate(4).putInt(temp3).array();

            temp3 = (int) temp % temp2;

            sb.append(temp3);

            r2.data = ByteBuffer.allocate(4).putInt(temp3).array();
            if (temp == 0) {
                sf.setZf(1);
            }
        }

        OsLogger.writeToLog("DV" + x + " " + y + "; " + sb, defaultLogLevel);

    }

    public void and() {
        StringBuilder sb = new StringBuilder();
        int temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: ");
        sb.append(temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        temp = temp & temp2;

        sb.append("; result: " + temp);

        r1.data = ByteBuffer.allocate(4).putInt(temp).array();
        if (temp == 0) {
            sf.setZf(1);
        }

        OsLogger.writeToLog("AND: " + sb, defaultLogLevel);
    }

    public void or() {
        StringBuilder sb = new StringBuilder();
        int temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: " + temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        temp = temp | temp2;

        sb.append("; result: " + temp);
        r1.data = ByteBuffer.allocate(4).putInt(temp).array();
        if (temp == 0) {
            sf.setZf(1);
        }

        OsLogger.writeToLog("OS: " + sb, defaultLogLevel);
    }

    public void xor() {
        StringBuilder sb = new StringBuilder();
        int temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: " + temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        temp = temp ^ temp2;

        sb.append("; result: " + temp);
        r1.data = ByteBuffer.allocate(4).putInt(temp).array();
        if (temp == 0) {
            sf.setZf(1);
        }

        OsLogger.writeToLog("XOR: " + sb, defaultLogLevel);
    }

    public void not() {
        StringBuilder sb = new StringBuilder();
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        sb.append("value: " + temp);

        temp = ~temp;

        sb.append("; result: " + temp);
        r1.data = ByteBuffer.allocate(4).putInt(temp).array();
        if (temp == 0) {
            sf.setZf(1);
        }

        OsLogger.writeToLog("NOT; " + sb, defaultLogLevel);
    }

    public void cmp() {
        StringBuilder sb = new StringBuilder();
        int temp, temp2;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();

        sb.append("values: " + temp + ", ");

        buffer = ByteBuffer.wrap(r2.data);
        temp2 = buffer.getInt();

        sb.append(temp2);

        if (temp < temp2) {

            sb.append("; result: lower");

            sf.setZf(0);
            sf.setCf(1);
        } else if (temp > temp2) {

            sb.append("; result: higher");

            sf.setZf(0);
            sf.setCf(0);
        } else {

            sb.append("; result: equal");

            sf.setZf(1);
        }
        OsLogger.writeToLog("CMP; " + sb, defaultLogLevel);
    }

    public void lw(int x, int y) {
        /*StringBuilder sb = new StringBuilder();
        int temp = 0;
        ByteBuffer buffer = null;
        try {
            buffer = ByteBuffer.wrap(getData(x, y));
            temp = buffer.getInt();
            sb.append("value: " + temp + " or " + new String(Utils.intToBytes(temp, 4)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        r1.data = Utils.intToBytes(temp, 4);*/

        Rm.setPI(InterruptType.GET_PUT_DATA);
        Rm.addResource(Type.WRITE_WORD, x + " " + y + " " + this.id, null);
        OsLogger.writeToLog("LW " + x + " " + y, defaultLogLevel);
    }

    public void sw(int x, int y) {
        /*StringBuilder sb = new StringBuilder();
        sb.append("register: " + r1.getDataInt() + " or " + new String(r1.data));
        try {
            saveData(x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        Rm.setPI(InterruptType.GET_PUT_DATA);
        Rm.addResource(Type.READ_WORD, x + " " + y + " " + this.id, null);
        OsLogger.writeToLog("SW " + x + " " + y, defaultLogLevel);
    }

    public void mov1() {
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        r2.data = buffer.array();

        OsLogger.writeToLog("MOV1; " + "r1: " + r1.getDataInt() + "; r2: " + r2.getDataInt(), defaultLogLevel);
    }

    public void mov2() {
        ByteBuffer buffer = ByteBuffer.wrap(r2.data);
        r1.data = buffer.array();

        OsLogger.writeToLog("MOV1; " + "r1: " + r1.getDataInt() + "; r2: " + r2.getDataInt(), defaultLogLevel);
    }

    public void prnt() {
        /*StringBuilder sb = new StringBuilder();
        ByteBuffer buffer = null;
        byte[] bytes = null;
        try {
            buffer = ByteBuffer.wrap(r2.data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int length = buffer.getInt();

        for(int i = 0; i < length; i++){
            try {
                bytes = getData((r1.getDataInt() + i)/16, (r1.getDataInt() + i)%16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sb.append(bytes);
            System.out.println(new String(bytes));
        }*/

        Rm.setPI(InterruptType.READ_WRITE);
        Rm.addResource(Type.NEED_OUTPUT, "nonNumber " + this.id, null);
        OsLogger.writeToLog("PRNT", defaultLogLevel);
    }

    public void prns() {
        //System.out.println(r1.getDataInt());
        Rm.setPI(InterruptType.READ_WRITE);
        Rm.addResource(Type.NEED_OUTPUT, "number " + this.id, null);
        OsLogger.writeToLog("PRNS: " + r1.getDataInt(), defaultLogLevel);
    }

    public void read(){
        /*Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
       // String[] words = getWords(input);
        String[] words = input.split("(?<=\\G.{4})");
        byte[] val = r1.data;
        int pos = r1.getDataInt();
        ByteBuffer byteBuffer  = null;
        for(int i = 0; i < words.length; i++) {
            byteBuffer = ByteBuffer.allocate(4).wrap(words[i].getBytes());
            int posX = pos / 16;
            int posY = pos % 16;
            r1.data = byteBuffer.array();
            try {
                saveData(posX, posY);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pos++;
        }
        r1.data = val;*/
        Rm.setPI(InterruptType.READ_WRITE);
        Rm.addResource(Type.NEED_INPUT, "" + this.id, null);
        OsLogger.writeToLog("PRNS: " + r1.getDataInt(), defaultLogLevel);

    }
   /* private String[] getWords(String line){
        ArrayList<String> words = new ArrayList<>();
        for(int i = 0; i < line.length(); i++){
            words.add("");
            for(; i % 4 < 4; i++){
                if(line.length() <= i){
                    break;
                }
                words.set(i / 4, words.get(i / 4).concat("" + line.charAt(i)));
            }
        }
        String[] words2 = new String[words.size()];
        for(int i = 0; i < words.size(); i++){
            words2[i] = words.get(i);
        }
        return words2;
    }*/
    public void jm(int x, int y) {
        ic = 16 * x + y;

        OsLogger.writeToLog("JM" + x + " " + y + "; ic: " + ic, defaultLogLevel);
    }

    public void je(int x, int y) {
        int oldIc = ic;
        if(sf.getZf()){
            ic = 16 * x + y;
        }
        OsLogger.writeToLog("JE" + x + " " + y + "; old ic: " + oldIc + ", new ic: " + ic, defaultLogLevel);
    }

    public void ja(int x, int y) {
        int oldIc = ic;
        if(!sf.getCf() && !sf.getZf()){
            ic = 16 * x + y;
        }
        OsLogger.writeToLog("JA" + x + " " + y + "; old ic: " + oldIc + ", new ic: " + ic, defaultLogLevel);
    }

    public void jl(int x, int y) {
        int oldIc = ic;
        if(sf.getCf()){
            ic = 16 * x + y;
        }
        OsLogger.writeToLog("JL" + x + " " + y + "; old ic: " + oldIc + ", new ic: " + ic, defaultLogLevel);
    }
    public void fo(int x, int y) {
        /*int pos = Rm.getFilePos(x, y, true);
        if(pos >= 0 && pos < 255)
            r1.data = Utils.intToBytes(pos, 4);
        else
            Rm.setPI(InterruptType.INCORRECT_FILE_NAME);
        */
        Rm.setPI(InterruptType.READ_WRITE);
        Rm.addResource(Type.OPEN_FILE, x + " " + y  + " " + this.id, null);
        OsLogger.writeToLog("FO " + (char)x + (char)y, defaultLogLevel);
    }

    public void fc() {
        /*Rm.closeFile(r1.data);
        if(r1.getDataInt() < 0){
            Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
        }*/
        Rm.setPI(InterruptType.READ_WRITE);
        Rm.addResource(Type.CLOSE_FILE, r1.getDataInt() + " " + this.id, null);
        OsLogger.writeToLog("FC; handler: " + r1.getDataInt(), defaultLogLevel);
    }

    public void fd() {
        /*OsLogger.writeToLog("FD; handler: " + r1.getDataInt());
        if(r1.getDataInt() < 0){
            Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
        } else
            Rm.deleteFile(r1.data);*/
        Rm.setPI(InterruptType.READ_WRITE);
        Rm.addResource(Type.DELETE_FILE, r1.getDataInt() + " " + this.id, null);
        OsLogger.writeToLog("FD; handler: " + r1.getDataInt(), defaultLogLevel);
    }

    public void fr(int x, int y) {
        //r2.data = Utils.intToBytes(1, 4);
        if(r1.getDataInt() < 0){
            Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
        } else {
            Rm.setPI(InterruptType.READ_WRITE);
            Rm.addResource(Type.READ_FILE, r1.getDataInt() + " " + this.id, null);
            /*r2.data = Rm.fileRead(r1.data);
            byte[] temp = {0, 0, r1.data[0], r1.data[1]};
            int temp2 = Utils.bytesToInt(temp);
            temp2++;
            temp = Utils.intToBytes(temp2, 4);
            r1.data[0] = temp[2];
            r1.data[1] = temp[3];*/
        }

        OsLogger.writeToLog("FR " + x + ", " + y, defaultLogLevel);
    }

    public void fw(int x, int y) {
        if(r1.getDataInt() < 0){
            Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
        } /*else {
            try {
                //byte[] data = getData(x, y);
                byte[] data = r2.data;
                if(Utils.bytesToInt(new byte[]{0, 0, r1.data[0], r1.data[3]}) < 255) {
                    Rm.fileWrite(r1.data, data);
                    byte[] temp = {0, 0, r1.data[0], r1.data[1]};
                    int temp2 = Utils.bytesToInt(temp);
                    temp2++;
                    temp = Utils.intToBytes(temp2, 4);
                    r1.data[0] = temp[2];
                    r1.data[1] = temp[3];
                }
                else {
                    Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
                    System.out.println("File is not big enough");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        else {
            Rm.setPI(InterruptType.READ_WRITE);
            Rm.addResource(Type.WRITE_FILE, r1.getDataInt() + " " + this.id, null);
        }
        OsLogger.writeToLog("FW " + x + ", " + y, defaultLogLevel);
    }

    public void halt() {
        //System.out.println("HALT");
        OsLogger.writeToLog("HALT", defaultLogLevel);
        OsLogger.writeToLog("HALT", defaultLogLevel);
        ic = 0;
    }

    public byte[] getData(int x, int y) throws Exception {
        byte[][] rmPtrTable = Rm.memory.memory[Rm.ptr.getDataInt()];
        byte[][] vmPtrTable = Rm.memory.memory[Utils.bytesToInt(rmPtrTable[ptr.getDataInt()])];
        byte[][] temp;
        int pos = (x * 16 + y) * 2;
        if(pos >= cs.getDataInt()){
            throw new Exception("Going too far");
        }
        x = pos / 16;
        y = pos % 16;
        temp = Rm.memory.memory[Utils.bytesToInt(vmPtrTable[x])];
        return temp[y];
    }

    public void saveData(int x, int y) throws Exception {
        byte[][] rmPtrTable = Rm.memory.memory[Rm.ptr.getDataInt()];
        byte[][] vmPtrTable = Rm.memory.memory[Utils.bytesToInt(rmPtrTable[ptr.getDataInt()])];
        int pos = (x * 16 + y) * 2;
        if(pos >= cs.getDataInt()){
            throw new Exception("Going too far");
        }
        Rm.memory.memory[Utils.bytesToInt(vmPtrTable[pos / 16])][pos % 16] = r1.data;

    }
    public void saveData(int x, int y, byte[] bytes) throws Exception {
        byte[][] rmPtrTable = Rm.memory.memory[Rm.ptr.getDataInt()];
        byte[][] vmPtrTable = Rm.memory.memory[Utils.bytesToInt(rmPtrTable[ptr.getDataInt()])];
        int pos = (x * 16 + y) * 2;
        if(pos >= cs.getDataInt()){
            throw new Exception("Going too far");
        }
        Rm.memory.memory[Utils.bytesToInt(vmPtrTable[pos / 16])][pos % 16] = bytes;

    }

}
