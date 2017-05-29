package processes;

import Rm.Rm;
import Rm.InterruptType;
import resources.Resource;
import resources.Type;
import testTools.Constants;
import utils.OsLogger;
import vm.Vm;

import java.nio.ByteBuffer;

/**
 * Created by blitZ on 4/7/2017.
 */
public class Chan_3_Device extends MIKOSProcess {
    public Chan_3_Device(){
        ID = "Chan_3_Device";
        PID = 8;
        PPID = 1;
        priority = 65;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("Channel 3 device started", Constants.PROCESS_LOG_LEVEL);
        resource = RES.get(0);
        String[] arguments = resource.content.split(" ");
        Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[1]));
        if(arguments[0].equals("number")){
            System.out.println(vm.r1.getDataInt());
            Rm.addResource(Type.PROGRAM_START, arguments[2], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        } else {
            StringBuilder sb = new StringBuilder();
            ByteBuffer buffer = null;
            byte[] bytes = null;
            try {
                buffer = ByteBuffer.wrap(vm.r2.data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            int length = buffer.getInt();

            for(int i = 0; i < length; i++){
                try {
                    bytes = vm.getData((vm.r1.getDataInt() + i)/16, (vm.r1.getDataInt() + i)%16);
                } catch (Exception e) {
                    OsLogger.writeToLog("Somethings wrong with register values", OsLogger.LEVEL_3);
                    break;
                    //e.printStackTrace();
                }
                if(bytes != null) {
                    sb.append(bytes);
                    System.out.println(new String(bytes));
                }
            }
        }
        Rm.addResource(Type.PROGRAM_START, arguments[1], null);
        Rm.setPI(InterruptType.NO_INTERRUPT);
        RES.remove(resource);
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Channel 3 device ended", Constants.PROCESS_LOG_LEVEL);
    }
}
