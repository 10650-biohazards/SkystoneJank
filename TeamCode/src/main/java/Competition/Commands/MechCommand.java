package Competition.Commands;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

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

    DcMotor intRight, intLeft;

    private Gamepad manip;

    public MechCommand(BiohazardTele op) {
        super(op, "mech");
    }

    @Override
    public void init() {
        intRight = RobotMap.intRight;
        intLeft = RobotMap.intLeft;

        manip = Robot.manipulator;
    }

    @Override
    public void start() {
        intLeft.setPower(0);
        intRight.setPower(0);
    }

    @Override
    public void loop() {
        intake();
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
            intRight.setPower(0.5);
        }
        if (VisionCommand.status == TILTLEFT) {
            Log.e(TAG, "Off course! Tilted to the left!");
            intLeft.setPower(0.5);
            intRight.setPower(1);
        }
    }

    @Override
    public void stop() {

    }
}
