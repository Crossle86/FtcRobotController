// Demonstrates simple teleop program using the Robot Task/Event model of program structure.
// Shows motor/servo operation. Shows usage of logging and telemetry utility classes.

package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.*;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.GamepadTask.*;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.SingleShotTimerTask.*;

import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.GamepadTask;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.Robot;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.RobotEvent;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.RobotTask;
import com.qualcomm.ftcrobotcontroller.local.lib.taskevent.SingleShotTimerTask;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;

//import org.swerverobotics.library.interfaces.TeleOp;

// TeleOp Mode
//
//@TeleOp(name="Robot TeleOp Sample",group="TELEOP")

public class RobotTeleOpSample extends Robot
{
    // hardware declarations
    DcMotorController wheelController;
    DcMotorController.DeviceMode devMode;
    DcMotor M_driveRight, M_driveLeft;
    TouchSensor I_touch;
    Servo S_upDown, S_sideSide;
    GamepadTask gpTask, gpTask2;

    // variable declarations
    final float DEAD_ZONE = 0.1f;

    float M_driveRightPower = 0.0f, M_driveLeftPower = 0.0f;

    double A_upDownServoPos = 0.5d, A_sideSideServoPos = 0.5d;

    private static boolean oneTime;

    // class constructor
    public RobotTeleOpSample() throws Exception
    {
        super();

        Util.log();
    }

    //
    // Code to run when the op mode is first enabled goes here
    // @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
    //
    @Override
    public void init()
    {
        super.init();

        Util.log();

        wheelController = hardwareMap.dcMotorController.get("Motor Controller 2");
        M_driveRight = hardwareMap.dcMotor.get("M_driveRight");
        M_driveLeft = hardwareMap.dcMotor.get("M_driveLeft");
        S_upDown = hardwareMap.servo.get("S_upDown");
        S_sideSide = hardwareMap.servo.get("S_sideSide");
        devMode = DcMotorController.DeviceMode.WRITE_ONLY;
        M_driveRight.setDirection(DcMotor.Direction.REVERSE);
        M_driveRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        M_driveLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
        M_driveRight.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
        M_driveLeft.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

        I_touch = hardwareMap.touchSensor.get("I_touch");

        gamepad1.setJoystickDeadzone(DEAD_ZONE);

        // note that preferences created by a list widget always store their values as strings so
        // if the value list for the list widget has numbers, you have to read them as string and
        // convert. Other preferences appear to be stored as their data type and have to be read
        // with the correct getXPreference function.
        String autoProgram = AppUtil.getStringPreference("AutoProgram", "0");
        int autoProgramInt = Integer.valueOf(AppUtil.getStringPreference("AutoProgram", "0"));
        Util.log("autoProgram=%s, %d", autoProgram, autoProgramInt);

        RobotTask task = new TelemetryTask(this);
        addTask(task);

        // Demonstrate using enum navigation functions.
        GamepadTask.EventKind gpek = GamepadTask.EventKind.first();

        for(int i = 0; i < GamepadTask.EventKind.count(); i++)
        {
            Util.log(gpek.toString() + " " + GamepadTask.EventKind.get(i).toString() + " " + GamepadTask.EventKind.list.get(i));
            gpek = gpek.next();
        }

        for (GamepadTask.EventKind gpek1 : GamepadTask.EventKind.list)
        {
            Util.log(gpek1.toString());
        }
    }

    // called when period start button is pressed.
    @Override
    public void start()
    {
        Util.log();

        RobotTask task = new SingleShotTimerTask(this, 20);
        addTask(task);

        gpTask = new GamepadTask(this, GamepadTask.GamePadNumber.GAMEPAD1);
        addTask(gpTask);

        gpTask2 = new GamepadTask(this, GamepadTask.GamePadNumber.GAMEPAD2);
        addTask(gpTask2);

        task = new DrivingTask(this);
        addTask(task);
    }

    // called when period stop button is pressed.
    @Override
    public void stop()
    {
        Util.log();

        M_driveRight.setPower(0);
        M_driveLeft.setPower(0);
    }

    // This is a global or Robot level event handler. Events can be handled at the Task level or
    // the Robot level.
    @Override
    public void handleEvent(RobotEvent e)
    {
        Util.log("event handled at Robot level: %s", e.toString());

        // See what event we have and handle as needed.

        if (e instanceof SingleShotTimerEvent) handleTimerEvent((SingleShotTimerEvent) e);

        if (e instanceof GamepadEvent) handleGPEvent((GamepadEvent) e);
    }

