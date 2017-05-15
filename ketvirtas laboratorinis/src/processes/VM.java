package processes;

import resources.Resource;

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
        STATE = State.RUNNING;







        STATE = State.BLOCKED;
    }
}
