package Rm;

import java.io.File;

/**
 * Created by blitZ on 3/8/2017.
 */
public class HDD {
    File file;
    public HDD() {
        file = new File("HDD.txt");
    }

    public String readFromMemory(){
        return new String("your mom");
    }

    public void writeToMemory(){

    }

}
