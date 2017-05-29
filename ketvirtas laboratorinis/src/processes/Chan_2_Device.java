package processes;

import Rm.Rm;
import Rm.InterruptType;
import resources.Resource;
import resources.Type;
import testTools.Constants;
import utils.OsLogger;
import vm.Vm;

import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * Created by blitZ on 4/7/2017.
 */
public class Chan_2_Device extends MIKOSProcess {
    public Chan_2_Device(){
        ID = "Chan_2_Device";
        PID = 7;
        PPID = 1;
        priority = 70;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("Channel 2 device started", Constants.PROCESS_LOG_LEVEL);

        Vm vm = Rm.getVmDescriptor(Integer.parseInt(RES.get(0).content));
        System.out.println("Press enter and then write input to vm " + vm.id);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        // String[] words = getWords(input);
        String[] words = input.split("(?<=\\G.{4})");
        byte[] val = vm.r1.data;
        int pos = vm.r1.getDataInt();
        ByteBuffer byteBuffer  = null;
        for(int i = 0; i < words.length; i++) {
            byteBuffer = ByteBuffer.allocate(4).wrap(words[i].getBytes());
            int posX = pos / 16;
            int posY = pos % 16;
            vm.r1.data = byteBuffer.array();
            try {
                vm.saveData(posX, posY);
            } catch (Exception e) {
                OsLogger.writeToLog("Reading error: not enough space in vm", Constants.PROCESS_LOG_LEVEL);
                break;
            }
            pos++;
        }
        vm.r1.data = val;
        Rm.addResource(Type.PROGRAM_START, RES.get(0).content, null);
        Rm.setPI(InterruptType.NO_INTERRUPT);
        RES.remove(resource);
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Channel 2 device ended", Constants.PROCESS_LOG_LEVEL);
    }
}
