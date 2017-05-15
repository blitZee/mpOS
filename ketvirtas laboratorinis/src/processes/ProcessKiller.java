package processes;

import resources.Resource;

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







        STATE = State.BLOCKED;
    }
}
