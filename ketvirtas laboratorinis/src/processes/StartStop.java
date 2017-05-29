package processes;

import Rm.Rm;
import resources.Resource;

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
        Rm.processes.add(new ProcessManager());
        Rm.processes.add(new ResourceManager());
        Rm.processes.add(new Loader());
        Rm.processes.add(new JCL());
        Rm.processes.add(new ProcessKiller());
        Rm.processes.add(new Chan_1_Device());
        Rm.processes.add(new Chan_3_Device());
        Rm.processes.add(new Chan_2_Device());
        Rm.processes.add(new GetPutData());
        STATE = State.BLOCKED;
    }
}
