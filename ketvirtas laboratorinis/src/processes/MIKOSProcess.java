package processes;

import Rm.RmRegister;
import Rm.RmStatusFlag;
import resources.Resource;

import java.util.ArrayList;

/**
 * Created by blitZ on 4/7/2017.
 */
public abstract class MIKOSProcess {
    public String ID;
    int PID;
    int PPID;
    public ArrayList<Resource> RES = new ArrayList<>();
    int CRES;
    int priority;

    State STATE;

    public RmRegister PTR;
    public RmRegister R1;
    public RmRegister R2;
    public RmRegister DS;
    public RmRegister CS;
    public RmStatusFlag SF;

    public int IC;

    public abstract void doProcess(Resource resource);
}
