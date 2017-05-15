package processes;

import Rm.Rm;
import resources.Resource;

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
        for(MIKOSProcess process : Rm.processes){
            if(process.STATE == State.READY){
                process.doProcess(process.RES.get(0));
            }
        }
    }
}
