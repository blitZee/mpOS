package processes;

import Rm.Rm;
import Rm.InterruptType;
import resources.Resource;
import resources.Type;
import testTools.Constants;
import utils.OsLogger;
import utils.Utils;
import vm.Vm;

/**
 * Created by blitZ on 4/7/2017.
 */
public class Chan_1_Device extends MIKOSProcess {
    public Chan_1_Device(){
        ID = "Chan_1_Device";
        PID = 4;
        PPID = 1;
        priority = 90;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("Channel one device started", Constants.PROCESS_LOG_LEVEL);
        resource = RES.get(0);
        if(resource.type == Type.OPEN_FILE){
            String[] arguments = resource.content.split(" ");
            int pos = Rm.getFilePos(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]), true);
            Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[2]));
            vm.r1.data = Utils.intToBytes(pos, 4);
            Rm.addResource(Type.PROGRAM_START, arguments[2], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        } else if (resource.type == Type.CLOSE_FILE){
            String[] arguments = resource.content.split(" ");
            Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[1]));
            Rm.closeFile(vm.r1.data);
            Rm.addResource(Type.PROGRAM_START, arguments[1], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
            if(vm.r1.getDataInt() < 0){
                Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
            }
        } else if(resource.type == Type.DELETE_FILE){
            String[] arguments = resource.content.split(" ");
            Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[1]));
            Rm.deleteFile(vm.r1.data);
            Rm.addResource(Type.PROGRAM_START, arguments[1], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        } else if(resource.type == Type.READ_FILE){
            String[] arguments = resource.content.split(" ");
            Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[1]));

            vm.r2.data = Rm.fileRead(vm.r1.data);
            byte[] temp = {0, 0, vm.r1.data[0], vm.r1.data[1]};
            int temp2 = Utils.bytesToInt(temp);
            temp2++;
            temp = Utils.intToBytes(temp2, 4);
            vm.r1.data[0] = temp[2];
            vm.r1.data[1] = temp[3];

            Rm.addResource(Type.PROGRAM_START, arguments[1], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        } else if(resource.type == Type.WRITE_FILE){
            String[] arguments = resource.content.split(" ");
            Vm vm = Rm.getVmDescriptor(Integer.parseInt(arguments[1]));

            try {
                //byte[] data = getData(x, y);
                byte[] data = vm.r2.data;
                if(Utils.bytesToInt(new byte[]{0, 0, vm.r1.data[0], vm.r1.data[3]}) < 255) {
                    Rm.fileWrite(vm.r1.data, data);
                    byte[] temp = {0, 0, vm.r1.data[0], vm.r1.data[1]};
                    int temp2 = Utils.bytesToInt(temp);
                    temp2++;
                    temp = Utils.intToBytes(temp2, 4);
                    vm.r1.data[0] = temp[2];
                    vm.r1.data[1] = temp[3];
                }
                else {
                    Rm.setPI(InterruptType.INCORRECT_FILE_HANLDE);
                    System.out.println("File is not big enough");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Rm.addResource(Type.PROGRAM_START, arguments[1], null);
            Rm.setPI(InterruptType.NO_INTERRUPT);
            RES.remove(resource);
        }






        STATE = State.BLOCKED;
        OsLogger.writeToLog("Channel one device ended", Constants.PROCESS_LOG_LEVEL);
    }
}
