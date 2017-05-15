package processes;

import Rm.Rm;
import resources.Resource;
import resources.Type;

/**
 * Created by blitZ on 4/7/2017.
 */
public class JobGovernor extends MIKOSProcess {
    private int inProcess = 0;// Need this so process could continue where it stopped
    public JobGovernor(){
        ID = "JobGovernor";
        PID = 2;
        PPID = 1;
        priority = 99;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        if(inProcess == 0) {
            Resource r = new Resource();
            r.content = RES.get(0).content;// Creating resource for loader
            RES.remove(RES.get(0));// Removing that resource from list, might need it later. SUBJECT TO CHANGE
            r.type = Type.PAKROVIMO_PAKETAS;
            r.state = resources.State.ALIVE;
            Rm.resourceList.add(r);
            inProcess++;
            return;
        }
        if(inProcess == 1){

        }






        STATE = State.BLOCKED;
    }
}
