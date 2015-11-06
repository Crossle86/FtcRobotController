
package com.qualcomm.ftcrobotcontroller.local.opmodes;

import com.qualcomm.ftcrobotcontroller.local.lib.AppUtil;
import com.qualcomm.ftcrobotcontroller.local.lib.Logging;
import com.qualcomm.ftcrobotcontroller.local.lib.Util;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;

//import org.swerverobotics.library.interfaces.Autonomous;

/**
 * TeleOp Mode
 * <p>
 * Enables control of the robot via the gamepad
 */
//@Autonomous(name="Telop Test Program",group="AUTO")
public class TeleOp extends OpMode
{
  public String rich = "globalXX";
  public String rich1 = "global";
  public static String rich2 = "global";

  // hardware declarations
  DcMotorController wheelController, legacyMotorController;
  DcMotorController.DeviceMode devMode;
  DcMotor M_driveRight, LM_driveRight;
  DcMotor M_driveLeft, LM_driveLeft;
  TouchSensor I_touch;
  LightSensor I_light;
  GyroSensor I_gyro;
  Servo S_upDown;
  Servo S_sideSide;
  Servo LM_armServo, LM_otherServo;

  // variable declarations

  // fixed variables
  final float DEAD_ZONE = 0.1f;

  // robot as a whole variables
  int numOpLoops;

  // motor specific variables
  float M_driveRightPower = 0.0f;
  float M_driveLeftPower = 0.0f;

  // servo specific variables

  // arm variables
  double A_upDownServoPos = 0.5d;
  double A_sideSideServoPos = 0.5d;
  double A_armServoPos = 0.5d;
  double A_otherServoPos = 0.5d;

  TestThread  myThread;

  volatile String motordirectionRight = "not set";

  public TeleOp() throws Exception
  {
    rich = "constructor";
    Util.currentOpMode = this;

    Logging.MyLogger.setup();

    Util.log(this.getClass().getName());
  }

  /*
   * Code to run when the op mode is first enabled goes here
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
   */
  @Override
  public void init()
  {
    Util.log();

    AppUtil.setControllerApp(hardwareMap.appContext);

    wheelController = hardwareMap.dcMotorController.get("Motor Controller 2");
    legacyMotorController = hardwareMap.dcMotorController.get("L_motorController");

    M_driveRight = hardwareMap.dcMotor.get("M_driveRight");
    M_driveLeft = hardwareMap.dcMotor.get("M_driveLeft");
    LM_driveRight = hardwareMap.dcMotor.get("L_motor1");
    LM_driveLeft = hardwareMap.dcMotor.get("L_motor2");

    S_upDown = hardwareMap.servo.get("S_upDown");
    S_sideSide = hardwareMap.servo.get("S_sideSide");
    //I_gyro = hardwareMap.gyroSensor.get("I_gyro");
    devMode = DcMotorController.DeviceMode.WRITE_ONLY;
    M_driveRight.setDirection(DcMotor.Direction.REVERSE);
    M_driveRight.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    M_driveLeft.setChannelMode(DcMotorController.RunMode.RESET_ENCODERS);
    M_driveRight.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);
    M_driveLeft.setChannelMode(DcMotorController.RunMode.RUN_WITHOUT_ENCODERS);

    // set servos
    //S_driveRight = hardwareMap.servo.get("servo_1");
    //S_driveLeft = hardwareMap.servo.get("servo_6");

    LM_armServo = hardwareMap.servo.get("LM_armServo");
    LM_otherServo = hardwareMap.servo.get("LM_otherServo");

    // set sensors
    //I_touch = hardwareMap.touchSensor.get("I_touch");
    //I_touch = hardwareMap.touchSensor.get("NXT_touch");
    //I_light = hardwareMap.lightSensor.get("NXT_light");

    // intialize stuff
    gamepad1.setJoystickDeadzone(DEAD_ZONE);
    gamepad2.setJoystickDeadzone(DEAD_ZONE);

    myThread = new TestThread();