    private void handleTimerEvent(SingleShotTimerEvent e)
    {
        Util.log();
    }

    private void handleGPEvent(GamepadEvent e)
    {
        Util.log();

        if (e.padNumber == GamePadNumber.GAMEPAD1 && e.kind == GamepadTask.EventKind.BUTTON_X_DOWN) Util.log("Pad1 Button X down event");
        if (e.padNumber == GamePadNumber.GAMEPAD2 && e.kind == GamepadTask.EventKind.BUTTON_X_DOWN) Util.log("Pad2 Button X down event");
    }

    // Tasks can be defined in-line as we see here or in separate .java source files.
    public class DrivingTask extends RobotTask
    {
        public DrivingTask(Robot robot)
        {
            super(robot);
            Util.log();
        }

        public boolean timeslice()
        {
            M_driveRightPower = gamepad1.right_stick_y;
            M_driveLeftPower = gamepad1.left_stick_y;

            M_driveRight.setPower(M_driveRightPower);
            M_driveLeft.setPower(M_driveLeftPower);

            if(gpTask.buttonState.a_pressed && A_upDownServoPos > Servo.MIN_POSITION)
                A_upDownServoPos -= 0.01d;
            else if (gpTask.buttonState.b_pressed && A_upDownServoPos < Servo.MAX_POSITION)
                A_upDownServoPos += 0.01d;

            if(gamepad1.x && A_sideSideServoPos < Servo.MAX_POSITION)
                A_sideSideServoPos += 0.01d;
            else if(gamepad1.y && A_sideSideServoPos > Servo.MIN_POSITION)
                A_sideSideServoPos -= 0.01d;

            S_sideSide.setPosition(Range.clip(A_sideSideServoPos, 0.0, 1.0));
            S_upDown.setPosition(Range.clip(A_upDownServoPos, 0.0, 1.0));

            return false;
        }
    }

    // Tasks can be defined in-line as we see here or in separate .java source files.
    public class TelemetryTask extends RobotTask
    {
        public TelemetryTask(Robot robot)
        {
            super(robot);
            Util.log();
        }

        public class TelemetryEvent extends RobotEvent
        {
            String tag, message;

            public TelemetryEvent(TelemetryTask task, String tag, String message)
            {
                super(task);
                this.tag = tag;
                this.message = message;
            }

            @Override
            public String toString() {return (super.toString() + ": " + tag + ": " + message);}
        }

        // This is a Task level event handler.
        @Override
        public void handleEvent(RobotEvent e)
        {
            //Util.log("event handled at task level: %s", e.toString());

            Util.telemetry(((TelemetryEvent) e).tag, ((TelemetryEvent) e).message);
        }

        // Here we do whatever we need during the init phase. This method is called repeatedly
        // until init phase ends or we return true to end the task.
        @Override
        public boolean initTimeslice()
        {
            new TelemetryEvent(this, "1-in init phase", String.format("loop=%d", initLoopCount)).postEvent();

            return false;
        }

        // Here we do whatever we need during the run phase. This method is called repeatedly
        // until run phase ends or we return true to end the task.
        @Override
        public boolean timeslice()
        {
            // display telemetry on DS. line labels are sorted.
            Util.telemetry("1-LeftGP", "LY=%.2f LP=%.2f  RY=%.2f RP=%.2f", gamepad1.left_stick_y, M_driveLeftPower, gamepad1.right_stick_y, M_driveRightPower);
            //Util.telemetry("Buttons", "x=%b a=%b b=%b y=%b", gamepad1.x, gamepad1.a, gamepad1.b, gamepad1.y);
            Util.telemetry("Buttons", "t=%b x=%b a=%b b=%b y=%b", I_touch.isPressed(), gamepad1.x, gamepad1.a, gamepad1.b, gamepad1.y);
            Util.telemetry("UpDwn Servo", "Sup=%.2f real=%.2f", A_upDownServoPos, S_upDown.getPosition());
            Util.telemetry("Sidew Servo", "Sup=%.2f real=%.2f", A_sideSideServoPos, S_sideSide.getPosition());

            new TelemetryEvent(this, "Z-Loop", String.format("loop=%d", loopCount)).postEvent();

            return false;
        }
    }
}

