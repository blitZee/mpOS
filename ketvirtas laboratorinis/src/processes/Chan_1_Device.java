package processes;

import resources.Resource;

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







        STATE = State.BLOCKED;
    }
}
