package com.qualcomm.ftcrobotcontroller.local.lib.taskevent;

/*
 * FTC Team 25: cmacfarl, August 31, 2015
 */

import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.Arrays;
import java.util.List;

public class GamepadTask extends RobotTask
{
    public final GamePadNumber gamePadNum;

    public enum GamePadNumber
    {
        GAMEPAD1,
        GAMEPAD2
    }

    public enum EventKind
    {
        BUTTON_A_DOWN,
        BUTTON_A_UP,
        BUTTON_B_DOWN,
        BUTTON_B_UP,
        BUTTON_X_DOWN,
        BUTTON_X_UP,
        BUTTON_Y_DOWN,
        BUTTON_Y_UP;

        // Generic enum navigation functions.
        public static List<EventKind> list = Arrays.asList(values());
        public static int count() {return list.size();}
        public static EventKind get(int index) {return list.get(index);}
        public static EventKind first() {return list.get(0);}
        public EventKind next() {return list.get((this.ordinal() + 1) % list.size());}
        public EventKind prev() {return list.get((this.ordinal() -1 + list.size()) % list.size());}
    }

    public class GamepadEvent extends RobotEvent
    {
        public final GamePadNumber    padNumber;
        public final EventKind        kind;

        public GamepadEvent(GamepadTask task, EventKind k)
        {
            super(task);
            padNumber =  task.gamePadNum;
            kind = k;
        }

        @Override
        public String toString()
        {
            return (super.toString() + ": " + padNumber + ";" + kind);
        }
    }

    public class ButtonState
    {
        public boolean a_pressed;
        public boolean b_pressed;
        public boolean x_pressed;
        public boolean y_pressed;
    }

    public ButtonState buttonState;

    public GamepadTask(Robot robot, GamePadNumber gamePadNum)
    {
        super(robot);

        Util.log(gamePadNum.toString());

        this.gamePadNum = gamePadNum;

        this.buttonState = new ButtonState();
        this.buttonState.a_pressed = false;
        this.buttonState.b_pressed = false;
        this.buttonState.x_pressed = false;
        this.buttonState.y_pressed = false;
    }

    /*
     * Process gamepad actions and send them to the robot as events.
     *
     * Note that these are not state changes, but is designed to send a
     * continual stream of events as long as the button is pressed (hmmm,
     * this may not be a good idea if software can't keep up).
     */
    @Override
    public boolean timeslice()
    {
        Gamepad gamepad = null;

        /*
         * I thought Java passed objects by reference, but oddly enough if you cache
         * the gamepad in the task's contstructor, it will never update.  Hence this.
         * This is a bug in the SDK. Will be fixed at some point.
         */

        if (gamePadNum == GamePadNumber.GAMEPAD1) gamepad = robot.gamepad1;
        if (gamePadNum == GamePadNumber.GAMEPAD2) gamepad = robot.gamepad2;

        // Raise event on button state transition.

        if (gamepad.a && !buttonState.a_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_A_DOWN).postEvent();
            buttonState.a_pressed = true;
        }
        else if (!gamepad.a && buttonState.a_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_A_UP).postEvent();
            buttonState.a_pressed = false;
        }

        if (gamepad.b && !buttonState.b_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_B_DOWN).postEvent();
            buttonState.b_pressed = true;
        }
        else if (!gamepad.b && buttonState.b_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_B_UP).postEvent();
            buttonState.b_pressed = false;
        }

        if (gamepad.x && !buttonState.x_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_X_DOWN).postEvent();
            buttonState.x_pressed = true;
        }
        else if (!gamepad.x && buttonState.x_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_X_UP).postEvent();
            buttonState.x_pressed = false;
        }

        if (gamepad.y && !buttonState.y_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_Y_DOWN).postEvent();
            buttonState.y_pressed = true;
        }
        else if (!gamepad.y && buttonState.y_pressed)
        {
            new GamepadEvent(this, EventKind.BUTTON_Y_UP).postEvent();
            buttonState.y_pressed = false;
        }

        /*
         * This task lives forever.
         */
        return false;
    }
}
