package processes;

/**
 * Created by blitZ on 4/7/2017.
 */
public class ResourceManager extends Process {
    public ResourceManager(){
        ID = "ResourceManager";
        PID = 10;
        PPID = 1;
        priority = 93;

        STATE = State.BLOCKED;
    }

    @Override
    public void doProcess() {
        STATE = State.RUNNING;







        STATE = State.BLOCKED;
    }
}
