import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

import Rm.Rm;
import processes.Interrupt;
import processes.StartStop;
import resources.Resource;
import resources.Type;
import utils.OsLogger;
import utils.Reader;

/**
 * Created by blitZ on 3/8/2017.
 */
public class Main {

    public static void main(String[] args) {
        new Thread(new Rm()).start();
        new Thread(new Reader()).start();
    }
}
