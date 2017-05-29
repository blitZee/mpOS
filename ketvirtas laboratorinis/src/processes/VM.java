package processes;

import Rm.Rm;
import Rm.InterruptType;
import resources.Resource;
import resources.Type;
import testTools.Constants;
import utils.OsLogger;
import utils.Utils;
import vm.Vm;

import java.util.Scanner;

/**
 * Created by blitZ on 4/7/2017.
 */
public class VM extends MIKOSProcess {
    public VM(){
        ID = "VM";
        PID = 12;
        PPID = 1;
        priority = 50;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        OsLogger.writeToLog("VM started, vm's id: " + resource.content, OsLogger.LEVEL_3);
        STATE = State.RUNNING;
        Vm vmDescriptor = Rm.getVm(Integer.parseInt(resource.content));
        RES.remove(resource);
        if(vmDescriptor == null){
            System.out.println("NO vm found");
            STATE = State.BLOCKED;
            return;
        }
        if (vmDescriptor == null) {
            //System.out.println("No such program");
            //OsLogger.writeToLog("No program named " + programName + " to start");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        Vm vm = vmDescriptor;
        //int[] codeBeginning = getCodeBeginning(vm);
        int[] codeBeginning = Rm.getNextIndexes(vm);
        int[] indexes;
        //System.out.println("row: " + codeBeginning[0]);
        //System.out.println("column: " + codeBeginning[1]);
        int row = codeBeginning[0];
        int column = codeBeginning[1];
        byte[] command;
        boolean cont;// continue
        while (true) {
            command = Rm.getCommand(vm, row, column);
            if(Rm.stepMode){
                Rm.showRegister(vm);
                System.out.println("Next command: " + new String(command));
                while(true) {
                    String line = scanner.nextLine();
                    if (line.toUpperCase().equals("SHOW CS")) {
                        Rm.showCseg(vm.id);
                    } else if (line.toUpperCase().equals("SHOW DS")) {
                        Rm.showDseg(vm.id);
                    } else if (line.toUpperCase().equals("FINISH")) {
                        Rm.stepMode = false;
                        break;
                    }
                    if (line.equals("")) {
                        break;
                    }
                }
            }
            cont = Rm.executeCommand(vm, command);
            vm.ic++;
            vm.iterations++;
            Rm.timer--;
            InterruptType interrupt =  Rm.test(vm);
            if(interrupt == InterruptType.TIMER_INTERRUPT){
                Rm.addResource(Type.PROGRAM_START, vm.id + "", null);
                break;
            }
            if(interrupt == InterruptType.READ_WRITE){
                break;
            }
            if(interrupt == InterruptType.GET_PUT_DATA){
                break;
            }
            if (!cont || vm.iterations > Constants.MAX_ITERATIONS) {
                Rm.timer = 10;
                vm.ic = 0;
                vm.iterations = 0;
                Rm.addResource(Type.PROGRAM_HALT, "" + vm.id, null);
                //Rm.processes.remove(this);
                Rm.processesToRemove.add(this);
                break;
            }
            indexes = Rm.getNextIndexes(vm);
            row = indexes[0];
            column = indexes[1];

        }
        STATE = State.BLOCKED;
        OsLogger.writeToLog("VM ended\n", OsLogger.LEVEL_3);
    }

    private int[] getCodeBeginning(Vm vm){
        int temp = Utils.bytesToInt(vm.cs.data);
        int[] ret = new int[2];
        ret[0] = temp / 16;// row
        ret[1] = temp % 16;// column
        return ret;
    }
}
