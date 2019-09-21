package Competition.Commands;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardTele;


public class MechCommand extends BioCommand {

    String TAG = "MechCommand";

    DcMotor intRight, intLeft;

    Servo swinger, gripper, hooker;

    private Gamepad manip, driver;

    public MechCommand(BiohazardTele op) {
        super(op, "mech");
    }

    @Override
    public void init() {
        intRight = RobotMap.intRight;
        intLeft = RobotMap.intLeft;

        swinger = RobotMap.swinger;
        gripper = RobotMap.gripper;
        hooker = RobotMap.hooker;

        manip = Robot.manipulator;
        driver = Robot.driver;
    }

    @Override
    public void start() {
        intLeft.setPower(0);
        intRight.setPower(0);
    }

    @Override
    public void loop() {
        intake();
        if (driver.a) {
            autoStack();
        }
    }

    public void autoStack() {
        double stackX = VisionCommand.stackX;
        int width  = VisionCommand.stackWid;

        if (stackX > 93 && stackX < 83 && width < 42) {
            gripper.setPosition(1.0);
        } else {
            gripper.setPosition(0);

        }
    }

    public void intake() {
        /*
        if (VisionCommand.status == NONE) {
            Log.e(TAG, "No stone in sight");
            intLeft.setPower(0);
            intRight.setPower(0);
        }
        if (VisionCommand.status == ONTARGET) {
            Log.e(TAG, "On target!");
            intLeft.setPower(1);
            intRight.setPower(1);
        }
        if (VisionCommand.status == TILTRIGHT) {
            Log.e(TAG, "Off course! Tilted to the right!");
            intLeft.setPower(1);
            intRight.setPower(0.5);
        }
        if (VisionCommand.status == TILTLEFT) {
            Log.e(TAG, "Off course! Tilted to the left!");
            intLeft.setPower(0.5);
            intRight.setPower(1);
        }*/
    }

    @Override
    public void stop() {

    }
}
