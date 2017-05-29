package utils;

import Rm.Rm;
import processes.MIKOSProcess;

import java.nio.ByteBuffer;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Utils {
    Rm rm;

    public Utils(Rm rm) {
        this.rm = rm;
    }


    public static int hexToInt(char c){// c should be upper case letter
        if(c >= 48 && c <= 57){
            return c - 48;
        } else
            return c - 'A' + 10;
    }

    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    public static byte[] intToBytes(int integer, int size) {
        return ByteBuffer.allocate(size).putInt(integer).array();
    }

    public static String intToString(int i){
        String s = "00000" + i;
        return s.substring(s.length() - 4);
    }
    public static MIKOSProcess findProcess(String id){
        for(MIKOSProcess p : Rm.processes){
            if(p.ID.equals(id))
                return p;
        }
        return null;
    }
}
