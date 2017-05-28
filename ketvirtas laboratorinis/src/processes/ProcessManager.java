package processes;

import Rm.Rm;
import resources.Resource;
import utils.OsLogger;

import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Irmis on 2017-05-15.
 */
public class ProcessManager extends MIKOSProcess {

    public ProcessManager(){
        ID = "ProcessManager";
        PID = 10;
        PPID = 1;
        priority = 93;

        STATE = State.BLOCKED;//TODO: PROBABLY NEED TO CHANGE, it's same as resource manager
    }

    @Override
    public void doProcess(Resource resource) {
        //OsLogger.writeToLog("Process manager started");
        for(MIKOSProcess process : Rm.processes){
            if(process.STATE == State.READY){
                process.doProcess(process.RES.get(0));
                if(process.ID.startsWith("VM")){
                    process.priority--;
                    if(process.priority == 0){
                        process.priority = 50;
                    }
                }
                break;
            }
        }
        for(MIKOSProcess process : Rm.processesToAdd){
            Rm.processes.add(process);
        }
        for(MIKOSProcess process : Rm.processesToRemove){
            Rm.processes.remove(process);
        }
        Rm.processesToRemove.clear();
        Collections.sort(Rm.processes);
        //OsLogger.writeToLog("Process manager ended\n");
    }
}
