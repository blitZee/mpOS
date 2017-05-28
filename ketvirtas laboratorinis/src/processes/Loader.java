package processes;

import Rm.*;
import Rm.InterruptType;
import resources.Resource;
import resources.Type;
import testTools.Constants;
import utils.OsLogger;
import utils.Utils;
import vm.Vm;

import java.io.IOException;

/**
 * Created by blitZ on 4/7/2017.
 */
public class Loader extends MIKOSProcess {
    public Loader(){
        ID = "Loader";
        PID = 3;
        PPID = 1;
        priority = 96;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        OsLogger.writeToLog("Loader started");
        STATE = State.RUNNING;
        RES.remove(resource);
        String programName = resource.content;
        /*if (Rm.getVmDescriptor(programName) != null) {
            Rm.setSI(InterruptType.DUPLICATE_NAME);
            OsLogger.writeToLog("There already is program loaded with this name " + programName);
            return;
        }*/
        int ret = 0;
        int vmPtr = Rm.getVmPtr();// assign space in rm ptr. Value can be from 0 to 15
        MIKOSProcess process = findProcess("JCL");
        process.doProcess(resource);
        if(resource.state == null || resource.state != resources.State.VALIDATED) {
            Rm.addResource(Type.VALIDATION, programName, null);
            return;
        }
        Vm vm = new Vm(programName);// create vm only after we know that there are enough space in rm ptr
        byte[][] rmTable = Rm.memory.memory[Utils.bytesToInt(Rm.ptr.data)];
        vm.ptr.data = Utils.intToBytes(vmPtr, 4);

        rmTable[vmPtr] = Utils.intToBytes(Rm.memory.getFreeBlock(), 4);
        try {
            long pos = Rm.findFilePos(programName);
            if(pos < 0) {
                Rm.setSI(InterruptType.INCORRECT_FILE_NAME);
                OsLogger.writeToLog("No program named " + programName);
                return;
            }
            while (true) {
                String line = Rm.hdd.file.readLine();
                ret = Rm.memory.addToVm(line, vm, programName, ret);
                if (ret == Constants.HALT)
                    break;
                else if(ret == Constants.NO_MEMORY){
                    Rm.setSI(InterruptType.OUT_OF_MEMORY);
                    //System.out.println("No memory");
                    OsLogger.writeToLog("No memory for vm, named " + programName);
                    Rm.removeVm(vm);
                }
            }
        } catch (IOException e) {
            Rm.removeVm(vm);
            System.out.println("IO Exception");
            resource.state = resources.State.FAILED_LOADING;
        } catch (Exception e) {
            Rm.setPI(InterruptType.UNDEFINED_OPERATION_WHILE_LOADING);
            Rm.removeVm(vm);// TODO: This needs to be processed in interrupt
            OsLogger.writeToLog("Removed vm named " + programName + ", because of incorrect code");
            resource.state = resources.State.FAILED_LOADING;
            return;
        }


        //OsLogger.writeToLog("Load for program " + programName + " ended, it's id is " + vm.id);

        Rm.VmList.add(vm);
        vm.ic = 0;
        //System.out.println("Vm's id is " + vm.id);
        resource.content = "" + vm.id;
        resource.state = resources.State.WAITING_FOR_USAGE;
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Loader ended. loaded vm's id is " + vm.id + "\n");
    }

    private MIKOSProcess findProcess(String id){
        for(MIKOSProcess p : Rm.processes){
            if(p.ID.equals(id))
                return p;
        }
        return null;
    }
}
