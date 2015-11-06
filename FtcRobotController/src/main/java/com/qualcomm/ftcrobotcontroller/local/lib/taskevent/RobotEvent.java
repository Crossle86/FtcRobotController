package com.qualcomm.ftcrobotcontroller.local.lib.taskevent;
/*
 * FTC Team 25: cmacfarl, August 31, 2015
 */

public abstract class RobotEvent
{
    /*
     * The task this event is associated with
     */
    protected RobotTask task;

    /**
     * Classes subclassing RobotEvent should call super() in their constructor or handle setting
     * the calling task themselves.
     * @param task
     */
    public RobotEvent(RobotTask task) {this.task = task;}

    public final void handleEvent() {task.handleEvent(this);}

    public final void postEvent() {task.robot.queueEvent(this);}

    public String toString() {return this.getClass().getSimpleName();}
}
