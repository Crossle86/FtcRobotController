package com.qualcomm.ftcrobotcontroller.local.lib.taskevent;

import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.Robot;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.RobotEvent;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.RobotTask;
import com.qualcomm.robotcore.util.ElapsedTime;

public class SingleShotTimerTask extends RobotTask
{
    public enum EventKind
    {
        EXPIRED,
    }

    public class SingleShotTimerEvent extends RobotEvent
    {
        EventKind kind;

        public SingleShotTimerEvent(SingleShotTimerTask task, EventKind kind)
        {
            super(task);
            this.kind = kind;
            Util.log(kind.toString());
        }

        @Override
        public String toString() {return (super.toString() + ": " + kind);}
    }

    private ElapsedTime timer;
    private int timeout;

    public SingleShotTimerTask(Robot robot, int timeout)
    {
        super(robot);
        Util.log("timeout=%d", timeout);
        this.timeout = timeout;
    }

    @Override
    public void start()
    {
         Util.log();
         timer = new ElapsedTime();
    }

    @Override
    public boolean timeslice()
    {
        if (timer.time() >= timeout)
        {
            new SingleShotTimerEvent(this, EventKind.EXPIRED).postEvent();

            return true;    // Task is done, will be deleted.
        }
        else
            return false;   // Task needs to keep running.
    }
}
