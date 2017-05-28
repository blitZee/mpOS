package processes;

import Rm.Rm;
import resources.Resource;
import resources.Type;

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
        System.out.println("JCL started");
        //Rm.addResource(Type.PAKROVIMO_PAKETAS, resource.content, resources.State.VALIDATED);
        resource.state = resources.State.VALIDATED;

        System.out.println("JCL ended\n");
        STATE = State.BLOCKED;
    }
}
