package processes;

import Rm.Rm;
import resources.Resource;
import testTools.Constants;
import utils.OsLogger;

/**
 * Created by blitZ on 4/7/2017.
 */
public class StartStop extends MIKOSProcess {

    public StartStop(){
        ID = "StartStop";
        PID = 1;
        PPID = -42; //No parents :(
        priority = 100;

        STATE = State.READY;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("Start stop started", Constants.PROCESS_LOG_LEVEL);
        Rm.processes.add(new ProcessManager());
        Rm.processes.add(new ResourceManager());
        Rm.processes.add(new Loader());
        Rm.processes.add(new JCL());
        Rm.processes.add(new ProcessKiller());
        Rm.processes.add(new Chan_1_Device());
        Rm.processes.add(new Chan_3_Device());
        Rm.processes.add(new Chan_2_Device());
        Rm.processes.add(new GetPutData());
        Rm.processes.add(new Interrupt());
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Start stop ended", Constants.PROCESS_LOG_LEVEL);
    }
}
