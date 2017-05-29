package processes;

import Rm.Rm;
import resources.Resource;
import resources.Type;
import utils.OsLogger;
import utils.Utils;
import vm.Vm;

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
        OsLogger.writeToLog("Resource manager started", OsLogger.LEVEL_1);
        STATE = State.RUNNING;
        MIKOSProcess process = null;
        ArrayList<MIKOSProcess> processes = new ArrayList<>();
        Iterator<Resource> iter = Rm.getResourceListIterator();
        while(iter.hasNext()){
            Resource r = iter.next();
            if (r.type == Type.PAKROVIMO_PAKETAS){
                if(r.state == resources.State.ALIVE) {// if it's alive, that means that jobGovernor used it and waiting till loader finishes it's job
                    process = Utils.findProcess("Loader");
                    process.RES.add(r);
                    process.STATE = State.READY;
                    //iter.remove(); //Do not remove yet because job governor will still need this
                } else if(r.state == resources.State.WAITING_FOR_USAGE){// else it means that job governor needs to continue
                    process = Utils.findProcess("JobGovernor");
                    process.RES.add(r);
                    process.STATE = State.READY;
                    iter.remove();// now we remove it from global scope
                } else if(r.state == resources.State.FAILED_LOADING){
                    iter.remove();
                }
            } else if(r.type == Type.VARTOTOJO_ATMINTIS){
                process = new JobGovernor();
                Rm.processes.add(process);
                process.RES.add(r);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.PROGRAM_START){
                process = Utils.findProcess("VM" + r.content);
                process.RES.add(r);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.PROGRAM_HALT){
                iter.remove();
                //System.out.println("HALT in resource manager");
            } else if(r.type == Type.VALIDATION){
                process = Utils.findProcess("JCL");
                process.RES.add(r);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.PROGRAM_KILL){
                process = Utils.findProcess("ProcessKiller");
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.OPEN_FILE){
                process = Utils.findProcess("Chan_1_Device");
                Resource temp = new Resource();
                temp.type = Type.OPEN_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.CLOSE_FILE){
                process = Utils.findProcess("Chan_1_Device");
                Resource temp = new Resource();
                temp.type = Type.CLOSE_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.DELETE_FILE){
                process = Utils.findProcess("Chan_1_Device");
                Resource temp = new Resource();
                temp.type = Type.DELETE_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            }
            else if(r.type == Type.READ_FILE){
                process = Utils.findProcess("Chan_1_Device");
                Resource temp = new Resource();
                temp.type = Type.READ_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.WRITE_FILE){
                process = Utils.findProcess("Chan_1_Device");
                Resource temp = new Resource();
                temp.type = Type.WRITE_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            }
            else if(r.type == Type.NEED_OUTPUT){
                process = Utils.findProcess("Chan_3_Device");
                Resource temp = new Resource();
                temp.type = Type.WRITE_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.NEED_INPUT){
                process = Utils.findProcess("Chan_2_Device");
                Resource temp = new Resource();
                temp.type = Type.WRITE_FILE;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.READ_WORD){
                process = Utils.findProcess("GetPutData");
                Resource temp = new Resource();
                temp.type = Type.READ_WORD;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            } else if(r.type == Type.WRITE_WORD){
                process = Utils.findProcess("GetPutData");
                Resource temp = new Resource();
                temp.type = Type.WRITE_WORD;
                temp.content = r.content;
                process.RES.add(temp);
                process.STATE = State.READY;
                iter.remove();
            }
        }
        STATE = State.BLOCKED;
        OsLogger.writeToLog("Resource manager ended\n", OsLogger.LEVEL_1);
    }

}
