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

@TeleOp (name = "Glyph Kicker")
public class glyphKicker extends ExplosiveTele {

    private FrameGrabber grabber = FtcRobotControllerActivity.frameGrabber;
    private GlyphResult oldResult;
    private GlyphResult newResult;

    private PID turnPID = new PID();

    private DcMotor bright, fright, bleft, fleft;
    private ModernRoboticsI2cRangeSensor ultra;

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
        oldResult = (GlyphResult) imageProcessorResult.getResult();

        turnPID.setup(0.0125, 0, 0, 0.1, 5, 72);
    }

    @Override
    public void initAction() {
        Log.i("glyphKicker Opmode", "Init action");
    }

    @Override
    public void firstLoop() {
        Log.i("glyphKicker Opmode", "First loop");
    }

    @Override
    public void bodyLoop() {

        Log.i("glyphKicker Opmode", "Begin body loop");
        telemetry.update();

        //while (ultra.getDistance(DistanceUnit.INCH) < 10);

        condition currCond = findCondition();

        if (currCond == condition.INFRAME) {
            double leftMotorSpeed = turnPID.status(newResult.getLoc().x) + 1;
            double rightMotorSpeed = -turnPID.status(newResult.getLoc().x) + 1;

            setPowers(rightMotorSpeed, rightMotorSpeed, leftMotorSpeed, leftMotorSpeed);
        }
        if (currCond == condition.OFFLEFT) {
            setPowers(1, 1, -1, -1);
        }
        if (currCond == condition.OFFRIGHT) {
            setPowers(-1, -1, 1, 1);
        }
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
        newResult = (GlyphResult) imageProcessorResult.getResult();

        if (newResult.getLoc() == null) {
            if (oldResult.getLoc().x > 72) {
                return condition.OFFRIGHT;
            } else {
                return condition.OFFLEFT;
            }
        } else {
            oldResult = newResult;
            return condition.INFRAME;
        }
    }

    private enum condition {
        INFRAME,
        OFFLEFT,
        OFFRIGHT
    }
}
