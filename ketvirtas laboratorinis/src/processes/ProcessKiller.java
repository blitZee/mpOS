package processes;

import Rm.Rm;
import resources.Resource;
import utils.OsLogger;

/**
 * Created by blitZ on 4/7/2017.
 */
public class ProcessKiller extends MIKOSProcess {
    public ProcessKiller(){
        ID = "ProcessKiller";
        PID = 9;
        PPID = 1;
        priority = 89;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("Process killer started");
        for(Resource r : RES){
            int temp = Integer.parseInt(r.content);
            Rm.removeVm(temp);
        }
        RES.clear();
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Process killer ended");
    }
}
