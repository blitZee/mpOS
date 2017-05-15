package processes;

import resources.Resource;

/**
 * Created by blitZ on 4/7/2017.
 */
public class Interrupt extends MIKOSProcess {
        public Interrupt(){
            ID = "Interrupt";
            PID = 5;
            PPID = 1;
            priority = 98;

            STATE = State.BLOCKED;
        }

        @Override
        public void doProcess(Resource resource) {
            STATE = State.RUNNING;







            STATE = State.BLOCKED;
        }
}
