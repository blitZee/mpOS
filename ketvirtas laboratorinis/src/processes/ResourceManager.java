package processes;

import Rm.Rm;
import resources.Resource;
import resources.Type;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by blitZ on 4/7/2017.
 */
public class ResourceManager extends MIKOSProcess {
    public ResourceManager(){
        ID = "ResourceManager";
        PID = 10;
        PPID = 1;
        priority = 93;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess(Resource resource) {
        STATE = State.RUNNING;
        MIKOSProcess process = null;
        ArrayList<MIKOSProcess> processes = new ArrayList<>();
        Iterator<Resource> iter = Rm.resourceList.iterator();
        while(iter.hasNext()){
            Resource r = iter.next();
            if (r.type == Type.PAKROVIMO_PAKETAS){
                if(r.state == resources.State.ALIVE) {// if it's alive, that means that jobGovernor used it and waiting till loader finishes it's job
                    process = findProcess("Loader");
                    process.RES.add(r);
                    process.STATE = State.READY;
                    //iter.remove(); //Do not remove yet because job governor will still need this
                } else if(r.state == resources.State.WAITING_FOR_USAGE){// else it means that job governor needs to continue
                    process = findProcess("JobGovernor");
                    process.RES.add(r);
                    process.STATE = State.READY;
                    iter.remove();// now we remove it from global scope
                }
            } else if(r.type == Type.VARTOTOJO_ATMINTIS){
                process = new JobGovernor();
                Rm.processes.add(process);
                process.RES.add(r);
                process.STATE = State.READY;
                iter.remove();
            }
        }
        STATE = State.BLOCKED;
    }

    private MIKOSProcess findProcess(String id){
        for(MIKOSProcess p : Rm.processes){
            if(p.ID.equals(id))
                return p;
        }
        return null;
    }
}
