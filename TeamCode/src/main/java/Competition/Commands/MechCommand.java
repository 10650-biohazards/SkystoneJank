package Competition.Commands;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import Utilities.PID;

import Competition.Robot;
import Competition.RobotMap;
import FtcExplosivesPackage.BioCommand;
import FtcExplosivesPackage.BiohazardTele;

import static Competition.Commands.VisionCommand.stoneStatus.NONE;
import static Competition.Commands.VisionCommand.stoneStatus.ONTARGET;
import static Competition.Commands.VisionCommand.stoneStatus.TILTLEFT;
import static Competition.Commands.VisionCommand.stoneStatus.TILTRIGHT;




public class MechCommand extends BioCommand {

    String TAG = "MechCommand";

    DcMotor intRight, intLeft, lift;

    Servo swinger, gripper, hooker;

    PID liftPID;

    private double liftPos;

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
        lift = RobotMap.lift;

        manip = Robot.manipulator;
        driver = Robot.driver;

        liftPos = lift.getCurrentPosition();
        liftPID.setup (42,0,42,0,0,liftPos);
    }

    @Override
    public void start() {
        intLeft.setPower(0);
        intRight.setPower(0);
    }

    @Override
    public void loop() {

        cairrage();
        //intake();
        if (driver.a) {
            //autoStack();
        }
        hooker();
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

    public void cairrage() {
        if (manip.x) {
            RobotMap.gripper.setPosition(0.7);
        } else {
            RobotMap.gripper.setPosition(0.35);
        }

    }

    public void intake() {

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
            intRight.setPower(0.8);
        }
        if (VisionCommand.status == TILTLEFT) {
            Log.e(TAG, "Off course! Tilted to the left!");
            intLeft.setPower(0.8);
            intRight.setPower(1);
        }
    }

    public void hooker() {
        if (manip.y) {
            hooker.setPosition(0.5);
        } else {
            hooker.setPosition(1);
        }
    }

    public void lift() {

        if(manip.right_stick_y > 0.05 || manip.right_stick_y < -0.05)
        {

            liftPos -= manip.right_stick_y;
            liftPID.adjTarg(liftPos);

        }

        lift.setPower(liftPID.status(lift.getCurrentPosition()));

    }

    @Override
    public void stop() {

    }
}