    //A_upDownServoPos = 0.1d;
    //A_sideSideServoPos = 0.1d;
    //S_upDown.setDirection(Servo.Direction.REVERSE);
  }

  @Override
  public void start()
  {
    Util.log("start");

    rich1 = "start";

    Util.telemetry("line 09", "start");

    myThread.start();
  }

  @Override
  public void stop()
  {
    Util.log();

    Util.telemetry("line 11", "stop");

    myThread.interrupt();

    M_driveRight.setPower(0);
    M_driveLeft.setPower(0);
  }

  /*
   * This method will be called repeatedly in a loop
   * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
   */
  @Override
  public void loop()
  {
    M_driveRightPower = gamepad1.right_stick_y;
    M_driveLeftPower = gamepad1.left_stick_y;

    if(gamepad1.a && A_upDownServoPos > Servo.MIN_POSITION)
    {
      A_upDownServoPos -= 0.01d;
    }
    else if (gamepad1.b && A_upDownServoPos < Servo.MAX_POSITION)
    {
      A_upDownServoPos += 0.01d;
    }

    if(gamepad1.x && A_sideSideServoPos < Servo.MAX_POSITION)
    {
      A_sideSideServoPos += 0.01d;
    }
    else if(gamepad1.y && A_sideSideServoPos > Servo.MIN_POSITION)
    {
      A_sideSideServoPos -= 0.01d;
    }

    if(gamepad2.a && A_armServoPos > Servo.MIN_POSITION)
    {
      A_armServoPos -= 0.01d;
    }
    else if (gamepad2.b && A_armServoPos < Servo.MAX_POSITION)
    {
      A_armServoPos += 0.01d;
    }

    if(gamepad2.x && A_otherServoPos < Servo.MAX_POSITION)
    {
      A_otherServoPos += 0.01d;
    }
    else if(gamepad2.y && A_otherServoPos > Servo.MIN_POSITION)
    {
      A_otherServoPos -= 0.01d;
    }

    if (gamepad1.y) rich1 = "loop";
    if (gamepad1.x) rich2 = "loop";

    // setting hardware
    //M_driveRight.setPower(M_driveRightPower);
    //M_driveLeft.setPower(M_driveLeftPower);

    if (legacyMotorController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.WRITE_ONLY)
    {
      LM_driveRight.setPower(gamepad2.right_stick_y);
      LM_driveLeft.setPower(gamepad2.left_stick_y);
    }

    S_sideSide.setPosition(Range.clip(A_sideSideServoPos, 0.0, 1.0));
    S_upDown.setPosition(Range.clip(A_upDownServoPos, 0.0, 1.0));

    LM_armServo.setPosition(Range.clip(A_armServoPos, 0.0, 1.0));
    LM_otherServo.setPosition(Range.clip(A_otherServoPos, 0.0, 1.0));

    // Every 17 loops, switch to read mode so we can read data from the NXT device.
    // Only necessary on NXT devices.
    if (numOpLoops % 17 == 0)
    {
      legacyMotorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.READ_ONLY);
    }

    Util.telemetry("1-LeftGP", "LY=%.2f LP=%.2f  RY=%.2f RP=%.2f", gamepad1.left_stick_y, M_driveLeft.getPower(), gamepad1.right_stick_y, M_driveRight.getPower());
    Util.telemetry("2-RightGP", "LY=%.2f RY=%.2f", gamepad2.left_stick_y, gamepad2.right_stick_y);
    //Util.telemetry("3-Legacy", "LP=%d RP=%d", LM_driveLeft.getCurrentPosition(), LM_driveRight.getCurrentPosition());
    //Util.telemetry("Buttons", "t=%b x=%b a=%b b=%b y=%b", I_touch.isPressed(), gamepad1.x, gamepad1.a, gamepad1.b, gamepad1.y);
    Util.telemetry("UpDwn Servo", "Sup=%.2f real=%.2f", A_upDownServoPos, S_upDown.getPosition());
    Util.telemetry("Sidew Servo", "Sup=%.2f real=%.2f", A_sideSideServoPos, S_sideSide.getPosition());
    //Util.telemetry("arm Servo", "Sup=%.2f real=%.2f", A_armServoPos, LM_armServo.getPosition());
    //Util.telemetry("other Servo", "Sup=%.2f real=%.2f", A_otherServoPos, LM_otherServo.getPosition());
    //Util.telemetry("Light", "value=%f", I_light.getLightDetected());
    Util.telemetry("line 05", "rich=%s", rich);
    Util.telemetry("line 06", "rich1=%s", rich1);
    Util.telemetry("line 07", "rich2=%s", rich2);
    Util.telemetry("line 08", "opmode=%s", Util.currentMethod());
    Util.telemetry("line 10", "line 10");
    Util.telemetry("thread", "directionL=%s  R=%s", myThread.motordirectionLeft, motordirectionRight);

    // Read Nxt device and switch back to write mode.
    if (legacyMotorController.getMotorControllerDeviceMode() == DcMotorController.DeviceMode.READ_ONLY)
    {
      Util.telemetry("arm Servo", "Sup=%.2f real=%.2f", A_armServoPos, LM_armServo.getPosition());
      Util.telemetry("other Servo", "Sup=%.2f real=%.2f", A_otherServoPos, LM_otherServo.getPosition());
      Util.telemetry("3-Legacy", "LP=%.2f RP=%.2f", LM_driveLeft.getPower(), LM_driveRight.getPower());

      legacyMotorController.setMotorControllerDeviceMode(DcMotorController.DeviceMode.WRITE_ONLY);

      // Reset the loop
      numOpLoops = 0;
    }

    // Update the current legacy devMode
    //devMode = legacyMotorController.getMotorControllerDeviceMode();
    numOpLoops++;

    //Util.log("end of loop");
  }

  // TestThread thread class.

  private class TestThread extends Thread
  {
    private DcMotor           _driveLeft;
    private DcMotor           _driveRight;
    public  volatile String   motordirectionLeft = "not set";

    TestThread()
    {
      this.setName("TestThread");

      Util.log("%s", this.getName());

      _driveLeft = hardwareMap.dcMotor.get("M_driveLeft");
      _driveRight = hardwareMap.dcMotor.get("M_driveRight");
      _driveRight.setDirection(DcMotor.Direction.REVERSE);
    }

    public void run()
    {
      Util.log("%s", this.getName());
      //Util.telemetry("mode", "thread %s running", this.getName());

      try
      {
        while (!isInterrupted())
        {
          //Util.log("thread loop");
          _driveRight.setPower(gamepad1.right_stick_y);
          M_driveLeft.setPower(gamepad1.left_stick_y);

          if (gamepad1.left_stick_y != 0.0) Util.log("left stick=%f", gamepad1.left_stick_y);

          if (gamepad1.left_stick_y == 0.0) motordirectionLeft = "stopped";
          if (gamepad1.left_stick_y > 0.0) motordirectionLeft = "forward";
          if (gamepad1.left_stick_y < 0.0) motordirectionLeft = "backward";
          if (gamepad1.right_stick_y == 0.0) motordirectionRight = "stopped";
          if (gamepad1.right_stick_y > 0.0) motordirectionRight = "forward";
          if (gamepad1.right_stick_y < 0.0) motordirectionRight = "backward";

          sleep(100);
        }
      }
      catch (InterruptedException e) {Util.log("%s interrupted", this.getName());}
      catch (Throwable e) {e.printStackTrace(Logging.logPrintStream);}

      Util.log("end of %s", this.getName());
    }
  }	// end of TestThread thread class.
}

