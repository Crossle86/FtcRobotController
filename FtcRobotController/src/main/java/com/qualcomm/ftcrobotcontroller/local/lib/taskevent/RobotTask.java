package com.qualcomm.ftcrobotcontroller.local.lib.taskevent;
/*
 * FTC Team 25: cmacfarl, August 31, 2015
 */

import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.Robot;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.RobotEvent;

public abstract class RobotTask
{
    protected Robot robot;

    public RobotTask(Robot robot) {this.robot = robot;}

    public void start() {}

    public void stop() {robot.removeTask(this);}

    public String toString() {return this.getClass().getSimpleName();}

    public void handleEvent(RobotEvent e) {robot.handleEvent(e);}

    /*
     * Perform work for the task. Your class overrides this method to do it's work.
     *
     * The task should return false if there is more work to
     * do, true otherwise.
     */
    public boolean timeslice() {return false;};

    /*
     * Perform work for this task during init phase. Tasks that implement this method must be
     * added to the Task queue during the init() method. Perform init work for the task. Your class
     * overrides this method to do it's init work.
     *
     * The task should return false if there is more work to
     * do, true otherwise.
     */
    public boolean initTimeslice() {return false;};
}
