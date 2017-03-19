package vm;

import testTools.Test;

import java.nio.ByteBuffer;

/**
 * Created by irmis on 2017.03.15.
 */
public class Vm {
    public VmRegister ptr;
    VmRegister r1;
    VmRegister r2;
    public VmRegister ds;
    public VmRegister cs;
    public VmRegister sp;

    public VmStatusFlag sf;

    public int ic;

    Rm.Rm rm = null;

    public Vm(Rm.Rm rm) {
        this.rm = rm;
        ptr = new VmRegister(4, "puslapiavimo lentele");
        for (int i = 0; i < 16; i++) {
            if (rm.memory.getInt(Test.bytesToInt(rm.ptr.data), i) == 0) {
                ptr.data = Test.intToBytes(i, 4);
            }
        }
        sp = new VmRegister(1, "sp");
        r1 = new VmRegister(4, "register 1");
        r2 = new VmRegister(4, "register 2");
        ds = new VmRegister(4, "data segment");
        cs = new VmRegister(4, "code segment");
        sf = new VmStatusFlag();
        ds.data = Test.intToBytes(0, 4);
        ic = 0;
    }

    public void adrr() {
        long temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        temp = temp + buffer.getInt();
        if (temp > Integer.MAX_VALUE) {
            temp -= Integer.MAX_VALUE;
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void ad(int x, int y) {
        long temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
    }

    public void sbrr() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        temp = temp - buffer.getInt();
        if (temp < 0) {
            temp += Integer.MAX_VALUE;
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void sb(int x, int y) {

    }

    public void mlrr() {
        long temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        temp = temp * buffer.getInt();
        if (temp > Integer.MAX_VALUE) {
            temp = temp - (Integer.MAX_VALUE * (temp / Integer.MAX_VALUE));
            sf.setCf(1);
        }
        buffer = ByteBuffer.allocate(4);
        buffer.putInt((int) temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void ml(int x, int y) {

    }

    public void dvrr() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        if (buffer.getInt() == 0) {

        } else {
            buffer = ByteBuffer.allocate(4);
            buffer.putInt(temp / buffer.getInt());
            r1.data = buffer.array();
            buffer = ByteBuffer.allocate(4);
            buffer.putInt((int) temp % buffer.getInt());
            r2.data = buffer.array();
            if (temp == 0) {
                sf.setZf(1);
            }
        }
    }

    public void dv(int x, int y) {

    }

    public void and() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        temp = temp & buffer.getInt();
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void or() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        temp = temp | buffer.getInt();
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void xor() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        temp = temp ^ buffer.getInt();
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void not() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = ~buffer.getInt();
        buffer.putInt(temp);
        r1.data = buffer.array();
        if (temp == 0) {
            sf.setZf(1);
        }
    }

    public void cmp() {
        int temp;
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        temp = buffer.getInt();
        buffer = ByteBuffer.wrap(r2.data);
        if (temp < buffer.getInt()) {
            sf.setZf(0);
            sf.setCf(1);
        } else if (temp > buffer.getInt()) {
            sf.setZf(0);
            sf.setCf(0);
        } else {
            sf.setZf(1);
        }
    }

    public void lw(int x, int y) {

    }

    public void sw(int x, int y) {

    }

    public void mov1() {
        ByteBuffer buffer = ByteBuffer.wrap(r1.data);
        r2.data = buffer.array();
    }

    public void mov2() {
        ByteBuffer buffer = ByteBuffer.wrap(r2.data);
        r1.data = buffer.array();
    }

    public void prnt() {

    }

    public void prns() {

    }

    public void push() {

    }

    public void pop() {

    }

    public void jm(int x, int y) {

    }

    public void je(int x, int y) {

    }

    public void ja(int x, int y) {

    }

    public void jl(int x, int y) {

    }

    public void fo(int x, int y) {

    }

    public void fc() {

    }

    public void fd() {

    }

    public void fr(int x, int y) {

    }

    public void fw(int x, int y) {

    }

    public void halt() {

    }
}
