package processes;

import Rm.Rm;
import resources.Resource;
import resources.Type;
import testTools.Constants;
import utils.OsLogger;

/**
 * Created by blitZ on 4/7/2017.
 */
public class JCL extends MIKOSProcess {
    public JCL(){
        ID = "JCL";
        PID = 11;
        PPID = 1;
        priority = 97;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        OsLogger.writeToLog("JCL started", Constants.PROCESS_LOG_LEVEL);
        //Rm.addResource(Type.PAKROVIMO_PAKETAS, resource.content, resources.State.VALIDATED);
        resource.state = resources.State.VALIDATED;

        OsLogger.writeToLog("JCL ended", Constants.PROCESS_LOG_LEVEL);
        STATE = State.BLOCKED;
    }
}
