package com.qualcomm.ftcrobotcontroller.local.lib.taskevent;
/*
 * FTC Team 25: cmacfarl, August 31, 2015
 */

/*
  * This is the Task/Event style programming structure originally created by team 25. This has been
  * extensively modified by R. Corn for OSD teams.
  * Instead of opmodes, you write tasks and add them to the Robot class task queue. It then takes
  * care of running them for you. You would break up your program into tasks and they will be run
  * for you by the Robot base class. You can communicate between the tasks using events. See the
  * sample for an example of how to use this program structure.
  * This structure is an alternative to using threads or having a very large single opmode loop. It
  * simulates running each task in a thread but without using threads. Note that task execution and
  * event processing is sequential.
 */

import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Robot extends OpMode
{
    private ConcurrentLinkedQueue<RobotTask> tasks;
    private ConcurrentLinkedQueue<RobotEvent> events;

    public long loopCount, initLoopCount;

    /**
     * Robot constructor.
     * Classes extending Robot should always call this constructor with super().
     * @throws Exception
     */
    public Robot() throws Exception
    {
        Util.currentOpMode = this;
        Logging.MyLogger.setup();

        tasks = new ConcurrentLinkedQueue<RobotTask>();
        events = new ConcurrentLinkedQueue<RobotEvent>();
    }

    /**
     * Event handling method. Classes extending Robot must implement this method to
     * handle events as appropriate for those classes.
     * @param e
     */
    protected abstract void handleEvent(RobotEvent e);

    public String toString() {return this.getClass().getSimpleName();}

    /**
     * Add a Task to the Task execution queue.
     * @param task Task to add to the queue.
     */
    protected void addTask(RobotTask task)
    {
        Util.log(task.toString());
        tasks.add(task);
        task.start();
    }

    /**
     * Remove a Task from the Task execution queue.
     * @param task Task to remove.
     */
    protected void removeTask(RobotTask task)
    {
        Util.log(task.toString());
        tasks.remove(task);
    }

    /**
     * Add an Event to the Event processing queue.
     * @param event Event to add.
     */
    protected void queueEvent(RobotEvent event) {events.add(event);}

    /**
     * If extending classes wish to implement their own init() method, they should call
     * super.init() or handle the functions performed here as appropriate.
     */
    @Override
    public void init()
    {
        Util.log();

        // These methods cannot be called before the init phase.
        AppUtil.setControllerApp(hardwareMap.appContext);
        Util.log(AppUtil.getVersion());
        AppUtil.logPreferences();
    }

    /**
     * Extending classes implement Tasks with implement the initTimeSlice() method and it is called
     * by this method in turn for each queued. Same for Events. Each event is passed to the Task
     * that queued it for processing and that Task may let the event bubble up to the Robot subclass
     * for processing.
     */
    @Override
    public final void init_loop()
    {
        RobotEvent event;

        initLoopCount++;

        /*
         * This is a straight FIFO queue.  Pull an event off the queue, process it,
         * move on to the next one.
         */
        event = events.poll();

        while (event != null)
        {
            event.handleEvent();

            event = events.poll();
        }

        /*
         * A list of tasks to give timeslices to.  A task remains in the list
         * until it tells the Robot that it is finished (true: I'm done, false: I have
         * more work to do), at which point it is stopped.
         */
        for (RobotTask task : tasks) if (task.initTimeslice()) task.stop();
    }

    @Override
    public final void loop()
    {
        RobotEvent event;

        loopCount++;

        /*
         * This is a straight FIFO queue.  Pull an event off the queue, process it,
         * move on to the next one.
         */
        event = events.poll();

        while (event != null)
        {
            event.handleEvent();
            event = events.poll();
        }

        /*
         * A list of tasks to give timeslices to.  A task remains in the list
         * until it tells the Robot that it is finished (true: I'm done, false: I have
         * more work to do), at which point it is stopped.
         */
        for (RobotTask task : tasks) if (task.timeslice()) task.stop();
    }
}
