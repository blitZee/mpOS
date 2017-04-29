package Rm;

import utils.Utils;

import java.nio.ByteBuffer;

/**
 * Created by blitZ on 3/8/2017.
 */
public class RmRegister {
    public int size;
    public String name;
    public byte[] data;

    public RmRegister(int size, String name) {
        this.size = size;
        this.name = name;
        data = new byte[size];
    }

    public int getDataInt(){
        return ByteBuffer.wrap(data).getInt();
    }

    public void inc(){
        int temp = Utils.bytesToInt(data);
        temp++;
        data = Utils.intToBytes(temp, size);
    }
}
