package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import FtcExplosivesPackage.ExplosiveTele;
import Gagarin.GagarinRobot;
import Utilities.PID;
import ftc.vision.FrameGrabber;
import ftc.vision.Glyph.GlyphResult;
import ftc.vision.ImageProcessorResult;
import ftc.vision.judgeDay.trackResult;

@TeleOp (name = "Top Secret")
public class follow extends ExplosiveTele {
    String TAG = "stalker";

    condition currCond = condition.UNREGISTERED;

    private FrameGrabber grabber = FtcRobotControllerActivity.frameGrabber;
    private trackResult oldResult;
    private trackResult newResult;
    private condition lastResult;

    private PID turnPID = new PID();

    private DcMotor bright, fright, bleft, fleft;

    ModernRoboticsI2cRangeSensor ultra;

    @Override
    public void initHardware() {
        GagarinRobot robot = new GagarinRobot(this, hardwareMap);
        bright = robot.bright;
        fright = robot.fright;
        bleft = robot.bleft;
        fleft = robot.fleft;

        ultra = robot.ultra;

        grabber.grabSingleFrame();

        while (!grabber.isResultReady());

        ImageProcessorResult imageProcessorResult = grabber.getResult();
        oldResult = (trackResult) imageProcessorResult.getResult();

        turnPID.setup(0.0125, 0, 0, 0.1, 5, 72);
    }

    @Override
    public void initAction() {
        Log.i(TAG, "Init action");
    }

    @Override
    public void firstLoop() {
        Log.i(TAG, "First loop");
    }

    @Override
    public void bodyLoop() {

        Log.i(TAG, "Begin body loop");


        /*while (ultra.getDistance(DistanceUnit.INCH) < 10) {
            setPowers(0, 0, 0, 0);
        };*/

        currCond = findCondition();

        double mod;


        if (newResult.screenLoc != null) {
            mod = turnPID.status(newResult.screenLoc.x);
        } else {
            mod = 42;
        }

        if (currCond == condition.ONTARGET) {
            setPowers(1, -1, 1, -1);
        }
        if (currCond == condition.OFFLEFT) {
            setPowers(1, -1, -1, 1);
        }
        if (currCond == condition.OFFRIGHT) {
            setPowers(-1, 1, 1, -1);
        }
        if (currCond == condition.SLIGHTLEFT) {
            setPowers(1, -1, 0, 0);
        }
        if (currCond == condition.SLIGHTRIGHT) {
            setPowers(0, 0, 1, -1);
        }


        //FUTURE SMOOTHER VERSION
        /*
        if (newResult.screenLoc != null) {
            double mod = turnPID.status(newResult.screenLoc.x);
        }
        if (currCond == condition.ONTARGET) {
            setPowers(1, -1, 1, -1);
        }
        if (currCond == condition.OFFLEFT) {
            setPowers(1, -1, -1, 1);
        }
        if (currCond == condition.OFFRIGHT) {
            setPowers(-1, 1, 1, -1);
        }
        if (currCond == condition.SLIGHTLEFT) {
            setPowers(1, -1, 0, 0);
        }
        if (currCond == condition.SLIGHTRIGHT) {
            setPowers(0, 0, 1, -1);
        }
        */


        telemetry.addData("Status", currCond);
        telemetry.addData("Modifier", mod);
        telemetry.update();
    }

    @Override
    public void exit() {
        setPowers(0, 0, 0, 0);
    }

    public void setPowers(double br, double fr, double bl, double fl) {
        bright.setPower(br);
        fright.setPower(fr);
        bleft.setPower(bl);
        fleft.setPower(fl);
    }

    public condition findCondition() {
        grabber.grabSingleFrame();
        while (!grabber.isResultReady());

        ImageProcessorResult imageProcessorResult = grabber.getResult();
        newResult = (trackResult) imageProcessorResult.getResult();

        if (newResult.screenLoc == null) {
            if (oldResult.screenLoc.x > 72) {
                return condition.OFFRIGHT;
            } else {
                return condition.OFFLEFT;
            }
        } else {
            oldResult = newResult;

            /*if (currCond == condition.OFFRIGHT) {
                if (newResult.screenLoc.x > 130) {
                    return condition.SLIGHTRIGHT;
                }
            }*/
            if (newResult.screenLoc.x > 80) {
                return condition.SLIGHTRIGHT;
            } else if (newResult.screenLoc.x < 65) {
                return condition.SLIGHTLEFT;
            } else {
                return condition.ONTARGET;
            }
        }
    }

    private enum condition {
        ONTARGET,
        OFFLEFT,
        OFFRIGHT,
        SLIGHTRIGHT,
        SLIGHTLEFT,
        UNREGISTERED
    }
}
