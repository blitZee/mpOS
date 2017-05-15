package processes;

import resources.Resource;

/**
 * Created by blitZ on 4/7/2017.
 */
public class GetPutData extends MIKOSProcess {
    public GetPutData(){
        ID = "GetPutData";
        PID = 6;
        PPID = 1;
        priority = 85;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;







        STATE = State.BLOCKED;
    }
}
