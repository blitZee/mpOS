package vm;

import testTools.Test;

import java.nio.ByteBuffer;

/**
 * Created by irmis on 2017.03.15.
 */
public class Vm {
    public VmRegister ptr;
    VmRegister sp;
    VmRegister r1;
    VmRegister r2;
    VmRegister cs;
    VmRegister ds;
    VmRegister ss;
    Rm.Rm rm = null;

    public Vm(Rm.Rm rm){
        this.rm = rm;
        ptr = new VmRegister(4, "puslapiavimo lentele");
        for(int i = 0; i < 16; i++){
            if(rm.memory.getInt(Test.bytesToInt(rm.ptr.data), i) == 0){
                ptr.data = Test.intToBytes(i, 4);
            }
        }

    }
}
