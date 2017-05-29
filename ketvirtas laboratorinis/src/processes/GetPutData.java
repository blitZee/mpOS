package processes;

import Rm.Rm;
import Rm.InterruptType;
import resources.Resource;
import resources.Type;
import utils.OsLogger;
import utils.Utils;
import vm.Vm;

import java.nio.ByteBuffer;

/**
 * Created by blitZ on 4/7/2017.
 */
public class GetPutData extends MIKOSProcess {
    public GetPutData(){
        ID = "GetPutData";
        PID = 6;
        PPID = 1;
        priority = 85;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("Get put data started");
        resource = RES.get(0);
        String[] arguments = resource.content.split(" ");
        Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[2]));
        int x = Integer.parseInt(arguments[0]);
        int y = Integer.parseInt(arguments[1]);
        if(resource.type == Type.READ_WORD){
            StringBuilder sb = new StringBuilder();
            sb.append("register: " + vm.r1.getDataInt() + " or " + new String(vm.r1.data));
            try {
                vm.saveData(x, y);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Rm.addResource(Type.PROGRAM_START, arguments[2], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        } else if(resource.type == Type.WRITE_WORD){
            StringBuilder sb = new StringBuilder();
            int temp = 0;
            ByteBuffer buffer = null;
            try {
                buffer = ByteBuffer.wrap(vm.getData(x, y));
                temp = buffer.getInt();
                sb.append("value: " + temp + " or " + new String(Utils.intToBytes(temp, 4)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            vm.r1.data = Utils.intToBytes(temp, 4);

            Rm.addResource(Type.PROGRAM_START, arguments[2], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        }






        STATE = State.BLOCKED;
        OsLogger.writeToLog("Get put data ended");
    }
}
