package processes;

import Rm.Rm;
import resources.Resource;
import resources.Type;
import utils.OsLogger;

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
        OsLogger.writeToLog("Job governor started");
        STATE = State.RUNNING;
        Resource r = getResource();
        if(resource.type == Type.VARTOTOJO_ATMINTIS){
            Rm.addResource(Type.PAKROVIMO_PAKETAS, RES.get(0).content, resources.State.ALIVE);
            RES.remove(RES.get(0));
            OsLogger.writeToLog("Job governor ended\n");
            return;
        }

        if(r.type == Type.PAKROVIMO_PAKETAS && r.state == resources.State.WAITING_FOR_USAGE){
            VM vmProcess= new VM();
            vmProcess.ID = "VM" + r.content;
            vmProcess.RES.add(r);
            Rm.processesToAdd.add(vmProcess);
            Rm.addResource(Type.PROGRAM_START, r.content, null);
        }

        System.out.println("job governor process state: " + inProcess + "; vm id: " + r.content);
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Job governor ended\n");
    }

    private Resource getResource(){
        if(RES.size() == 0){
            return null;
        }
        if(inProcess > RES.size() - 1){
            inProcess = 0;
            return RES.get(inProcess++);
        } else{
            return RES.get(inProcess++);
        }
    }
}
